package itmo.blps.citilink.dto.responses

import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.OrderStatus
import itmo.blps.citilink.models.PaymentMethod
import itmo.blps.citilink.models.ReceiptMethod
import java.time.LocalDateTime

data class OrderResponse (
    val id: Long,

    val status: OrderStatus,

    val recipientName: String,
    val recipientSurname: String,
    val recipientPhone: String,

    val paymentMethod: PaymentMethod,
    val itemsPrice: Double,
    val deliveryPrice: Double,
    val totalAmount: Double,

    val receiptMethod: ReceiptMethod,
    val deliveryAddress: String? = null,

    val createdAt: LocalDateTime
)

fun Order.toResponse() = OrderResponse(
    id = requireNotNull(id) { "Order ID must not be null" },
    status = status,
    recipientName = recipientName,
    recipientSurname = recipientSurname,
    recipientPhone = recipientPhone,
    paymentMethod = paymentMethod,
    itemsPrice = itemsPrice,
    deliveryPrice = deliveryPrice,
    totalAmount = totalAmount,
    receiptMethod = receiptMethod,
    deliveryAddress = deliveryAddress,
    createdAt = createdAt
)
