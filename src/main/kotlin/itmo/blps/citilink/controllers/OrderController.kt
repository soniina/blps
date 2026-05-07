package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.requests.OrderRequest
import itmo.blps.citilink.dto.responses.OrderResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.OrderService
import jakarta.validation.Valid
import org.camunda.bpm.engine.TaskService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "Заказы", description = "Оформление и просмотр заказов")
@Profile("shop")
@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
    private val taskService: TaskService
) {

    @Operation(summary = "Оформить заказ", description = "Передает данные заказа в бизнес-процесс")
    @PostMapping
    fun orderProcess(
        authentication: Authentication,
        @Valid @RequestBody request: OrderRequest
    ): ResponseEntity<String> {
        val username = authentication.name
        val task = taskService.createTaskQuery()
            .processInstanceBusinessKey(username)
            .taskDefinitionKey("OrderDetailsTask")
            .active()
            .singleResult() ?: return ResponseEntity.badRequest().body("Этап OrderDetailsTask недоступен для пользователя $username сейчас")

        val variables = mapOf(
            "name" to request.name,
            "surname" to request.surname,
            "phone" to request.phone,
            "receiptMethod" to request.receiptMethod.name,
            "deliveryAddress" to request.deliveryAddress,
            "paymentMethod" to request.paymentMethod.name
        )

        taskService.complete(task.id, variables)

        return ResponseEntity.ok("Данные заказа приняты. Процесс переходит к созданию заказа.")
    }

    @Operation(summary = "Детали заказа", description = "Возвращает информацию о конкретном заказе по ID")
    @GetMapping("/{orderId}")
    fun getOrder(
        authentication: Authentication,
        @PathVariable orderId: Long
    ): ResponseEntity<OrderResponse> {
        val username = authentication.name
        val order = orderService.getOrder(orderId, username)

        return ResponseEntity.ok(order.toResponse())
    }
}
