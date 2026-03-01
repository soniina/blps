package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Product
import jakarta.annotation.Resource
import org.springframework.data.jpa.repository.JpaRepository

@Resource
interface ProductRepository: JpaRepository<Product, Long> {
    fun getProductsByIsProductOfDayIsTrue(): List<Product>
}