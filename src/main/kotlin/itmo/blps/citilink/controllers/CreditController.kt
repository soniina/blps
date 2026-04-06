package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.CreditOfferResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditOfferService
import itmo.blps.citilink.services.CreditService
import itmo.blps.citilink.services.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/credit")
class CreditController(
    private val creditService: CreditService, private val orderService: OrderService,
    private val creditOfferService: CreditOfferService
) {

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
