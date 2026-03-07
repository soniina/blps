package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.services.CreditService
import itmo.blps.citilink.services.OrderService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/credit")
class CreditController(private val creditService: CreditService, private val orderService: OrderService) {

    @GetMapping("/{orderId}")
    fun showApplicationPage(@PathVariable orderId: Long, model: Model): String {
        val order = orderService.getOrderById(orderId) ?: return "redirect:/"

        model.addAttribute("creditRequest", CreditApplicationRequest(orderId = orderId))
        model.addAttribute("order", order)

        return "credit"
    }

    @PostMapping("/{orderId}")
    fun processCredit(
        @PathVariable orderId: Long,
        @Valid @ModelAttribute("creditRequest") request: CreditApplicationRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        val order = orderService.getOrderById(orderId) ?: return "redirect:/"

        if (bindingResult.hasErrors()) {
            model.addAttribute("order", order)
            return "credit"
        }

        creditService.process(request, order)

        return "redirect:/credit/offers/$orderId"
    }

}