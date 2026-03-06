package itmo.blps.citilink.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

enum class ReceiptMethod {
    PICKUP,
    DELIVERY
}

enum class PaymentMethod {
//    SBP,
//    CARD,
//    UPON_RECEIPT,
    CREDIT
}

enum class OrderStatus {
    PENDING,
    PROCESSING,
    READY_FOR_PICKUP,
    IN_DELIVERY,
    COMPLETED,
    CANCELLED
}

@Entity
@Table(name = "orders")
data class Order (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val recipientName: String,

    @Column(nullable = false)
    val recipientSurname: String,

    @Column(nullable = false)
    val recipientPhone: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val receiptMethod: ReceiptMethod = ReceiptMethod.PICKUP,

    val deliveryAddress: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val paymentMethod: PaymentMethod,

    @Column(nullable = false)
    val totalAmount: Double,

    @Column(nullable = false)
    val deliveryPrice: Double,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
