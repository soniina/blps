package itmo.blps.citilink.models

import jakarta.persistence.*

@Entity
@Table(name = "carts_items")
open class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    var cart: Cart,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false)
    var quantity: Int = 1
)
