package itmo.blps.citilink.services

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Product
import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.CartItemRepository
import itmo.blps.citilink.repositories.CartRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(private val cartRepository: CartRepository, private val cartItemRepository: CartItemRepository) {

    fun getCartItems(user: User): List<CartItem> {
        val cart = cartRepository.findCartByUser(user) ?: return emptyList()
        return cartItemRepository.findAllByCart(cart)
    }

    @Transactional
    fun addCartItem(product: Product, user: User) {
        val cart = cartRepository.findCartByUser(user) ?: cartRepository.save(Cart(user = user))
        cartItemRepository.save(CartItem(cart = cart, product = product))
    }

    fun removeCartItem(itemId: Long) {
        cartItemRepository.deleteById(itemId)
    }

    fun updateQuantity(itemId: Long, delta: Int) {
        val item = cartItemRepository.findCartItemById(itemId) ?: return
        val newQuantity = item.quantity + delta

        if (newQuantity in 0..item.product.stockQuantity) {
            item.quantity = newQuantity
            cartItemRepository.save(item)
        }
    }

    @Transactional
    fun clearCart(user: User) {
        val cart = cartRepository.findCartByUser(user) ?: return
        cartItemRepository.deleteAllByCart(cart)
    }

}
