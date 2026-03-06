package itmo.blps.citilink.controllers

import itmo.blps.citilink.dto.OrderRequest
import itmo.blps.citilink.models.PaymentMethod
import itmo.blps.citilink.models.ReceiptMethod
import itmo.blps.citilink.services.CartService
import itmo.blps.citilink.services.OrderService
import itmo.blps.citilink.services.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute

@Controller
@RequestMapping("/checkout")
class CheckoutController(private val userService: UserService, private val cartService: CartService, private val orderService: OrderService) {

    @GetMapping
    fun checkoutPage(
        @CookieValue(value = "session_id", required = true) sessionId: String,
        model: Model): String {
        val user = userService.getUser(sessionId) ?: return "redirect:/"
        val cartItems = cartService.getCartItems(user)

        if (cartItems.isEmpty()) return "redirect:/cart"

        val itemsPrice = cartItems.sumOf { it.product.price * it.quantity }

        model.addAttribute("orderRequest", OrderRequest())
        model.addAttribute("cartItemsCount", cartItems.sumOf { it.quantity })
        model.addAttribute("itemsPrice", itemsPrice)
        model.addAttribute("deliveryFee", orderService.calculateDeliveryPrice(ReceiptMethod.DELIVERY, itemsPrice))

        return "checkout"
    }

    @PostMapping
    fun orderProcess(
        @CookieValue(value = "session_id", required = true) sessionId: String,
        @Valid @ModelAttribute("orderRequest") request: OrderRequest,
        bindingResult: BindingResult,
        model: Model
    ) : String {
        val user = userService.getUser(sessionId) ?: return "redirect:/"
        val cartItems = cartService.getCartItems(user)

        if (bindingResult.hasErrors()) {
            val itemsPrice = cartItems.sumOf { it.product.price * it.quantity }
            model.addAttribute("cartItemsCount", cartItems.sumOf { it.quantity })
            model.addAttribute("itemsPrice", itemsPrice)
            model.addAttribute("deliveryFee", orderService.calculateDeliveryPrice(ReceiptMethod.DELIVERY, itemsPrice))
            return "checkout"
        }

        val order = orderService.process(request, user, cartItems)

        return when (order.paymentMethod) {
            PaymentMethod.CREDIT -> "redirect:/credit/${order.id}"
        }
    }

}