package itmo.blps.citilink.services.warehouse

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.naming.InitialContext
import java.io.PrintWriter
import java.io.File

@Service
class WarehouseJcaService {

    @Transactional
    fun reserveProduct(orderId: String, productId: Long, quantity: Int) {
        println("JCA Warehouse: attempt to reserve the item $productId in count $quantity for order $orderId")

        try {
            // на самом деле JNDI lookup:
            // val ctx = InitialContext()
            // val cf = ctx.lookup("java:/eis/WarehouseConnector")

            // имитация работы JCA-адаптера (запись во внешний "реестр" склада)
            val logFile = File("warehouse_external_system.txt")
            logFile.appendText("ORDER: $orderId | PRODUCT: $productId | QTY: $quantity | STATUS: RESERVED\n")

            println("JCA Warehouse: successfully booked via JCA connector")
        } catch (e: Exception) {
            println("JCA Warehouse: error, external system unavailable")
            throw e // Чтобы JTA откатил транзакцию в нашей БД
        }
    }
}