package itmo.blps.citilink

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CitilinkApplication : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(CitilinkApplication::class.java)
    }
}

fun main(args: Array<String>) {
    System.setProperty("java.security.auth.login.config", "src/main/resources/jaas.config")
    runApplication<CitilinkApplication>(*args)
}
