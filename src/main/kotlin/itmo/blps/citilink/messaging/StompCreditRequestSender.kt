package itmo.blps.citilink.messaging

import org.apache.activemq.transport.stomp.StompConnection
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("shop")
class StompCreditRequestSender(
    @Value("\${app.stomp.host}") private val host: String,
    @Value("\${app.stomp.port}") private val port: Int,
    @Value("\${app.stomp.queue-name}") private val queueName: String,
    @Value("\${app.stomp.login}") private val login: String,
    @Value("\${app.stomp.pass}") private val pass: String
) {

    fun sendApplicationId(applicationId: Long?) {
        val applicationId = applicationId?.toString() ?: throw IllegalArgumentException(">>> STOMP ERROR: impossible to send null ID")

        val connection = StompConnection()
        try {
            println(">>> STOMP: Connecting to $host:$port...")

            connection.open(host, port)
            connection.connect(login, pass)

            println(">>> STOMP: The connection is established. Shipment ID $applicationId")

            connection.send(queueName, applicationId)

            connection.disconnect()

            println(">>> STOMP: The application was successfully submitted to $queueName")
        } catch (e: Exception) {
            println(">>> STOMP ERROR: Error when working with the queue: \${e.message}")
            e.printStackTrace()
        } finally {
            connection.close()
        }
    }
}