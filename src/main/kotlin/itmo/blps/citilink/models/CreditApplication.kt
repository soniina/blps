package itmo.blps.citilink.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

enum class ApplicationStatus {
    SENT,
    REJECTED,
    WAITING_FOR_OPERATOR,
    SIGNED,
    CANCELLED
}

@Entity
@Table(name = "credit_applications")
open class CreditApplication (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @Column(nullable = false)
    var termMonths: Int = 6,

    @Column(nullable = false)
    var initialPayment: Double = 0.0,

    @Column(nullable = false)
    var passportSeries: String,

    @Column(nullable = false)
    var passportNumber: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var phone: String,

    @OneToOne
    @JoinColumn(name = "selected_offer_id")
    var selectedOffer: CreditOffer? = null,

    @Enumerated(EnumType.STRING)
    var status: ApplicationStatus = ApplicationStatus.SENT
)
