package itmo.blps.citilink.services

import itmo.blps.citilink.models.Product
import itmo.blps.citilink.repositories.ProductRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService {

    override fun getProductsOfDay() = productRepository.findProductsByIsProductOfDayIsTrue()

    override fun getProductById(productId: Long): Product = productRepository.findProductById(productId)
        ?: throw EntityNotFoundException("Product with id $productId not found")

    @Transactional
    override fun decreaseStock(productId: Long, quantity: Int) {
        val updatedRows = productRepository.decreaseStock(productId, quantity)
        if (updatedRows == 0) {
            throw IllegalStateException("Product with id $productId out of stock")
        }
    }
}
