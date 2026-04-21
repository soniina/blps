package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findCartByUsername(username: String): Cart?
}
