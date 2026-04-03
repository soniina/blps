package itmo.blps.citilink.services

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Product
import itmo.blps.citilink.models.User
import org.springframework.transaction.annotation.Transactional

interface CartService {
    fun getCart(user: User): Cart
    fun getOrCreateCart(user: User): Cart
    fun getCartItems(cart: Cart): List<CartItem>

    @Transactional
    fun addCartItem(product: Product, user: User): Cart
    fun removeCartItem(itemId: Long, user: User): Cart
    fun updateQuantity(itemId: Long, quantity: Int, user: User): Cart

    @Transactional
    fun clearCart(user: User)
}