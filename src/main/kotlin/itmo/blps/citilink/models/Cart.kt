package itmo.blps.citilink.models

import jakarta.persistence.*

@Entity
@Table(name = "carts")
open class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
)
