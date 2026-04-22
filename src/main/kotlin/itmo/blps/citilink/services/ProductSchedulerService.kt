package itmo.blps.citilink.services

import itmo.blps.citilink.repositories.ProductRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductSchedulerService(private val productRepository: ProductRepository) {

    @Scheduled(fixedRate = 300000) // 5 минут
    @Transactional
    fun updateProductsOfDay() {
        println("Planner: updating of day's product")

        // снимаем статус "Товар дня" со всех текущих товаров
        val currentProducts = productRepository.findAll()
        currentProducts.forEach { it.isProductOfDay = false }

        // 3 случайных товара из списка
        if (currentProducts.isNotEmpty()) {
            val randomProducts = currentProducts.shuffled().take(3)
            randomProducts.forEach { it.isProductOfDay = true }
            productRepository.saveAll(currentProducts)
            println("Планировщик: Новые товары дня выбраны успешно.")
        }
    }
}