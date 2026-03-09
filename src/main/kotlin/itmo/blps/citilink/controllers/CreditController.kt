package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.services.CreditOfferService
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

@Controller
@RequestMapping("/credit")
class CreditController(private val creditService: CreditService, private val orderService: OrderService, private val creditOfferService: CreditOfferService) {

    @GetMapping("/{orderId}")
    fun showApplicationPage(@PathVariable orderId: Long, model: Model): String {
        val order = orderService.getOrderById(orderId) ?: return "redirect:/"

        model.addAttribute("creditRequest", CreditApplicationRequest(orderId = orderId))
        model.addAttribute("order", order)

        return "credit/apply"
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
            return "credit/apply"
        }

        val creditApplication = creditService.process(request, order)

        return "redirect:/credit/offers/${creditApplication.id}"
    }

    @GetMapping("/offers/{applicationId}")
    fun listCreditOffers(
        @PathVariable applicationId: Long,
        model: Model): String {
        val creditApplication = creditService.getCreditApplicationById(applicationId) ?: return "redirect:/"

        val offers = creditOfferService.getCreditOffers(creditApplication)

        val order = creditApplication.order
        model.addAttribute("order", order)
        model.addAttribute("offers", offers)

        return "credit/offers"
    }

}
