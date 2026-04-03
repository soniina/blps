package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.dto.responses.OrderResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.OrderService
import itmo.blps.citilink.services.UserService
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(private val userService: UserService, private val cartService: CartService, private val orderService: OrderService) {

    @PostMapping
    fun orderProcess(
        @CookieValue(value = "session_id") sessionId: String,
        @Valid @RequestBody request: OrderRequest
    ) : ResponseEntity<OrderResponse> {
        val user = userService.getUser(sessionId)
        val cart = cartService.getCart(user)
        val cartItems = cartService.getCartItems(cart)

        val order = orderService.process(request, user, cartItems)
        return ResponseEntity.status(HttpStatus.CREATED).body(order.toResponse())
    }

    @GetMapping("/{orderId}")
    fun getOrder(
        @CookieValue(value = "session_id") sessionId: String,
        @PathVariable orderId: Long
    ): ResponseEntity<OrderResponse> {
        val user = userService.getUser(sessionId)
        val order = orderService.getOrder(orderId, user)

        return ResponseEntity.ok(order.toResponse())
    }
}
