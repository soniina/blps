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
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService,
    private val userService: UserService,
    private val productService: ProductService
) {
    private fun getCurrentUser(authentication: Authentication?): itmo.blps.citilink.models.User {
        // Если пользователь залогинен — берем его username из токена, иначе константа "anonymous"
        val username = authentication?.name ?: "anonymous_guest"
        return userService.getOrCreateUser(username)
    }

//    @GetMapping
//    fun getCart(
//        @CookieValue(value = "session_id", required = false) sessionId: String?
//    ): ResponseEntity<CartResponse> {
//        val user = sessionId?.let { userService.findUser(it) }
//            ?: return ResponseEntity.ok(CartResponse(null, emptyList(), 0, 0.0))
//
//        val cart = cartService.getOrCreateCart(user)
//        val items = cartService.getCartItems(cart)
//
//        return ResponseEntity.ok(cart.toResponse(items))
//    }


    @GetMapping
    fun getCart(authentication: Authentication?): ResponseEntity<CartResponse> {
        val user = getCurrentUser(authentication)
        val cart = cartService.getOrCreateCart(user)
        val items = cartService.getCartItems(cart)

        return ResponseEntity.ok(cart.toResponse(items))
    }

//    @PostMapping("/items")
//    fun addCartItem(
//        @CookieValue(value = "session_id", required = false) sessionId: String?,
//        @RequestParam(required = true) productId: Long,
//        response: HttpServletResponse
//    ): ResponseEntity<CartResponse> {
//        val actualSessionId = sessionId ?: UUID.randomUUID().toString()
//
//        if (sessionId == null) {
//            val cookie = Cookie("session_id", actualSessionId).apply {
//                path = "/"
//                maxAge = 7 * 24 * 60 * 60
//            }
//            response.addCookie(cookie)
//        }
//
//        val user = userService.getOrCreateUser(actualSessionId)
//
//        val product = productService.getProductById(productId)
//        val cart = cartService.addCartItem(product, user)
//
//        val items = cartService.getCartItems(cart)
//        return ResponseEntity.status(HttpStatus.CREATED).body(cart.toResponse(items))
//    }

    @PostMapping("/items")
    fun addCartItem(
        authentication: Authentication?,
        @RequestParam(required = true) productId: Long
    ): ResponseEntity<CartResponse> {
        val user = getCurrentUser(authentication)

        val product = productService.getProductById(productId)
        val cart = cartService.addCartItem(product, user)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.status(HttpStatus.CREATED).body(cart.toResponse(items))
    }

//    @DeleteMapping("/items/{itemId}")
//    fun removeCartItem(
//        @CookieValue(value = "session_id") sessionId: String,
//        @PathVariable itemId: Long
//    ): ResponseEntity<CartResponse> {
//        val user = userService.getUser(sessionId)
//
//        val cart = cartService.removeCartItem(itemId, user)
//
//        val items = cartService.getCartItems(cart)
//        return ResponseEntity.ok(cart.toResponse(items))
//    }

    @DeleteMapping("/items/{itemId}")
    fun removeCartItem(
        authentication: Authentication, // Здесь Authentication обязателен
        @PathVariable itemId: Long
    ): ResponseEntity<CartResponse> {
        val user = userService.getOrCreateUser(authentication.name)

        val cart = cartService.removeCartItem(itemId, user)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.ok(cart.toResponse(items))
    }

//    @PatchMapping("/items/{itemId}")
//    fun updateCartItem(
//        @CookieValue(value = "session_id") sessionId: String,
//        @PathVariable itemId: Long,
//        @RequestParam quantity: Int
//    ): ResponseEntity<CartResponse> {
//        val user = userService.getUser(sessionId)
//
//        val cart = cartService.updateQuantity(itemId, quantity, user)
//
//        val items = cartService.getCartItems(cart)
//        return ResponseEntity.ok(cart.toResponse(items))
//    }

    @PatchMapping("/items/{itemId}")
    fun updateCartItem(
        authentication: Authentication,
        @PathVariable itemId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<CartResponse> {
        val user = userService.getOrCreateUser(authentication.name)

        val cart = cartService.updateQuantity(itemId, quantity, user)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.ok(cart.toResponse(items))
    }
}
