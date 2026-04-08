package itmo.blps.citilink.security.jaas

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import itmo.blps.citilink.configs.JaasConfig
import itmo.blps.citilink.security.model.UsersList
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.File
import javax.security.auth.Subject
import javax.security.auth.callback.Callback
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.spi.LoginModule

class CustomJaasLoginModule : LoginModule {
    private var role: String? = null
    private lateinit var subject: Subject
    private lateinit var callbackHandler: CallbackHandler
    private var loginSucceeded = false
    private var username: String? = null

    private val xmlMapper = XmlMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()
    private val encoder = BCryptPasswordEncoder()

    override fun initialize(
        subject: Subject,
        callbackHandler: CallbackHandler,
        sharedState: Map<String, *>,
        options: Map<String, *>
    ) {
        this.subject = subject
        this.callbackHandler = callbackHandler
    }

    override fun login(): Boolean {
        val callbacks: Array<Callback> = arrayOf(
            NameCallback("username"),
            PasswordCallback("password", false)
        )

        callbackHandler.handle(callbacks)

        val inputUser = (callbacks[0] as NameCallback).name
        val inputPass = String((callbacks[1] as PasswordCallback).password)
        println("JAAS: Попытка входа пользователя: $inputUser")

        try {
            val xmlFile = File(JaasConfig.USERS_XML_PATH)

            if (!xmlFile.exists()) {
                println("JAAS ОШИБКА: Файл ${xmlFile.absolutePath} не найден!")
                return false
            }

            val usersList = xmlMapper.readValue(xmlFile, UsersList::class.java)

            usersList.users.forEach {
                println("DEBUG XML: Прочитан юзер [${it.username}], роль [${it.role}]")
            }

            val foundUser = usersList.users.find {
                val usernameMatch = it.username.trim() == inputUser.trim()

                if (usernameMatch) {
                    val cleanXmlHash = it.password.replace("\\s".toRegex(), "")

                    println("--- FINAL DIAGNOSTIC ---")
                    println("Input: [$inputPass]")
                    println("XML Hash: [$cleanXmlHash]")

                    val isMatch = encoder.matches(inputPass, cleanXmlHash)
                    println("Is Match: $isMatch")

                    isMatch
                } else {
                    false
                }
            }

            if (foundUser != null) {
                println("JAAS: Пользователь найден. Роль: ${foundUser.role}")
                username = foundUser.username
                role = foundUser.role
                loginSucceeded = true
                return true
            } else {
                println("JAAS: Пользователь не найден или пароль неверный")
            }

        } catch (e: Exception) {
            println("JAAS ошибка: ${e.message}")
        }
        return false
    }

    override fun commit(): Boolean {
        if (!loginSucceeded) return false
        subject.principals.add(UserPrincipal(username!!))
        subject.principals.add(RolePrincipal(role!!))
        return true
    }

    override fun abort() = false
    override fun logout() = true
}