package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditService
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Панель оператора", description = "Интерфейс для сотрудников по подписанию кредитных договоров")
@Profile("shop")
@RestController
@RequestMapping("/operator")
class OperatorController(private val creditService: CreditService) {

    @Operation(summary = "Список заявок", description = "Возвращает все заявки, ожидающие офлайн-подписания договора")
    @GetMapping("/applications")
    fun getDashboard(): ResponseEntity<List<CreditApplicationResponse>> {
        val pendingApplications = creditService.getApplicationsForOperator()

        return ResponseEntity.ok(pendingApplications.map { it.toResponse() })
    }

    @Operation(summary = "Одобрить заявку", description = "Подтверждение офлайн-подписания документов")
    @PostMapping("/applications/{applicationId}/approve")
    fun approveApplication(@PathVariable applicationId: Long): ResponseEntity<CreditApplicationResponse> {
        val application = creditService.approveOfflineSigning(applicationId)

        return ResponseEntity.ok(application.toResponse())
    }

}
