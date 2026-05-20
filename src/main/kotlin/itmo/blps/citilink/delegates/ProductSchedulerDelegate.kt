package itmo.blps.citilink.delegates

import itmo.blps.citilink.repositories.ProductRepository
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.camunda.bpm.engine.delegate.JavaDelegate

@Profile("shop")
@Service()
class ProductSchedulerDelegate(private val productRepository: ProductRepository): JavaDelegate {

    @Transactional
    override fun execute(execution: DelegateExecution) {
        println(">>> Camunda: Started updating day's products by timer...")

        val currentProducts = productRepository.findAll()
        currentProducts.forEach { it.isProductOfDay = false }

        if (currentProducts.isNotEmpty()) {
            val randomProducts = currentProducts.shuffled().take(3)
            randomProducts.forEach { it.isProductOfDay = true }
            productRepository.saveAll(currentProducts)
            println(">>> Camunda: New day's products selected!")
        }
    }
}