package itmo.blps.citilink.controllers

import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import itmo.blps.citilink.services.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class ProductController(private val productService: ProductService, private val userService: UserService, private val cartService: CartService) {

    @GetMapping
    fun listProductsOfDay(
        @CookieValue(value = "session_id", required = false) sessionId: String?,
        model: Model
    ): String {
        val user = sessionId?.let { userService.getUser(it) }

        if (user == null) {
            model.addAttribute("cartItemsCount", 0)
            model.addAttribute("cartProductIds", emptyList<Long>())
        }
        else {
            val cartItemIds = cartService.getCartItems(user).map { it.product.id }
            model.addAttribute("cartItemsCount", cartItemIds.size)
            model.addAttribute("cartProductIds", cartItemIds)
        }

        model.addAttribute("productsOfDay", productService.getProductsOfDay())

        return "index"
    }
}
