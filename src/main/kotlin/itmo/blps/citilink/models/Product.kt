package itmo.blps.citilink.models

import jakarta.persistence.*

@Entity
@Table(name = "products")
open class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var price: Double,

    var description: String? = null,

    @Column(nullable = false)
    var isProductOfDay: Boolean = false,

    @Column(nullable = false)
    var stockQuantity: Long
)