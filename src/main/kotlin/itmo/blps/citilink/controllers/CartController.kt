package itmo.blps.citilink.controllers

import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import itmo.blps.citilink.services.UserService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
@RequestMapping("/cart")
class CartController(private val cartService: CartService, private val userService: UserService, private val productService: ProductService) {

    @GetMapping
    fun listCartItems(
        @CookieValue(value = "session_id", required = false) sessionId: String?,
        model: Model
    ): String {
        val user = sessionId?.let { userService.getUser(it) }

        if (user == null) {
            model.addAttribute("cartItems", emptyList<CartItem>())
            model.addAttribute("cartItemsCount", 0)
            model.addAttribute("itemsPrice", 0.0)
        } else {
            val items = cartService.getCartItems(user)
            model.addAttribute("cartItems", items)
            model.addAttribute("cartItemsCount", items.sumOf { it.quantity })
            model.addAttribute("itemsPrice", items.sumOf { it.product.price * it.quantity })
        }

        return "cart"
    }

    @PostMapping
    @RequestMapping("/add")
    fun addCartItem(
        @CookieValue(value = "session_id", required = false) sessionId: String?,
        @RequestParam(required = true) productId: Long,
        response: HttpServletResponse
    ): String {
        val actualSessionId = sessionId ?: UUID.randomUUID().toString()

        if (sessionId == null) {
            val cookie = Cookie("session_id", actualSessionId)
            cookie.path = "/"
            cookie.maxAge = 7 * 24 * 60 * 60
            response.addCookie(cookie)
        }

        val user = userService.getOrCreateUser(actualSessionId)

        val product = productService.getProductById(productId) ?: return "redirect:/"

        cartService.addCartItem(product, user)
        return "redirect:/"
    }

    @DeleteMapping
    @RequestMapping("/remove/{itemId}")
    fun removeCartItem(@PathVariable itemId: Long): String {
        cartService.removeCartItem(itemId)
        return "redirect:/cart"
    }

    @PutMapping
    @RequestMapping("/update/{itemId}")
    fun minusCartItem(
        @PathVariable itemId: Long,
        @RequestParam delta: Int
    ): String {
        cartService.updateQuantity(itemId, delta)
        return "redirect:/cart"
    }


}
