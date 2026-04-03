package itmo.blps.citilink.models

import jakarta.persistence.*

@Entity
@Table(name = "users")
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var sessionId: String,

    var name: String? = null,

    var email: String? = null,

    @Column(length = 20)
    var phone: String? = null
)