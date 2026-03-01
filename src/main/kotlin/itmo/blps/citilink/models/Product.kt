package itmo.blps.citilink.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Product (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val price: Double,

    val description: String? = null,

    @Column(nullable = false)
    val isProductOfDay: Boolean = false,

    @Column(nullable = false)
    val stockQuantity: Long
)