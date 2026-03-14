package itmo.blps.citilink.services

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.OrderStatus
import itmo.blps.citilink.repositories.CreditApplicationRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreditServiceImpl(private val creditApplicationRepository: CreditApplicationRepository, private val bankService: BankService) :
    CreditService {

    override fun getCreditApplicationByUid(applicationUid: UUID): CreditApplication? = creditApplicationRepository.findCreditApplicationsByUid(applicationUid)

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
    override fun approveOfflineSigning(applicationUid: UUID) {
        val application = getCreditApplicationByUid(applicationUid) ?: return

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
