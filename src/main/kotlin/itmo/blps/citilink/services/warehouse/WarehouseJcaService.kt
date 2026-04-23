package itmo.blps.citilink.services.warehouse

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File

@Service
class WarehouseJcaService {

    @Transactional
    fun reserveProduct(orderId: String, productId: Long, quantity: Int) {
        println("JCA warehouse: checking stock for product $productId (requested: $quantity)")

        // имитация отказа: товара 999 никогда нет в наличии
        if (productId == 999L) {
            println("JCA warehouse: product $productId is out of stock")
            throw RuntimeException("Warehouse error: not enough items in stock for product $productId")
        }

        try {
            val logFile = File("warehouse_external_system.txt")
            logFile.appendText("TRANSACTION_PENDING | ORDER: $orderId | PRODUCT: $productId | QTY: $quantity | STATUS: RESERVED\n")
            println("JCA warehouse: successfully reserved via JCA connector")
        } catch (e: Exception) {
            throw RuntimeException("External System is unreachable")
        }
    }
}