package itmo.blps.citilink.delegates

import itmo.blps.citilink.services.CreditService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component
class CreditSigningDelegate(private val creditService: CreditService) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val applicationId = execution.getVariable("applicationId") as Long
        val username = execution.getVariable("username") as String

        val application = creditService.getCreditApplication(applicationId, username)
        creditService.signApplication(application)

        println(">>> Camunda: Заявка №$applicationId успешно подписана онлайн")
    }
}