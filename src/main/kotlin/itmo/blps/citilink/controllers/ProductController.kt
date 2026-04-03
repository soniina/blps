package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.responses.ProductResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.ProductService
import itmo.blps.citilink.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService, private val userService: UserService, private val cartService: CartService) {

    @GetMapping("/of-the-day")
    fun getProductsOfDay(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getProductsOfDay().map { it.toResponse() }

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(products)
    }

}
