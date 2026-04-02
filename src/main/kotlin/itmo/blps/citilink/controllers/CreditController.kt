package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.models.ApplicationStatus
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

    @PostMapping("/select/{offerId}")
    fun selectOffer(
        @PathVariable offerId: Long,
        model: Model): String {

        val offer = creditOfferService.getCreditOfferById(offerId) ?: return "redirect:/"
        val application = offer.application

        creditService.selectOffer(application, offer)

        return if (offer.isOnlineSigningAvailable) {
            "redirect:/credit/sign-online/${application.id}"
        } else {
            creditService.updateStatus(application, ApplicationStatus.WAITING_FOR_OPERATOR)
            "redirect:/credit/wait-call/${application.id}"
        }
    }

    @GetMapping("/sign-online/{applicationId}")
    fun showSignOnline(
        @PathVariable applicationId: Long,
        model: Model): String {
        val application = creditService.getCreditApplicationById(applicationId) ?: return "redirect:/"

        model.addAttribute("creditApplication", application)
        model.addAttribute("order", application.order)
        return "credit/sign-online"
    }

    @PostMapping("/confirm-sign/{applicationId}")
    fun confirmSign(@PathVariable applicationId: Long): String {
        val application = creditService.getCreditApplicationById(applicationId) ?: return "redirect:/"

        creditService.signApplication(application)

        return "redirect:/checkout/success/${application.order.id}"
    }

    @GetMapping("/wait-call/{applicationId}")
    fun showWaitCall(@PathVariable applicationId: Long, model: Model): String {
        val application = creditService.getCreditApplicationById(applicationId) ?: return "redirect:/"

        model.addAttribute("creditApplication", application)

        return "credit/wait-call"
    }

}
