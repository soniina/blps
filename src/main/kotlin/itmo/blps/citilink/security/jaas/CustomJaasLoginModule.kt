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
        println("JAAS: user login attempt: $inputUser")

        try {
            val inputUser = (callbacks[0] as NameCallback).name
            val inputPass = String((callbacks[1] as PasswordCallback).password)

            println("JAAS: login attempt for user: $inputUser")

            val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("users.xml")
                ?: this::class.java.classLoader.getResourceAsStream("users.xml")
                ?: throw Exception("File users.xml not found in classpath!")

            val xmlMapper = com.fasterxml.jackson.dataformat.xml.XmlMapper.builder()
                .addModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
                .build()

            val usersList = xmlMapper.readValue(inputStream, UsersList::class.java)

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
                println("JAAS: user founded. Role: ${foundUser.role}")
                username = foundUser.username
                role = foundUser.role
                loginSucceeded = true
                return true
            } else {
                println("JAAS: user not founded or password incorrect")
            }

        } catch (e: Exception) {
            println("JAAS error: ${e.message}")
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