package itmo.blps.citilink.dto.responses

import itmo.blps.citilink.models.CartItem

data class CartItemResponse (
    val id: Long,
    val product: ProductResponse,
    val quantity: Int
)

fun CartItem.toResponse() = CartItemResponse(
    id = requireNotNull(this.id) { "CartItem ID must not be null" },
    product = this.product.toResponse(),
    quantity = this.quantity
)
