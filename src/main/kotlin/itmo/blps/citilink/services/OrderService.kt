package itmo.blps.citilink.services

import itmo.blps.citilink.dto.OrderRequest
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.ReceiptMethod
import itmo.blps.citilink.models.User
import org.springframework.transaction.annotation.Transactional

interface OrderService {
    fun getOrderById(orderID: Long): Order?

    @Transactional
    fun process(request: OrderRequest, user: User, items: List<CartItem>): Order
    fun calculateDeliveryPrice(method: ReceiptMethod, totalAmount: Double): Double
}