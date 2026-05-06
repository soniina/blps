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
        val username = execution.getVariable("username") as String

        val request = OrderRequest(
            name = execution.getVariable("name") as String,
            surname = execution.getVariable("surname") as String,
            phone = execution.getVariable("phone") as String,
            receiptMethod = ReceiptMethod.valueOf(execution.getVariable("receiptMethod") as String),
            deliveryAddress = execution.getVariable("deliveryAddress") as? String,
            paymentMethod = PaymentMethod.valueOf(execution.getVariable("paymentMethod") as String)
        )

        val cart = cartService.getCart(username)
        val cartItems = cartService.getCartItems(cart)

        val order = orderService.process(request, username, cartItems)

        execution.setVariable("orderId", order.id)
        println(">>> Camunda: Заказ №${order.id} создана")
    }
}