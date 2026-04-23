package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.models.*
import itmo.blps.citilink.repositories.OrderItemRepository
import itmo.blps.citilink.repositories.OrderRepository
import itmo.blps.citilink.services.warehouse.WarehouseJcaService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.AccessDeniedException

@Profile("shop")
@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productService: ProductService,
    private val cartService: CartService,
    private val warehouseJcaService: WarehouseJcaService
) : OrderService {

    @Transactional(readOnly = true)
    override fun getOrder(orderId: Long, username: String): Order {
        val order = orderRepository.findOrderById(orderId)
            ?: throw EntityNotFoundException("Order with id $orderId not found")

        if (order.username != username) throw AccessDeniedException("Access denied")
        return order
    }

    @Transactional
    override fun process(request: OrderRequest, username: String, items: List<CartItem>): Order {
        if (items.isEmpty()) throw IllegalStateException("Cannot place order with empty cart")

        val itemsPrice = items.sumOf { it.product.price * it.quantity }
        val deliveryPrice = calculateDeliveryPrice(request.receiptMethod, itemsPrice)

        // создание и сохранение заказа в БД (commit еще не произошел)
        val order = orderRepository.save(
            Order(
                username = username,
                recipientName = request.name!!,
                recipientSurname = request.surname!!,
                recipientPhone = request.phone!!,
                receiptMethod = request.receiptMethod,
                deliveryAddress = if (request.receiptMethod == ReceiptMethod.DELIVERY) request.deliveryAddress else null,
                paymentMethod = request.paymentMethod,
                itemsPrice = itemsPrice,
                deliveryPrice = deliveryPrice,
                totalAmount = itemsPrice + deliveryPrice,
                status = OrderStatus.PENDING
            )
        )

        // проверка склада через JCA
        items.forEach { item ->
            productService.decreaseStock(item.product.id!!, item.quantity)

            orderItemRepository.save(
                OrderItem(
                    order = order,
                    product = item.product,
                    quantity = item.quantity
                )
            )

            // вызов внешней системы
            warehouseJcaService.reserveProduct(
                orderId = order.id.toString(),
                productId = item.product.id!!,
                quantity = item.quantity
            )
        }

        cartService.clearCart(username)

        return order
    }

    private fun calculateDeliveryPrice(method: ReceiptMethod, totalAmount: Double): Double {
        if (method == ReceiptMethod.PICKUP) return 0.0
        return if (totalAmount >= 10000.0) 0.0 else 500.0
    }
}