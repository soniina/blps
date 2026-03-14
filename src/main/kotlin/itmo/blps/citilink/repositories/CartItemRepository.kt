package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CartItemRepository: JpaRepository<CartItem, Long> {
    fun findCartItemByUid(itemUid: UUID): CartItem?
    fun findAllByCartOrderByIdAsc(cart: Cart): List<CartItem>
    fun deleteCartItemByUid(itemUid: UUID)
    fun deleteAllByCart(cart: Cart)
}
