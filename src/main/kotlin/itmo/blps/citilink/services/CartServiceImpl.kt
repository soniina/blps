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
class CartServiceImpl(private val cartRepository: CartRepository, private val cartItemRepository: CartItemRepository) :
    CartService {

    override fun getCartItems(user: User): List<CartItem> {
        val cart = cartRepository.findCartByUser(user) ?: return emptyList()
        return cartItemRepository.findAllByCartOrderByIdAsc(cart)
    }

    @Transactional
    override fun addCartItem(product: Product, user: User) {
        val cart = cartRepository.findCartByUser(user) ?: cartRepository.save(Cart(user = user))
        cartItemRepository.save(CartItem(cart = cart, product = product))
    }

    override fun removeCartItem(itemId: Long) {
        cartItemRepository.deleteById(itemId)
    }

    override fun updateQuantity(itemId: Long, delta: Int) {
        val item = cartItemRepository.findCartItemById(itemId) ?: return
        val newQuantity = item.quantity + delta

        if (newQuantity in 0..item.product.stockQuantity) {
            item.quantity = newQuantity
            cartItemRepository.save(item)
        }
    }

    @Transactional
    override fun clearCart(user: User) {
        val cart = cartRepository.findCartByUser(user) ?: return
        cartItemRepository.deleteAllByCart(cart)
    }

}
