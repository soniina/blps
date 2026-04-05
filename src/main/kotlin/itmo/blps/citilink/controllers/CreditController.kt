package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.CreditOfferResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditOfferService
import itmo.blps.citilink.services.CreditService
import itmo.blps.citilink.services.OrderService
import itmo.blps.citilink.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/credit")
class CreditController(
    private val creditService: CreditService, private val orderService: OrderService,
    private val creditOfferService: CreditOfferService, private val userService: UserService
) {

    @PostMapping("/applications")
    fun processCredit(
        authentication: Authentication, // Берем из Security Context
        @Valid @RequestBody request: CreditApplicationRequest
    ): ResponseEntity<CreditApplicationResponse> {
        val user = userService.getOrCreateUser(authentication.name)
        val order = orderService.getOrder(request.orderId, user)

        val application = creditService.process(request, order)

        return ResponseEntity.status(HttpStatus.CREATED).body(application.toResponse())
    }

    @GetMapping("/applications/{applicationId}/offers")
    fun getOffers(
        authentication: Authentication,
        @PathVariable applicationId: Long
    ): ResponseEntity<List<CreditOfferResponse>> {
        val user = userService.getOrCreateUser(authentication.name)
        val application = creditService.getCreditApplication(applicationId, user)

        val offers = creditOfferService.getCreditOffers(application)
        return ResponseEntity.ok(offers.map { it.toResponse() })
    }


    @PostMapping("/offers/{offerId}/select")
    fun selectOffer(
        authentication: Authentication,
        @PathVariable offerId: Long
    ): ResponseEntity<CreditApplicationResponse> {
        val user = userService.getOrCreateUser(authentication.name)
        val offer = creditOfferService.getCreditOffer(offerId, user)

        creditService.selectOffer(offer.application, offer)

        return ResponseEntity.ok(offer.application.toResponse())
    }

    @PostMapping("/applications/{applicationId}/sign")
    fun signApplication(
        authentication: Authentication,
        @PathVariable applicationId: Long
    ): ResponseEntity<CreditApplicationResponse> {
        val user = userService.getOrCreateUser(authentication.name)
        val application = creditService.getCreditApplication(applicationId, user)

        creditService.signApplication(application)

        return ResponseEntity.ok(application.toResponse())
    }

}
