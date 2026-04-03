package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.responses.CreditApplicationResponse
import itmo.blps.citilink.dto.responses.toResponse
import itmo.blps.citilink.services.CreditService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/operator")
class OperatorController(private val creditService: CreditService) {

    @GetMapping("/applications")
    fun getDashboard(): ResponseEntity<List<CreditApplicationResponse>> {
        val pendingApplications = creditService.getApplicationsForOperator()

        return ResponseEntity.ok(pendingApplications.map { it.toResponse() })
    }

    @PostMapping("/applications/{applicationId}/approve")
    fun approveApplication(@PathVariable applicationId: Long): ResponseEntity<CreditApplicationResponse> {
        val application = creditService.approveOfflineSigning(applicationId)

        return ResponseEntity.ok(application.toResponse())
    }

}
