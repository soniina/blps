package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Order
import org.springframework.transaction.annotation.Transactional

interface OrderService {
    fun getOrder(orderId: Long, username: String): Order

    @Transactional
    fun process(request: OrderRequest, username: String, items: List<CartItem>): Order
}
