package itmo.blps.citilink.configs

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.io.File
import javax.security.auth.login.AppConfigurationEntry
import javax.security.auth.login.Configuration as JaasConfiguration

@Configuration
class JaasConfig {

    companion object {
        val USERS_XML_PATH = System.getProperty("user.home") + File.separator + "users.xml"
    }

    @PostConstruct
    fun init() {
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
