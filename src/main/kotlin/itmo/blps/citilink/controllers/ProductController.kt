package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.responses.ProductResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Товары", description = "Каталог товаров")
@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @Operation(summary = "Товары дня", description = "Возвращает список товаров дня")
    @GetMapping("/of-the-day")
    fun getProductsOfDay(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getProductsOfDay().map { it.toResponse() }

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(products)
    }

}
