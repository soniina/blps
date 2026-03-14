package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository: JpaRepository<Order, Long> {
    fun findOrderById(orderId: Long): Order?
    fun findOrderByUid(orderUid: UUID): Order?
}
