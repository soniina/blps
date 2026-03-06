package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<Order, Long>