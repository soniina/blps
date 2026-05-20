package itmo.blps.citilink.configs

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.io.File
import javax.security.auth.login.AppConfigurationEntry
import javax.security.auth.login.Configuration as JaasConfiguration

@Configuration
class JaasConfig {

    companion object {
        // Динамически определяем путь
        val USERS_XML_PATH: String by lazy {
            // Пытаемся взять папку данных WildFly (standalone/data)
            // Если её нет, берем текущую папку запуска приложения
            val baseDir = System.getProperty("jboss.server.data.dir") ?: "."
            val path = baseDir + File.separator + "users.xml"
            path
        }
    }

    @PostConstruct
    fun init() {
        val absolutePath = File(USERS_XML_PATH).absolutePath
        println(">>> JAAS: attempt to use user's file here: $absolutePath")

        val customConfig = object : JaasConfiguration() {
            override fun getAppConfigurationEntry(name: String): Array<AppConfigurationEntry>? {
                if (name == "CitilinkLogin") {
                    return arrayOf(
                        AppConfigurationEntry(
                            "itmo.blps.citilink.security.jaas.CustomJaasLoginModule",
                            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                            emptyMap<String, Any>()
                        )
                    )
                }
                return null
            }
        }

        JaasConfiguration.setConfiguration(customConfig)
        println("JAAS: program configuration of 'CitilinkLogin' successfully downloaded")
    }
}