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
    APPROVED,
    REJECTED
}

@Entity
@Table(name = "credit_applications")
data class CreditApplication (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @Column(nullable = false)
    val termMonths: Int = 6,

    @Column(nullable = false)
    val initialPayment: Double = 0.0,

    @Column(nullable = false)
    var passportSeries: String,

    @Column(nullable = false)
    var passportNumber: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var phone: String,

    @Enumerated(EnumType.STRING)
    var status: ApplicationStatus = ApplicationStatus.SENT
)
