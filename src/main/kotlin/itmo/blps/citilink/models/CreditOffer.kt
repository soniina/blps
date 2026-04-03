package itmo.blps.citilink.models

import jakarta.persistence.*

@Entity
@Table(name = "credits_offers")
open class CreditOffer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    var application: CreditApplication,

    @Column(nullable = false)
    var bankName: String,

    @Column(nullable = false)
    var interestRate: Double,

    @Column(nullable = false)
    var isOnlineSigningAvailable: Boolean = false
)
