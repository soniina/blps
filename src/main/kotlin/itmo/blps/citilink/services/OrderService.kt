package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.User
import org.springframework.transaction.annotation.Transactional

interface OrderService {
    fun getOrder(orderId: Long, user: User): Order

    @Transactional
    fun process(request: OrderRequest, user: User, items: List<CartItem>): Order
}
