package itmo.blps.citilink.delegates

import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.models.PaymentMethod
import itmo.blps.citilink.models.ReceiptMethod
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("shop")
@Component
class OrderCreationDelegate(
    private val orderService: OrderService,
    private val cartService: CartService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        try {
            val username = execution.getVariable("username") as? String
                ?: throw IllegalArgumentException("Variable 'username' is missing!")

            val request = OrderRequest(
                name = execution.getVariable("name")?.toString() ?: "",
                surname = execution.getVariable("surname")?.toString() ?: "",
                phone = execution.getVariable("phone")?.toString() ?: "",
                receiptMethod = ReceiptMethod.valueOf(execution.getVariable("receiptMethod")?.toString() ?: "PICKUP"),
                deliveryAddress = execution.getVariable("deliveryAddress")?.toString(),
                paymentMethod = PaymentMethod.valueOf(execution.getVariable("paymentMethod")?.toString() ?: "CREDIT")
            )

            println(">>> Camunda: Creating order for user $username...")

            val cart = cartService.getCart(username)
            val cartItems = cartService.getCartItems(cart)

            val order = orderService.process(request, username, cartItems)

            execution.setVariable("orderId", order.id)
            println(">>> Camunda: Order №${order.id} created successfully")

        } catch (e: Exception) {
            println("!!! CRITICAL ERROR IN OrderCreationDelegate: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}