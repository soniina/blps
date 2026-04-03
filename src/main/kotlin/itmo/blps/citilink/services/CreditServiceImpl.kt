package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.models.*
import itmo.blps.citilink.repositories.CreditApplicationRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
//import org.springframework.security.access.AccessDeniedException

@Service
class CreditServiceImpl(
    private val creditApplicationRepository: CreditApplicationRepository,
    private val bankService: BankService,
    private val userService: UserService
) : CreditService {

    override fun getCreditApplication(applicationId: Long, user: User): CreditApplication {
        val application = creditApplicationRepository.findCreditApplicationsById(applicationId) ?: throw EntityNotFoundException("CreditApplication with id $applicationId not found")

//        if (application.order.user.id != user.id) throw AccessDeniedException("Access denied")

        return application
    }

    override fun process(request: CreditApplicationRequest, order: Order): CreditApplication {
        val application = creditApplicationRepository.save(
            CreditApplication(
                order = order,
                termMonths = request.termMonths!!,
                initialPayment = request.initialPayment!!,
                passportSeries = request.passportSeries!!,
                passportNumber = request.passportNumber!!,
                email = request.email!!,
                phone = request.phone!!
            )
        )
        bankService.generateOffers(application)
        return application
    }


    override fun getApplicationsForOperator(): List<CreditApplication> {
        return creditApplicationRepository.findAllByStatus(ApplicationStatus.WAITING_FOR_OPERATOR)
    }

    @Transactional
    override fun approveOfflineSigning(applicationId: Long) {
        val application = getCreditApplication(applicationId) ?: return

        application.status = ApplicationStatus.SIGNED
        application.order.status = OrderStatus.PROCESSING
        creditApplicationRepository.save(application)
    }

    override fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus) {
        creditApplication.status = status
        creditApplicationRepository.save(creditApplication)
    }

    override fun selectOffer(creditApplication: CreditApplication, selectedOffer: CreditOffer) {
        creditApplication.selectedOffer = selectedOffer
        creditApplicationRepository.save(creditApplication)
    }

    @Transactional
    override fun signApplication(creditApplication: CreditApplication) {
        creditApplication.status = ApplicationStatus.SIGNED
        creditApplication.order.status = OrderStatus.PROCESSING
        creditApplicationRepository.save(creditApplication)
    }

}
