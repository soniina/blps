package itmo.blps.citilink.controllers

import itmo.blps.citilink.services.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun listProductsOfDay(model: Model): String {
        model.addAttribute("productsOfDay", productService.getProductsOfDay())
        model.addAttribute("cartItemsCount", 0)
        return "index"
    }
}