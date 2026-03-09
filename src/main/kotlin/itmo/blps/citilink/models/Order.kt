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
open class Order (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var recipientName: String,

    @Column(nullable = false)
    var recipientSurname: String,

    @Column(nullable = false)
    var recipientPhone: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var receiptMethod: ReceiptMethod = ReceiptMethod.PICKUP,

    var deliveryAddress: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var paymentMethod: PaymentMethod,

    @Column(nullable = false)
    var itemsPrice: Double,

    @Column(nullable = false)
    var deliveryPrice: Double,

    @Column(nullable = false)
    var totalAmount: Double,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
