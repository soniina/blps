package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.responses.CartResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService,
    private val productService: ProductService
) {

    @GetMapping
    fun getCart(authentication: Authentication): ResponseEntity<CartResponse> {
        val username = authentication.name
        val cart = cartService.getOrCreateCart(username)
        val items = cartService.getCartItems(cart)

        return ResponseEntity.ok(cart.toResponse(items))
    }

    @PostMapping("/items")
    fun addCartItem(
        authentication: Authentication,
        @RequestParam productId: Long
    ): ResponseEntity<CartResponse> {
        val username = authentication.name

        val product = productService.getProductById(productId)
        val cart = cartService.addCartItem(product, username)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.status(HttpStatus.CREATED).body(cart.toResponse(items))
    }

    @DeleteMapping("/items/{itemId}")
    fun removeCartItem(
        authentication: Authentication,
        @PathVariable itemId: Long
    ): ResponseEntity<CartResponse> {
        val username = authentication.name

        val cart = cartService.removeCartItem(itemId, username)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.ok(cart.toResponse(items))
    }

    @PatchMapping("/items/{itemId}")
    fun updateCartItem(
        authentication: Authentication,
        @PathVariable itemId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<CartResponse> {
        val username = authentication.name

        val cart = cartService.updateQuantity(itemId, quantity, username)

        val items = cartService.getCartItems(cart)
        return ResponseEntity.ok(cart.toResponse(items))
    }
}
