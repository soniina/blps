package itmo.blps.citilink

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CitilinkApplication

fun main(args: Array<String>) {
    runApplication<CitilinkApplication>(*args)
}
