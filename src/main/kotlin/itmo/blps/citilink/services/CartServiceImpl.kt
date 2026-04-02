package itmo.blps.citilink.services

import itmo.blps.citilink.models.Cart
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Product
import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.CartItemRepository
import itmo.blps.citilink.repositories.CartRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
//import org.springframework.security.access.AccessDeniedException

@Service
class CartServiceImpl(private val cartRepository: CartRepository, private val cartItemRepository: CartItemRepository) :
    CartService {

    override fun getOrCreateCart(user: User): Cart {
        return cartRepository.findCartByUser(user) ?: cartRepository.save(Cart(user = user))
    }

    override fun getCartItems(cart: Cart): List<CartItem> = cartItemRepository.findAllByCartOrderByIdAsc(cart)

    @Transactional
    override fun addCartItem(product: Product, user: User): Cart {
        val cart = getOrCreateCart(user)

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
    override fun removeCartItem(itemId: Long, user: User): Cart {
        val item = cartItemRepository.findCartItemById(itemId) ?: throw EntityNotFoundException("Cart item with id $itemId not found")

//        if (item.cart.user.id != user.id) throw AccessDeniedException("Access denied")

        val cart = item.cart
        cartItemRepository.delete(item)
        return cart
    }

    @Transactional
    override fun updateQuantity(itemId: Long, quantity: Int, user: User): Cart {
        val item = cartItemRepository.findCartItemById(itemId) ?: throw EntityNotFoundException("Cart item with id $itemId not found")

//        if (item.cart.user.id != user.id) throw AccessDeniedException("Access denied")
        if (quantity < 1) throw IllegalArgumentException("Quantity must be at least 1")
        if (quantity > item.product.stockQuantity) throw IllegalStateException("Requested quantity $quantity exceeds stock availability (${item.product.stockQuantity})")

        item.quantity = quantity
        cartItemRepository.save(item)
        return item.cart
    }

    @Transactional
    override fun clearCart(user: User) {
        val cart = cartRepository.findCartByUser(user) ?: return
        cartItemRepository.deleteAllByCart(cart)
    }

}
