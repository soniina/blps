package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.responses.CartResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "Корзина", description = "Управление товарами в корзине пользователя")
@Profile("shop")
@RestController
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService,
    private val productService: ProductService
) {

    @Operation(summary = "Получить корзину", description = "Возвращает текущий состав корзины авторизованного пользователя")
    @GetMapping
    fun getCart(authentication: Authentication): ResponseEntity<CartResponse> {
        val username = authentication.name
        val cart = cartService.getOrCreateCart(username)
        val items = cartService.getCartItems(cart)

        return ResponseEntity.ok(cart.toResponse(items))
    }

    @Operation(summary = "Добавить товар", description = "Добавляет товар в корзину по его ID")
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

    @Operation(summary = "Удалить товар", description = "Удаляет позицию из корзины по её ID")
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

    @Operation(summary = "Изменить количество", description = "Обновляет количество товара в корзине")
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
