package itmo.blps.citilink.delegates

import itmo.blps.citilink.messaging.StompCreditRequestSender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class MessagingDelegate(private val stompSender: StompCreditRequestSender) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val applicationId = execution.getVariable("applicationId") as Long

        stompSender.sendApplicationId(applicationId)

        println(">>> Camunda: Заявка №$applicationId отправлена в банки")
    }
}