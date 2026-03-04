package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: JpaRepository<Product, Long> {
    fun findProductById(productId: Long): Product?
    fun findProductsByIsProductOfDayIsTrue(): List<Product>
}
