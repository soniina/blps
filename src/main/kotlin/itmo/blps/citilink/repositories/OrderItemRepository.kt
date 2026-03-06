package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository: JpaRepository<OrderItem, Long>