package itmo.blps.citilink.services

import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Product
import itmo.blps.citilink.models.User
import org.springframework.transaction.annotation.Transactional

interface CartService {
    fun getCartItems(user: User): List<CartItem>

    @Transactional
    fun addCartItem(product: Product, user: User)
    fun removeCartItem(itemId: Long)
    fun updateQuantity(itemId: Long, delta: Int)

    @Transactional
    fun clearCart(user: User)
}