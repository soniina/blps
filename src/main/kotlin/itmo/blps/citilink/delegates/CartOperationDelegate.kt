package itmo.blps.citilink.delegates

import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("shop")
@Component()
class CartOperationDelegate(
    private val cartService: CartService,
    private val productService: ProductService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val action = execution.getVariable("action") as String
        val username = execution.getVariable("username") as String

        when (action) {
//            "ADD" -> {
//                val productId = execution.getVariable("productId") as Long
//                val product = productService.getProductById(productId)
//                cartService.addCartItem(product, username)
//            }
            "ADD" -> {
                val productId = execution.getVariable("productId").toString().toLong()
                val product = productService.getProductById(productId)
                // проверка, чтобы не добавить в корзину товар, который не относится к товарам дня
                if (!product.isProductOfDay) {
                    throw IllegalArgumentException("Ошибка: Товар '${product.name}' не является товаром дня!")
                }

                cartService.addCartItem(product, username)
                println(">>> Camunda: Product ${product.name} added to cart")
            }
            "UPDATE" -> {
                val itemId = execution.getVariable("itemId") as Long
                val quantity = execution.getVariable("quantity") as Int
                cartService.updateQuantity(itemId, quantity, username)
            }
            "REMOVE" -> {
                val itemId = execution.getVariable("itemId") as Long
                cartService.removeCartItem(itemId, username)
            }
        }
    }
}