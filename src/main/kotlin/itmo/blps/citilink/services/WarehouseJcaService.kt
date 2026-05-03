package itmo.blps.citilink.services

import itmo.blps.citilink.models.CartItem
import itmo.blps.citilink.warehouse.WarehouseConnection
import itmo.blps.citilink.warehouse.WarehouseConnectionFactory
import jakarta.annotation.Resource
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("shop")
@Service
class WarehouseJcaService {

    @Resource(mappedName = "java:/eis/WarehouseConnector")
    private lateinit var jiraFactory: WarehouseConnectionFactory

    fun reserveOrder(orderId: Long, items: List<CartItem>): String {
        val connection = jiraFactory.connection as WarehouseConnection
        try {
            return connection.createJiraIssue(
                "[RESERVE] Order №$orderId",
                "Items to reserve: " + items.joinToString { "${it.product.name} (x${it.quantity})" })
        } finally {
            connection.close()
        }
    }

    fun startAssembly(orderId: Long) {
        val connection = jiraFactory.connection as WarehouseConnection
        try {
            connection.createJiraIssue(
                "[ASSEMBLY] Order №$orderId",
                "Proceed with assembly and shipping."
            )
        } finally {
            connection.close()
        }
    }

    fun cancelReservation(ticketKey: String) {
        val connection = jiraFactory.connection as WarehouseConnection
        try {
            connection.deleteJiraIssue(ticketKey)
        } catch (e: Exception) {
            println("CRITICAL ERROR: Could not delete Jira ticket $ticketKey: ${e.message}")
        } finally {
            connection.close()
        }
    }
}