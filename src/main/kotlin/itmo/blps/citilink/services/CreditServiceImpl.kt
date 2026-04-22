package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.models.*
import itmo.blps.citilink.repositories.CreditApplicationRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Profile("shop")
@Service
class CreditServiceImpl(
    private val creditApplicationRepository: CreditApplicationRepository
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
                initialPayment = request.initialPayment,
                passportSeries = request.passportSeries,
                passportNumber = request.passportNumber,
                email = request.email,
                phone = request.phone
            )
        )
        application.status = ApplicationStatus.WAITING_FOR_BANKS
//        bankService.generateOffers(application)
        return application
    }

    @Transactional(readOnly = true)
    override fun getApplicationsForOperator(): List<CreditApplication> {
        return creditApplicationRepository.findAllByStatus(ApplicationStatus.WAITING_FOR_OPERATOR)
    }

    @Transactional
    override fun approveOfflineSigning(applicationId: Long): CreditApplication {
        val application = creditApplicationRepository.findById(applicationId)
            .orElseThrow { EntityNotFoundException("CreditApplication with id $applicationId not found") }

        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING
        return creditApplicationRepository.save(application)
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
    override fun signApplication(creditApplication: CreditApplication) {
        val selectedOffer = creditApplication.selectedOffer
            ?: throw IllegalStateException("No offer selected for this application. Please select an offer first.")

        if (!selectedOffer.isOnlineSigningAvailable)
            throw IllegalStateException("Online signing is not available for this offer")

        if (creditApplication.status != ApplicationStatus.PENDING_SIGNATURE)
            throw IllegalStateException("This application has already been signed.")

        creditApplication.status = ApplicationStatus.SIGNED
        creditApplication.order.status = OrderStatus.PROCESSING
        creditApplicationRepository.save(creditApplication)
    }

}
