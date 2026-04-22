package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.messaging.StompCreditRequestSender
import itmo.blps.citilink.models.*
import itmo.blps.citilink.repositories.CreditApplicationRepository
import itmo.blps.citilink.repositories.OrderItemRepository
import itmo.blps.citilink.services.warehouse.WarehouseJcaService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Profile("shop")
@Service
class CreditServiceImpl(
    private val creditApplicationRepository: CreditApplicationRepository,
    private val warehouseJcaService: WarehouseJcaService,
    private val orderItemRepository: OrderItemRepository,
    private val stompCreditRequestSender: StompCreditRequestSender
) : CreditService {

    @Transactional(readOnly = true)
    override fun getCreditApplication(applicationId: Long, username: String): CreditApplication {
        val application = creditApplicationRepository.findCreditApplicationsById(applicationId)
            ?: throw EntityNotFoundException("CreditApplication with id $applicationId not found")

        if (application.order.username != username) throw AccessDeniedException("You cannot access orders of another user")

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
        stompCreditRequestSender.sendApplicationId(application.id)
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
        val savedApplication = creditApplicationRepository.save(application)
        // получаем список товаров через репозиторий
        val items = orderItemRepository.findAllByOrder(application.order)

        items.forEach { item ->
            warehouseJcaService.reserveProduct(
                orderId = application.order.id.toString(),
                productId = item.product.id!!,
                quantity = item.quantity
            )
        }

        return savedApplication
    }

    @Transactional
    override fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus) {
        creditApplication.status = status
        creditApplicationRepository.save(creditApplication)
    }

    @Transactional
    override fun selectOffer(creditApplication: CreditApplication, offer: CreditOffer) {
        creditApplication.selectedOffer = offer

        if (offer.isOnlineSigningAvailable) creditApplication.status = ApplicationStatus.PENDING_SIGNATURE
        else creditApplication.status = ApplicationStatus.WAITING_FOR_OPERATOR

        creditApplicationRepository.save(creditApplication)
    }

    @Transactional
    override fun signApplication(application: CreditApplication) {
        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING
        creditApplicationRepository.save(application)
        val items = orderItemRepository.findAllByOrder(application.order)

        items.forEach { item ->
            warehouseJcaService.reserveProduct(
                orderId = application.order.id.toString(),
                productId = item.product.id!!,
                quantity = item.quantity
            )
        }
    }
}
