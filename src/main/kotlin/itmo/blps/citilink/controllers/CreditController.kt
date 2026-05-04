package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.CreditOfferResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditOfferService
import itmo.blps.citilink.services.CreditService
import jakarta.validation.Valid
import org.camunda.bpm.engine.TaskService
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "Кредитование", description = "Оформление кредитных заявок и выбор предложений")
@Profile("shop")
@RestController
@RequestMapping("/credit")
class CreditController(
    private val creditService: CreditService,
    private val taskService: TaskService,
    private val creditOfferService: CreditOfferService
) {

    @Operation(
        summary = "Создать заявку на кредит",
        description = "Передает данные в процесс для создания заявки на основе существующего заказа"
    )
    @PostMapping("/applications")
    fun processCredit(
        authentication: Authentication,
        @Valid @RequestBody request: CreditApplicationRequest
    ): ResponseEntity<String> {
        val username = authentication.name

        val task = taskService.createTaskQuery()
            .processInstanceBusinessKey(username)
            .taskDefinitionKey("CreditDetailsTask")
            .active()
            .singleResult() ?: return ResponseEntity.badRequest().body("Задача создания кредитной заявки не активна")

        val variables = mapOf(
            "termMonths" to request.termMonths,
            "initialPayment" to request.initialPayment,
            "passportSeries" to request.passportSeries,
            "passportNumber" to request.passportNumber,
            "email" to request.email,
            "phone" to request.phone
        )

        taskService.complete(task.id, variables)

        return ResponseEntity.ok("Данные для кредита приняты. Заявка формируется и отправляется в банки.")
    }

    @Operation(
        summary = "Получить информацию о заявке",
        description = "Возвращает текущие данные и статус конкретной кредитной заявки"
    )
    @GetMapping("/applications/{applicationId}")
    fun getApplication(
        authentication: Authentication,
        @PathVariable applicationId: Long
    ): ResponseEntity<CreditApplicationResponse> {
        val username = authentication.name
        val application = creditService.getCreditApplication(applicationId, username)

        return ResponseEntity.ok(application.toResponse())
    }

    @Operation(
        summary = "Список предложений",
        description = "Возвращает доступные предложения банков по конкретной заявке"
    )
    @GetMapping("/applications/{applicationId}/offers")
    fun getOffers(
        authentication: Authentication,
        @PathVariable applicationId: Long
    ): ResponseEntity<List<CreditOfferResponse>> {
        val username = authentication.name
        val application = creditService.getCreditApplication(applicationId, username)

        val offers = creditOfferService.getCreditOffers(application)
        return ResponseEntity.ok(offers.map { it.toResponse() })
    }

    @Operation(summary = "Выбрать предложение", description = "Фиксация выбора конкретного банковского предложения")
    @PostMapping("/offers/{offerId}/select")
    fun selectOffer(
        authentication: Authentication,
        @PathVariable offerId: Long
    ): ResponseEntity<CreditApplicationResponse> {
        val username = authentication.name
        val offer = creditOfferService.getCreditOffer(offerId, username)

        creditService.selectOffer(offer.application, offer)

        return ResponseEntity.ok(offer.application.toResponse())
    }

    @Operation(summary = "Подписать заявку", description = "Онлайн подписание кредитного договора пользователем")
    @PostMapping("/applications/{applicationId}/sign")
    fun signApplication(
        authentication: Authentication,
        @PathVariable applicationId: Long
    ): ResponseEntity<CreditApplicationResponse> {
        val username = authentication.name
        val application = creditService.getCreditApplication(applicationId, username)

        creditService.signApplication(application)

        return ResponseEntity.ok(application.toResponse())
    }

}
