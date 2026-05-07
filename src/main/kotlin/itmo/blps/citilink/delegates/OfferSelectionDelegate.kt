package itmo.blps.citilink.delegates

import itmo.blps.citilink.services.CreditOfferService
import itmo.blps.citilink.services.CreditService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class OfferSelectionDelegate(
    private val creditService: CreditService,
    private val creditOfferService: CreditOfferService
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val username = execution.getVariable("username") as String
        val offerId = execution.getVariable("selectedOfferId") as Long

        val offer = creditOfferService.getCreditOffer(offerId, username)
        creditService.selectOffer(offer.application, offer)

        execution.setVariable("isOnline", offer.isOnlineSigningAvailable)

        println(">>> Camunda: Пользователь $username выбрал кредитное предложение №$offerId")
    }
}