package itmo.blps.citilink.services

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.OrderStatus
import itmo.blps.citilink.repositories.CreditApplicationRepository
import org.springframework.stereotype.Service

@Service
class CreditService(private val creditApplicationRepository: CreditApplicationRepository) {

    fun getCreditApplicationById(applicationId: Long): CreditApplication? = creditApplicationRepository.findCreditApplicationsById(applicationId)

    fun process(request: CreditApplicationRequest, order: Order): CreditApplication {
        return creditApplicationRepository.save(
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
    }

    fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus) {
        creditApplication.status = status
        creditApplicationRepository.save(creditApplication)
    }

    fun selectOffer(creditApplication: CreditApplication, selectedOffer: CreditOffer) {
        creditApplication.selectedOffer = selectedOffer
        creditApplicationRepository.save(creditApplication)
    }

    fun signApplication(creditApplication: CreditApplication) {
        creditApplication.status = ApplicationStatus.SIGNED
        creditApplication.order.status = OrderStatus.PROCESSING
        creditApplicationRepository.save(creditApplication)
    }

}