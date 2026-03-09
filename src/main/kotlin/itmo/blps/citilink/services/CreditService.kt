package itmo.blps.citilink.services

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.Order
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

}