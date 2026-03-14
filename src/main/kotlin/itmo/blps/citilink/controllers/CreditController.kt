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
import java.util.UUID

@Controller
@RequestMapping("/credit")
class CreditController(private val creditService: CreditService, private val orderService: OrderService, private val creditOfferService: CreditOfferService) {

    @GetMapping("/{orderUid}")
    fun showApplicationPage(@PathVariable orderUid: UUID, model: Model): String {
        val order = orderService.getOrderByUid(orderUid) ?: return "redirect:/"

        model.addAttribute("creditRequest", CreditApplicationRequest())
        model.addAttribute("order", order)

        return "credit/apply"
    }

    @PostMapping("/{orderUid}")
    fun processCredit(
        @PathVariable orderUid: UUID,
        @Valid @ModelAttribute("creditRequest") request: CreditApplicationRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        val order = orderService.getOrderByUid(orderUid) ?: return "redirect:/"

        if (bindingResult.hasErrors()) {
            model.addAttribute("order", order)
            return "credit/apply"
        }

        val creditApplication = creditService.process(request, order)

        return "redirect:/credit/offers/${creditApplication.uid}"
    }

    @GetMapping("/offers/{applicationUid}")
    fun listCreditOffers(
        @PathVariable applicationUid: UUID,
        model: Model): String {
        val creditApplication = creditService.getCreditApplicationByUid(applicationUid) ?: return "redirect:/"

        val offers = creditOfferService.getCreditOffers(creditApplication)

        val order = creditApplication.order
        model.addAttribute("order", order)
        model.addAttribute("offers", offers)

        return "credit/offers"
    }

    @PostMapping("/select/{offerUid}")
    fun selectOffer(
        @PathVariable offerUid: UUID,
        model: Model): String {

        val offer = creditOfferService.getCreditOfferByUid(offerUid) ?: return "redirect:/"
        val application = offer.application

        creditService.selectOffer(application, offer)

        return if (offer.isOnlineSigningAvailable) {
            "redirect:/credit/sign-online/${application.uid}"
        } else {
            creditService.updateStatus(application, ApplicationStatus.WAITING_FOR_OPERATOR)
            "redirect:/credit/wait-call/${application.uid}"
        }
    }

    @GetMapping("/sign-online/{applicationUid}")
    fun showSignOnline(
        @PathVariable applicationUid: UUID,
        model: Model): String {
        val application = creditService.getCreditApplicationByUid(applicationUid) ?: return "redirect:/"

        model.addAttribute("creditApplication", application)
        model.addAttribute("order", application.order)
        return "credit/sign-online"
    }

    @PostMapping("/confirm-sign/{applicationUid}")
    fun confirmSign(@PathVariable applicationUid: UUID): String {
        val application = creditService.getCreditApplicationByUid(applicationUid) ?: return "redirect:/"

        creditService.signApplication(application)

        return "redirect:/checkout/success/${application.order.uid}"
    }

    @GetMapping("/wait-call/{applicationUid}")
    fun showWaitCall(@PathVariable applicationUid: UUID, model: Model): String {
        val application = creditService.getCreditApplicationByUid(applicationUid) ?: return "redirect:/"

        model.addAttribute("creditApplication", application)

        return "credit/wait-call"
    }

}
