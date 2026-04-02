package itmo.blps.citilink.dto.responses

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem

data class CartResponse(
    val id: Long?,
    val items: List<CartItemResponse>,
    val totalItems: Int,
    val totalPrice: Double
)

fun Cart.toResponse(items: List<CartItem>) = CartResponse(
    id = this.id,
    items = items.map { it.toResponse() },
    totalItems = items.sumOf { it.quantity },
    totalPrice = items.sumOf { it.product.price * it.quantity }
)
