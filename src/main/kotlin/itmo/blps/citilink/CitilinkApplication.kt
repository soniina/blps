package itmo.blps.citilink

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CitilinkApplication

fun main(args: Array<String>) {
    System.setProperty("java.security.auth.login.config", "src/main/resources/jaas.config")
    runApplication<CitilinkApplication>(*args)
}
