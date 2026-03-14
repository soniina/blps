package itmo.blps.citilink.services

import itmo.blps.citilink.models.Product

interface ProductService {
    fun getProductsOfDay(): List<Product>
    fun getProductById(productId: Long): Product?
}