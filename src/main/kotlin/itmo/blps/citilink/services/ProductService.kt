package itmo.blps.citilink.services

import itmo.blps.citilink.models.Product
import itmo.blps.citilink.repositories.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun getProductsOfDay() = productRepository.findProductsByIsProductOfDayIsTrue()

    fun getProductById(productId: Long): Product? = productRepository.findProductById(productId)
}
