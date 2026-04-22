package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    // Найти все позиции конкретного заказа
    fun findAllByOrder(order: Order): List<OrderItem>
}