package itmo.blps.citilink.delegates

import itmo.blps.citilink.services.CreditService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("shop")
@Component
class CreditOfflineSigningDelegate(private val creditService: CreditService) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val applicationId = execution.getVariable("applicationId") as Long

        creditService.approveOfflineSigning(applicationId)

        println(">>> Camunda: Оператор подтвердил подпись для заявки №$applicationId")
    }
}