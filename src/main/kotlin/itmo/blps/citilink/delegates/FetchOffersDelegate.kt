package itmo.blps.citilink.delegates

import com.fasterxml.jackson.databind.ObjectMapper
import itmo.blps.citilink.repositories.CreditOfferRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.spin.Spin.JSON
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("shop")
@Component
class FetchOffersDelegate(
    private val creditOfferRepository: CreditOfferRepository,
    private val objectMapper: ObjectMapper
) : JavaDelegate {

    @Transactional(readOnly = true)
    override fun execute(execution: DelegateExecution) {
        val applicationId = execution.getVariable("applicationId").toString().toLong()

        println(">>> FetchOffersDelegate [START]: Fetching offers for App ID: $applicationId")

        var offers = creditOfferRepository.findAllByApplicationIdNative(applicationId)

        if (offers.isEmpty()) {
            println(">>> FetchOffersDelegate [RETRY]: Offers not found in DB. Waiting 2000ms for commit synchronization...")
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            offers = creditOfferRepository.findAllByApplicationIdNative(applicationId)
        }

        println(">>> FetchOffersDelegate [RESULT]: Found ${offers.size} offers in Database")

        val offerOptions = offers.map { offer ->
            val onlineStatus = if (offer.isOnlineSigningAvailable) "Online signing available" else "Offline signing only"
            mapOf(
                "label" to "${offer.bankName} (Rate: ${offer.interestRate}%) — $onlineStatus",
                "value" to offer.id.toString()
            )
        }
        val jsonString = objectMapper.writeValueAsString(offerOptions)
        execution.setVariable("availableOffers", JSON(jsonString))

        println(">>> FetchOffersDelegate [SUCCESS]: Process variable 'availableOffers' updated")
    }
}