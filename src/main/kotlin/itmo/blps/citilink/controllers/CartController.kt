package itmo.blps.citilink.controllers

import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/cart")
class CartController(private val cartService: CartService, private val userService: UserService) {

    @GetMapping
    fun listCartItems(
        @CookieValue(value = "session_id", required = false) sessionId: String?,
        model: Model
    ): String {
        val user = sessionId?.let { userService.getUser(it) }

        if (user == null) {
            model.addAttribute("cartItems", emptyList<CartItem>())
            model.addAttribute("totalPrice", 0.0)
        } else {
            val items = cartService.getCartItems(user)
            model.addAttribute("cartItems", items)
            model.addAttribute("totalPrice", items.sumOf { it.product.price * it.quantity })
        }

        return "cart"
    }

}