package itmo.blps.citilink

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CitilinkApplication

fun main(args: Array<String>) {
    System.setProperty("java.security.auth.login.config", "src/main/resources/jaas.config")
    runApplication<CitilinkApplication>(*args)
}
//fun main(args: Array<String>) {
//    val encoder = org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
//    println("GENERATING HASHES")
//    println("Hash for 'password123': ${encoder.encode("password123")}")
//    println("Hash for 'admin_pass': ${encoder.encode("admin_pass")}")
//    println("END GENERATING")
//
//    System.setProperty("java.security.auth.login.config", "src/main/resources/jaas.config")
//    runApplication<CitilinkApplication>(*args)
//}