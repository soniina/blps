package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: JpaRepository<Product, Long> {
    fun findProductById(productId: Long): Product?
    fun findProductsByIsProductOfDayIsTrue(): List<Product>

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    fun decreaseStock(id: Long, quantity: Int): Int
}
