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

    // инъекция внешнего ресурса (JCA адаптера из JNDI сервера WildFly)
    @Resource(mappedName = "java:/eis/WarehouseConnector")
    private lateinit var jiraFactory: WarehouseConnectionFactory

    @Transactional(readOnly = true)
    override fun getCreditApplication(applicationId: Long, username: String): CreditApplication {
        val application = creditApplicationRepository.findCreditApplicationsById(applicationId)
            ?: throw EntityNotFoundException("CreditApplication not found")

        if (application.order.username != username) throw AccessDeniedException("Access denied")
        return application
    }

    @Transactional
    override fun process(request: CreditApplicationRequest, order: Order): CreditApplication {
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
        application.status = ApplicationStatus.WAITING_FOR_BANKS

        // уведомление Jira о создании новой заявки через JCA
        sendJiraNotification(
            "Новая заявка на кредит №${application.id}",
            "Клиент ${application.email} ожидает ответа от банков. Сумма заказа: ${order.totalAmount}"
        )

        // синхронная отправка в ActiveMQ
        stompCreditRequestSender.sendApplicationId(application.id!!)

        return application
    }

    @Transactional(readOnly = true)
    override fun getApplicationsForOperator(): List<CreditApplication> {
        return creditApplicationRepository.findAllByStatus(ApplicationStatus.WAITING_FOR_OPERATOR)
    }

    @Transactional
    override fun approveOfflineSigning(applicationId: Long): CreditApplication {
        val application = creditApplicationRepository.findById(applicationId)
            .orElseThrow { EntityNotFoundException("Application not found") }

        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING

        // уведомление Jira о ручном одобрении оператором
        sendJiraNotification(
            "Заявка №${application.id} одобрена оператором",
            "Требуется выгрузка товара со склада."
        )

        return creditApplicationRepository.save(application)
    }

    @Transactional
    override fun signApplication(application: CreditApplication) {
        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING

        val saved = creditApplicationRepository.save(application)

        // финальное уведомление в Jira
        sendJiraNotification(
            "Заявка №${saved.id} полностью подписана",
            "Кредит оформлен. Передано в службу доставки."
        )
    }

    @Transactional
    override fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus) {
        creditApplication.status = status
        creditApplicationRepository.save(creditApplication)
    }

    @Transactional
    override fun selectOffer(creditApplication: CreditApplication, offer: CreditOffer) {
        creditApplication.selectedOffer = offer

        if (offer.isOnlineSigningAvailable) {
            creditApplication.status = ApplicationStatus.PENDING_SIGNATURE
        } else {
            creditApplication.status = ApplicationStatus.WAITING_FOR_OPERATOR
            // тикет на помощь оператора
            sendJiraNotification("Нужна помощь оператора (Заявка №${creditApplication.id})", "Офлайн подписание.")
        }

        creditApplicationRepository.save(creditApplication)
    }

    /**
     * Вспомогательный метод для взаимодействия с JCA адаптером
     */
    private fun sendJiraNotification(summary: String, description: String) {
        try {
            // 1. Получаем соединение из пула WildFly
            val connection = jiraFactory.connection as WarehouseConnection

            // 2. Вызываем метод нашего адаптера (JCA Driver)
            // Убедись, что этот метод реализован в твоем WarehouseConnection классе в RAR модуле!
            connection.createJiraIssue(summary, description)

            // 3. Закрываем соединение (возвращаем в пул)
            connection.close()
        } catch (e: Exception) {
            // Важно: ошибку логируем, но не бросаем дальше, чтобы не откатывать основную транзакцию БД
            // из-за проблем со связью с Jira (если только это не критично по ТЗ)
            println("FAILED TO NOTIFY JIRA: ${e.message}")
        }
    }
}