package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.CreditOfferResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditOfferService
import itmo.blps.citilink.services.CreditService
import itmo.blps.citilink.services.OrderService
import jakarta.validation.Valid
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "Кредитование", description = "Оформление кредитных заявок и выбор предложений")
@Profile("shop")
@RestController
@RequestMapping("/credit")
class CreditController(
    private val creditService: CreditService, private val orderService: OrderService,
    private val creditOfferService: CreditOfferService
) {

    @Operation(summary = "Создать заявку на кредит", description = "Оформляет заявку на основе существующего заказа")
    @PostMapping("/applications")
    fun processCredit(
        authentication: Authentication,
        @Valid @RequestBody request: CreditApplicationRequest
    ): ResponseEntity<CreditApplicationResponse> {
        val username = authentication.name
        val order = orderService.getOrder(request.orderId, username)

        val application = creditService.process(request, order)

        return ResponseEntity.status(HttpStatus.CREATED).body(application.toResponse())
    }

    @Operation(summary = "Список предложений", description = "Возвращает доступные предложения банков по конкретной заявке")
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
