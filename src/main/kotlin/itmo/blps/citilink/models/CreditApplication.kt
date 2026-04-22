package itmo.blps.citilink.models

import jakarta.persistence.*

enum class ApplicationStatus {
    SENT,
    WAITING_FOR_BANKS,
    OFFERS_READY,
    REJECTED,
    WAITING_FOR_OPERATOR,
    PENDING_SIGNATURE,
    SIGNED
}

@Entity
@Table(name = "credits_applications")
open class CreditApplication(
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
