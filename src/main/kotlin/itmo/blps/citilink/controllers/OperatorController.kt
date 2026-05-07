package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditService
import org.camunda.bpm.engine.TaskService
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Панель оператора", description = "Интерфейс для сотрудников по подписанию кредитных договоров")
@Profile("shop")
@RestController
@RequestMapping("/operator")
class OperatorController(
    private val taskService: TaskService,
    private val creditService: CreditService
) {

    @Operation(summary = "Список заявок", description = "Возвращает все заявки, ожидающие офлайн-подписания договора")
    @GetMapping("/applications")
    fun getDashboard(): ResponseEntity<List<CreditApplicationResponse>> {
        val tasks = taskService.createTaskQuery()
            .taskDefinitionKey("OperatorSigningTask")
            .active()
            .list()

        val responses = tasks.map { task ->
            val applicationId = taskService.getVariable(task.id, "applicationId") as Long
            creditService.getApplicationForOperator(applicationId).toResponse()
        }

        return ResponseEntity.ok(responses)
    }

    @Operation(summary = "Одобрить заявку", description = "Подтверждение офлайн-подписания документов")
    @PostMapping("/applications/{applicationId}/approve")
    fun approveApplication(@PathVariable applicationId: Long): ResponseEntity<String> {
        val task = taskService.createTaskQuery()
            .taskDefinitionKey("OperatorSigningTask")
            .processVariableValueEquals("applicationId", applicationId)
            .active()
            .singleResult() ?: throw IllegalStateException("Задача для заявки $applicationId не найдена")

        taskService.complete(task.id)

        return ResponseEntity.ok("Оффлайн подписание подтверждено.")
    }
}
