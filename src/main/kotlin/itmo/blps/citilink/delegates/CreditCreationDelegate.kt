package itmo.blps.citilink.delegates

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.services.CreditService
import itmo.blps.citilink.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("shop")
@Component
class CreditCreationDelegate(
    private val creditService: CreditService,
    private val orderService: OrderService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val username = execution.getVariable("username") as String
        val orderId = execution.getVariable("orderId") as Long

        val request = CreditApplicationRequest(
            termMonths = execution.getVariable("termMonths") as Int,
            initialPayment = execution.getVariable("initialPayment") as Double,
            passportSeries = execution.getVariable("passportSeries") as String,
            passportNumber = execution.getVariable("passportNumber") as String,
            email = execution.getVariable("email") as String,
            phone = execution.getVariable("phone") as String
        )

        val order = orderService.getOrder(orderId, username)

        val application = creditService.process(request, order)

        execution.setVariable("applicationId", application.id)

        println(">>> Camunda: Заявка №${application.id} создана")
    }
}