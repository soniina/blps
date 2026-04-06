package itmo.blps.citilink.services

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Product
import itmo.blps.citilink.repositories.CartItemRepository
import itmo.blps.citilink.repositories.CartRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartServiceImpl(private val cartRepository: CartRepository, private val cartItemRepository: CartItemRepository) :
    CartService {

    override fun getCart(username: String) = cartRepository.findCartByUsername(username)
        ?: throw EntityNotFoundException("Cart for user ${username} not found")

    override fun getOrCreateCart(username: String) =
        cartRepository.findCartByUsername(username) ?: cartRepository.save(Cart(username = username))

    override fun getCartItems(cart: Cart): List<CartItem> = cartItemRepository.findAllByCartOrderByIdAsc(cart)

    @Transactional
    override fun addCartItem(product: Product, username: String): Cart {
        val cart = getOrCreateCart(username)
        val existingItem = cartItemRepository.findByCartAndProduct(cart, product)

        if (existingItem != null) {
            existingItem.quantity += 1
            cartItemRepository.save(existingItem)
        } else {
            cartItemRepository.save(CartItem(cart = cart, product = product))
        }
        return cart
    }

    @Transactional
    override fun removeCartItem(itemId: Long, username: String): Cart {
        val item = cartItemRepository.findCartItemById(itemId)
            ?: throw EntityNotFoundException("Cart item with id $itemId not found")

        if (item.cart.username != username) throw AccessDeniedException("You cannot remove items from another user's cart")

        val cart = item.cart
        cartItemRepository.delete(item)
        return cart
    }

    @Transactional
    override fun updateQuantity(itemId: Long, quantity: Int, username: String): Cart {
        val item = cartItemRepository.findCartItemById(itemId)
            ?: throw EntityNotFoundException("Cart item with id $itemId not found")

        if (item.cart.username != username)
            throw AccessDeniedException("You cannot update items in another user's cart")

        if (quantity < 1) throw IllegalArgumentException("Quantity must be at least 1")
        if (quantity > item.product.stockQuantity) {
            throw IllegalStateException("Requested quantity $quantity exceeds stock availability (${item.product.stockQuantity})")
        }

        item.quantity = quantity
        cartItemRepository.save(item)
        return item.cart
    }

    @Transactional
    override fun clearCart(username: String) {
        val cart = cartRepository.findCartByUsername(username) ?: return
        cartItemRepository.deleteAllByCart(cart)
    }
}