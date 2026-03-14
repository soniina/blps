package itmo.blps.citilink.services

import itmo.blps.citilink.dto.OrderRequest
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.OrderItem
import itmo.blps.citilink.models.ReceiptMethod
import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.OrderItemRepository
import itmo.blps.citilink.repositories.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OrderServiceImpl(private val orderRepository: OrderRepository, private val orderItemRepository: OrderItemRepository,
                       private val cartService: CartService) : OrderService {

    override fun getOrderById(orderId: Long): Order? = orderRepository.findOrderById(orderId)
    override fun getOrderByUid(orderUid: UUID): Order? = orderRepository.findOrderByUid(orderUid)

    @Transactional
    override fun process(request: OrderRequest, user: User, items: List<CartItem>): Order {
        if (items.isEmpty()) throw IllegalStateException("Корзина пуста")

        val itemsPrice = items.sumOf { it.product.price * it.quantity }
        val deliveryPrice = calculateDeliveryPrice(request.receiptMethod, itemsPrice)

        val order = orderRepository.save(Order(
            user = user,
            recipientName = request.name!!,
            recipientSurname = request.surname!!,
            recipientPhone = request.phone!!,
            receiptMethod = request.receiptMethod,
            deliveryAddress = if (request.receiptMethod == ReceiptMethod.DELIVERY) request.deliveryAddress else null,
            paymentMethod = request.paymentMethod,
            itemsPrice = itemsPrice,
            deliveryPrice = deliveryPrice,
            totalAmount = itemsPrice + deliveryPrice
        ))

        items.forEach { item ->
            orderItemRepository.save(OrderItem(
                order = order,
                product = item.product,
                quantity = item.quantity
            ))
        }

        cartService.clearCart(user)

        return order

    }

    override fun calculateDeliveryPrice(method: ReceiptMethod, totalAmount: Double): Double {
        if (method == ReceiptMethod.PICKUP) return 0.0
        return if (totalAmount >= 10000.0) 0.0 else 500.0
    }
}