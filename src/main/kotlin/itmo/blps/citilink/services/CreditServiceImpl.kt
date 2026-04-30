package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.messaging.StompCreditRequestSender
import itmo.blps.citilink.models.*
import itmo.blps.citilink.repositories.CreditApplicationRepository
import itmo.blps.warehouse.WarehouseConnection
import itmo.blps.warehouse.WarehouseConnectionFactory
import jakarta.annotation.Resource
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Profile("shop")
@Service
class CreditServiceImpl(
    private val creditApplicationRepository: CreditApplicationRepository,
    private val stompCreditRequestSender: StompCreditRequestSender
) : CreditService {

    @Resource(mappedName = "java:/eis/WarehouseConnector")
    private lateinit var jiraFactory: WarehouseConnectionFactory

    @Transactional(readOnly = true)
    override fun getCreditApplication(applicationId: Long, username: String): CreditApplication {
        val application = creditApplicationRepository.findCreditApplicationsById(applicationId)
            ?: throw EntityNotFoundException("CreditApplication not found")

        if (application.order.username != username) throw AccessDeniedException("Access denied")
        return application
    }

    /**
     * Соответствует блоку "Транзакция" на схеме.
     * Реализует логику: Сохранение -> Запрос в склад -> Откат при ошибке
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun process(request: CreditApplicationRequest, order: Order): CreditApplication {
        var jiraTicketKey: String? = null

        try {
            // 1. Блок: "Запрос на сохранение нового заказа" (черновик в БД)
            val application = creditApplicationRepository.save(
                CreditApplication(
                    order = order,
                    termMonths = request.termMonths,
                    initialPayment = request.initialPayment ?: 0.0,
                    passportSeries = request.passportSeries,
                    passportNumber = request.passportNumber,
                    email = request.email,
                    phone = request.phone
                )
            )

            // запрос в склад, возвращаем ключ тикета
            jiraTicketKey = reserveInWarehouseAndGetKey(application)

            // запрос на сохранение новой заявки
            application.status = ApplicationStatus.WAITING_FOR_BANKS
            val finalSavedApp = creditApplicationRepository.save(application)

            // отправка в activeMQ
            stompCreditRequestSender.sendApplicationId(finalSavedApp.id!!)

            return finalSavedApp

        } catch (e: Exception) {
            // если что-то пошло не так, postgres откатится из-за @Transactional, Jira откатываем вручную
            if (jiraTicketKey != null) {
                println("ROLLBACK: Deleting Jira issue $jiraTicketKey due to: ${e.message}")
                deleteWarehouseReservation(jiraTicketKey)
            }

            // проброс ошибки дальше, чтобы Spring увидел её и завершил Rollback в БД
            throw e
        }
    }

    private fun reserveInWarehouseAndGetKey(app: CreditApplication): String {
        val connection = jiraFactory.connection as WarehouseConnection
        try {
            println("JCA Warehouse: attempt to reserve for order ${app.order.id}")
            return connection.createJiraIssue(
                "Бронь: заказ №${app.order.id}",
                "Резерв товара для клиента ${app.email}. Заявка №${app.id}"
            )
        } finally {
            connection.close()
        }
    }

    private fun deleteWarehouseReservation(key: String) {
        try {
            val connection = jiraFactory.connection as WarehouseConnection
            connection.deleteJiraIssue(key)
            connection.close()
        } catch (e: Exception) {
            println("CRITICAL: Failed to compensate (delete) Jira ticket $key: ${e.message}")
        }
    }


    @Transactional
    override fun approveOfflineSigning(applicationId: Long): CreditApplication {
        val application = creditApplicationRepository.findById(applicationId)
            .orElseThrow { EntityNotFoundException("Application not found") }

        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING

        sendSafeNotification(
            "Заявка №${application.id} одобрена оператором",
            "Требуется выгрузка со склада."
        )

        return creditApplicationRepository.save(application)
    }

    @Transactional
    override fun signApplication(application: CreditApplication) {
        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING
        val saved = creditApplicationRepository.save(application)

        sendSafeNotification(
            "Нужно приступить к сборке заказа ${saved.order.id}",
            "Кредит оформлен. Передано в доставку."
        )
    }

    @Transactional
    override fun selectOffer(creditApplication: CreditApplication, offer: CreditOffer) {
        creditApplication.selectedOffer = offer
        if (offer.isOnlineSigningAvailable) {
            creditApplication.status = ApplicationStatus.PENDING_SIGNATURE
        } else {
            creditApplication.status = ApplicationStatus.WAITING_FOR_OPERATOR
            sendSafeNotification("Нужна помощь оператора (Заявка №${creditApplication.id})", "Офлайн.")
        }
        creditApplicationRepository.save(creditApplication)
    }

    @Transactional(readOnly = true)
    override fun getApplicationsForOperator(): List<CreditApplication> =
        creditApplicationRepository.findAllByStatus(ApplicationStatus.WAITING_FOR_OPERATOR)

    @Transactional
    override fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus) {
        creditApplication.status = status
        creditApplicationRepository.save(creditApplication)
    }

    private fun sendSafeNotification(summary: String, description: String) {
        try {
            val connection = jiraFactory.connection as WarehouseConnection
            connection.createJiraIssue(summary, description)
            connection.close()
        } catch (e: Exception) {
            println("NON-CRITICAL JIRA NOTIFY FAILED: ${e.message}")
        }
    }
}