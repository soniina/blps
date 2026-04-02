package itmo.blps.citilink.controllers

import org.springframework.ui.Model
import itmo.blps.citilink.services.CreditService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/operator")
class OperatorController(private val creditService: CreditService) {
    @GetMapping("/dashboard")
    fun showDashboard(model: Model): String {
        val pendingApplications = creditService.getApplicationsForOperator()
        model.addAttribute("applications", pendingApplications)
        return "operator/dashboard"
    }

    @PostMapping("/approve/{id}")
    fun approveApplication(@PathVariable id: Long): String {
        creditService.approveOfflineSigning(id)
        return "redirect:/operator/dashboard"
    }
}