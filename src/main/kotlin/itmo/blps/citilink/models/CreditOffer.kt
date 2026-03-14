package itmo.blps.citilink.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Columns
import java.util.UUID

@Entity
@Table(name = "credit_offers")
open class CreditOffer (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    val uid: UUID = UUID.randomUUID(),

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
