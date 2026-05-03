package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.responses.CartResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CartService
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.task.Task
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "Корзина", description = "Управление товарами в корзине пользователя")
@Profile("shop")
@RestController
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService,
    private val taskService: TaskService,
    private val runtimeService: RuntimeService
) {

    private fun findTask(username: String, taskDefinitionKey: String): Task {
        return taskService.createTaskQuery()
            .processInstanceBusinessKey(username)
            .taskDefinitionKey(taskDefinitionKey)
            .active()
            .singleResult()
            ?: throw IllegalStateException("Задача $taskDefinitionKey не найдена для пользователя $username. Сначала зайдите на витрину!")
    }

    @Operation(
        summary = "Получить корзину",
        description = "Возвращает текущий состав корзины авторизованного пользователя"
    )
    @GetMapping
    fun getCart(authentication: Authentication): ResponseEntity<CartResponse> {
        val username = authentication.name
        val cart = cartService.getOrCreateCart(username)
        val items = cartService.getCartItems(cart)

        return ResponseEntity.ok(cart.toResponse(items))
    }

    @Operation(
        summary = "Добавить товар",
        description = "Мы на Витрине, добавляем товар и переходим в Корзину"
    )
    @PostMapping("/items")
    fun addCartItem(
        authentication: Authentication,
        @RequestParam productId: Long
    ): ResponseEntity<CartResponse> {
        val username = authentication.name

        val task = findTask(username, "BrowsingTask")

        taskService.complete(
            task.id, mapOf(
                "action" to "ADD",
                "productId" to productId
            )
        )

        return getCart(authentication)
    }

    @Operation(summary = "Удалить товар", description = "Мы в корзине (CartTask), удаляем позицию и остаемся в ней")
    @DeleteMapping("/items/{itemId}")
    fun removeCartItem(authentication: Authentication, @PathVariable itemId: Long): ResponseEntity<CartResponse> {
        val task = findTask(authentication.name, "CartTask")

        taskService.complete(
            task.id, mapOf(
                "action" to "REMOVE",
                "itemId" to itemId,
                "target" to "STAY"
            )
        )

        return getCart(authentication)
    }


    @Operation(
        summary = "Изменить количество",
        description = "Мы в корзине (CartTask), обновляем количество товара и остаемся в ней"
    )
    @PatchMapping("/items/{itemId}")
    fun updateQuantity(
        authentication: Authentication,
        @PathVariable itemId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<CartResponse> {
        val task = findTask(authentication.name, "CartTask")

        taskService.complete(
            task.id, mapOf(
                "action" to "UPDATE",
                "itemId" to itemId,
                "quantity" to quantity,
                "target" to "STAY"
            )
        )
        return getCart(authentication)
    }

    @Operation(summary = "Вернуться к покупкам", description = "Переход из Корзины назад в Витрину")
    @PostMapping("/back-to-browsing")
    fun backToBrowsing(authentication: Authentication): ResponseEntity<Unit> {
        val username = authentication.name
        val task = findTask(username, "CartTask")

        taskService.complete(task.id, mapOf("target" to "BACK_TO_SHOPPING"))

        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Оформить заказ", description = "Выход из Корзины к Оформлению заказа")
    @PostMapping("/checkout")
    fun checkout(authentication: Authentication): ResponseEntity<String> {
        val task = findTask(authentication.name, "CartTask")

        taskService.complete(task.id, mapOf("target" to "GO_TO_ORDER"))
        return ResponseEntity.ok("Переход к оформлению...")
    }
}
