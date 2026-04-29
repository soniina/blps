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

    @Transactional(rollbackFor = [Exception::class]) // Откатываем БД при ошибке в Jira
    override fun process(request: CreditApplicationRequest, order: Order): CreditApplication {
        // 1. Блок: "Запрос на сохранение нового заказа"
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

        // "Запрос в склад" (Jira JCA)
        // Если тут упадет исключение (например, Jira 400), Spring сделает ROLLBACK для записи выше
        reserveInWarehouse(application)

        // "Достаточно ли товара" = ДА
        application.status = ApplicationStatus.WAITING_FOR_BANKS
        val finalSavedApp = creditApplicationRepository.save(application)

        // переход к асинхронной части (ActiveMQ)
        stompCreditRequestSender.sendApplicationId(finalSavedApp.id!!)

        return finalSavedApp
    }

    /**
     * Блокирующий метод вызова JCA. Бросает исключение при ошибке.
     */
    private fun reserveInWarehouse(app: CreditApplication) {
        val connection = jiraFactory.connection as WarehouseConnection
        try {
            println("JCA Warehouse: attempt to reserve the item for order ${app.order.id}")
            connection.createJiraIssue(
                "БРОНЬ: Заказ №${app.order.id}",
                "Новая заявка на кредит №${app.id}. Клиент: ${app.email}. Требуется резерв товара."
            )
            println("JCA Warehouse: successfully booked via JCA connector")
        } finally {
            connection.close() // Всегда закрываем соединение
        }
    }

    // остальные методы используют неблокирующие уведомления

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
            "Заявка №${saved.id} подписана",
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

    /**
     * Вспомогательный метод для статусов, где падение Jira не должно прерывать работу
     */
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