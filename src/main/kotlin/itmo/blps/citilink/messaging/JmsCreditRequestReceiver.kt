package itmo.blps.citilink.messaging

import itmo.blps.citilink.repositories.CreditApplicationRepository
import itmo.blps.citilink.services.BankService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
@Profile("banks")
class CreditRequestReceiver(
    private val bankService: BankService,
    private val creditApplicationRepository: CreditApplicationRepository
) {
    @JmsListener(destination = "java:/jms/queue/CreditRequestsQueue")
    fun receiveApplication(applicationId: String) {
        println(">>> JMS: A request has been received to generate offers for the application №$applicationId")

        val application = creditApplicationRepository.findCreditApplicationById(applicationId.toLong())
            ?: throw EntityNotFoundException(">>> JMS: CreditApplication with id $applicationId not found")

        val isApproved = bankService.generateOffers(application)

        val restTemplate = RestTemplate()
        val messageBody = mapOf(
            "messageName" to "OffersGeneratedMessage",
            "processVariables" to mapOf(
                "isApproved" to mapOf("value" to isApproved, "type" to "Boolean")
            ),
            "correlationKeys" to mapOf(
                "applicationId" to mapOf("value" to application.id, "type" to "Long")
            )
        )
        restTemplate.postForEntity<String>("http://localhost:8080/engine-rest/message", messageBody)

        println(">>> JMS: Application offers №$applicationId successfully generated")
    }
}
