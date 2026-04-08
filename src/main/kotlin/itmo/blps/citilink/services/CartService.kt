package itmo.blps.citilink.services

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Product
import org.springframework.transaction.annotation.Transactional

interface CartService {
    fun getCart(username: String): Cart
    fun getOrCreateCart(username: String): Cart
    fun getCartItems(cart: Cart): List<CartItem>

    fun addCartItem(product: Product, username: String): Cart
    fun removeCartItem(itemId: Long, username: String): Cart
    fun updateQuantity(itemId: Long, quantity: Int, username: String): Cart

    fun clearCart(username: String)
}
