package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.responses.ProductResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.ProductService
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Товары", description = "Каталог товаров")
@Profile("shop")
@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val taskService: TaskService,
    private val runtimeService: RuntimeService
) {

    @Operation(summary = "Товары дня", description = "Возвращает список товаров дня")
    @GetMapping("/of-the-day")
    fun getProductsOfDay(authentication: Authentication?): ResponseEntity<List<ProductResponse>> {
        authentication?.let { auth ->
            val username = auth.name
            val task = taskService.createTaskQuery()
                .processInstanceBusinessKey(username)
                .active()
                .singleResult()

            if (task == null) {
                runtimeService.startProcessInstanceByKey("ShopProcess", username, mapOf("username" to username))
            }
        }

        val products = productService.getProductsOfDay().map { it.toResponse() }

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(products)
    }
}
