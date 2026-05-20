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
            termMonths = execution.getVariable("termMonths")?.toString()?.toInt() ?: 0,
            initialPayment = execution.getVariable("initialPayment")?.toString()?.toDouble() ?: 0.0,

            passportSeries = execution.getVariable("passportSeries")?.toString() ?: "",
            passportNumber = execution.getVariable("passportNumber")?.toString() ?: "",
            email = execution.getVariable("email")?.toString() ?: "",
            phone = execution.getVariable("phone")?.toString() ?: ""
        )

        val order = orderService.getOrder(orderId, username)

        val application = creditService.process(request, order)

        execution.setVariable("applicationId", application.id)

        println(">>> Camunda: Credit order №${application.id} created")
    }
}