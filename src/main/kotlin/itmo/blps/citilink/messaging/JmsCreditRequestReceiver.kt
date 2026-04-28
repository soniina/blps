package itmo.blps.citilink.messaging

import itmo.blps.citilink.repositories.CreditApplicationRepository
import itmo.blps.citilink.services.BankService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
@Profile("banks")
class CreditRequestReceiver(
    private val bankService: BankService,
    private val creditApplicationRepository: CreditApplicationRepository
) {
    @JmsListener(destination = "java:/jms/queue/CreditRequestsQueue")
    fun receiveApplication(applicationId: String) {
        println(">>> JMS: A request has been received to generate offers for the application №$applicationId")

        val application = creditApplicationRepository.findCreditApplicationsById(applicationId.toLong())
            ?: throw EntityNotFoundException(">>> JMS: CreditApplication with id $applicationId not found")

        bankService.generateOffers(application)
        println(">>> JMS: Application offers №$applicationId successfully generated")
    }
}
