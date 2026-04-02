package itmo.blps.citilink.dto.responses

import itmo.blps.citilink.models.Product

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String?
)

fun Product.toResponse() = ProductResponse(
    id = requireNotNull(this.id) { "Product ID must not be null" },
    name = this.name,
    price = this.price,
    description = this.description
)
