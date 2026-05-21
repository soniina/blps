package itmo.blps.citilink.messaging

import itmo.blps.citilink.repositories.CreditApplicationRepository
import itmo.blps.citilink.services.BankService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpEntity
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.postForEntity

@Component
@Profile("banks")
class CreditRequestReceiver(
    private val bankService: BankService,
    private val creditApplicationRepository: CreditApplicationRepository
) {
    @JmsListener(destination = "java:/jms/queue/CreditRequestsQueue")
    fun receiveApplication(applicationId: String) {
        try {
            println(">>> JMS: A request has been received to generate offers for the application №$applicationId")

            val application = creditApplicationRepository.findCreditApplicationById(applicationId.toLong())
                ?: throw EntityNotFoundException(">>> JMS: CreditApplication with id $applicationId not found")

            val isApproved = bankService.generateOffers(application)

            val restTemplate = RestTemplate()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val messageBody = mapOf(
                "messageName" to "OffersGeneratedMessage",
                "businessKey" to applicationId,
                "processVariables" to mapOf(
                    "isApproved" to mapOf("value" to isApproved, "type" to "Boolean")
                )
            )

            val requestEntity = HttpEntity(messageBody, headers)

            println(">>> JMS: Sending response to Shop for BusinessKey: $applicationId")

            restTemplate.postForEntity<String>(
                "http://localhost:8080/engine-rest/message",
                requestEntity,
                String::class.java
            )

            println(">>> JMS: Application offers №$applicationId successfully generated and sent to Shop")

        } catch (e: Exception) {
            println("!!! JMS ERROR IN BANK RECEIVER: ${e.message}")
            e.printStackTrace()
        }
    }
}
