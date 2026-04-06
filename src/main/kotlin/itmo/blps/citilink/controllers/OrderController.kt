package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.dto.responses.OrderResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val cartService: CartService,
    private val orderService: OrderService
) {

    @PostMapping
    fun orderProcess(
        authentication: Authentication,
        @Valid @RequestBody request: OrderRequest
    ): ResponseEntity<OrderResponse> {
        val username = authentication.name
        val cart = cartService.getCart(username)
        val cartItems = cartService.getCartItems(cart)

        val order = orderService.process(request, username, cartItems)
        return ResponseEntity.status(HttpStatus.CREATED).body(order.toResponse())
    }

    @GetMapping("/{orderId}")
    fun getOrder(
        authentication: Authentication,
        @PathVariable orderId: Long
    ): ResponseEntity<OrderResponse> {
        val username = authentication.name
        val order = orderService.getOrder(orderId, username)

        return ResponseEntity.ok(order.toResponse())
    }
}
