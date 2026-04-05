package itmo.blps.citilink.security.jaas

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import itmo.blps.citilink.security.model.UsersList
import java.io.File
import javax.security.auth.Subject
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.spi.LoginModule
import javax.security.auth.callback.Callback
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class CustomJaasLoginModule : LoginModule {
    private var role: String? = null
    private lateinit var subject: Subject
    private lateinit var callbackHandler: CallbackHandler
    private var loginSucceeded = false
    private var username: String? = null

    override fun initialize(subject: Subject, callbackHandler: CallbackHandler, sharedState: Map<String, *>, options: Map<String, *>) {
        this.subject = subject
        this.callbackHandler = callbackHandler
    }

    override fun login(): Boolean {
        val callbacks: Array<Callback> = arrayOf(
            NameCallback("username"),
            PasswordCallback("password", false)
        )
        val encoder = org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()

        callbackHandler.handle(callbacks)

        try {
            val inputUser = (callbacks[0] as NameCallback).name
            val inputPass = String((callbacks[1] as PasswordCallback).password)

            println("JAAS: Попытка входа пользователя: $inputUser")
            val inputStream = this::class.java.classLoader.getResourceAsStream("users.xml")
                ?: Thread.currentThread().contextClassLoader.getResourceAsStream("users.xml")
                ?: throw Exception("Файл users.xml не найден!")

            //val xmlMapper = com.fasterxml.jackson.dataformat.xml.XmlMapper()
            val xmlMapper = XmlMapper.builder()
                .addModule(KotlinModule.Builder().build())
                .build()
            //val usersList = xmlMapper.readValue(inputStream, UsersList::class.java)
            val usersList = xmlMapper.readValue(inputStream, UsersList::class.java)
            usersList.users.forEach {
                println("DEBUG XML: Прочитан юзер [${it.username}], роль [${it.role}]")
            }

            println("JAAS: Загружено пользователей из XML: ${usersList.users.size}")
            //val foundUser = usersList.users.find { it.username == inputUser && it.password == inputPass }
//            val foundUser = usersList.users.find {
//                it.username == inputUser && encoder.matches(inputPass, it.password)
//            }
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
            e.printStackTrace()
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