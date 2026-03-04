package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository: JpaRepository<CartItem, Long> {
    fun findCartItemById(itemId: Long): CartItem?
    fun findAllByCart(cart: Cart): List<CartItem>
}
