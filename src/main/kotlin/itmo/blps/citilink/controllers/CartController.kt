package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.responses.CartResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import itmo.blps.citilink.services.UserService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/cart")
class CartController(private val cartService: CartService, private val userService: UserService, private val productService: ProductService) {

    @GetMapping
    fun getCart(
        @CookieValue(value = "session_id", required = false) sessionId: String?
    ): ResponseEntity<CartResponse> {
        val user = sessionId?.let { userService.findUser(it) }
            ?: return ResponseEntity.ok(CartResponse(null, emptyList(), 0, 0.0))

        val cart = cartService.getOrCreateCart(user)
        val items = cartService.getCartItems(cart)

        return ResponseEntity.ok(cart.toResponse(items))
    }

    @PostMapping("/items")
    fun addCartItem(
        @CookieValue(value = "session_id", required = false) sessionId: String?,
        @RequestParam(required = true) productId: Long,
        response: HttpServletResponse
    ): ResponseEntity<CartResponse> {
        val actualSessionId = sessionId ?: UUID.randomUUID().toString()

        if (sessionId == null) {
            val cookie = Cookie("session_id", actualSessionId).apply {
                path = "/"
                maxAge = 7 * 24 * 60 * 60
            }
            response.addCookie(cookie)
        }

        val user = userService.getOrCreateUser(actualSessionId)

        val product = productService.getProductById(productId)
        val cart = cartService.addCartItem(product, user)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.status(HttpStatus.CREATED).body(cart.toResponse(items))
    }

    @DeleteMapping("/items/{itemId}")
    fun removeCartItem(
        @CookieValue(value = "session_id") sessionId: String,
        @PathVariable itemId: Long
    ): ResponseEntity<CartResponse> {
        val user = userService.getUser(sessionId)

        val cart = cartService.removeCartItem(itemId, user)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.ok(cart.toResponse(items))
    }

    @PatchMapping("/items/{itemId}")
    fun updateCartItem(
        @CookieValue(value = "session_id") sessionId: String,
        @PathVariable itemId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<CartResponse> {
        val user = userService.getUser(sessionId)

        val cart = cartService.updateQuantity(itemId, quantity, user)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.ok(cart.toResponse(items))
    }
}
