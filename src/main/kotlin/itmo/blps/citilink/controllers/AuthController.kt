package itmo.blps.citilink.controllers

import itmo.blps.citilink.security.jaas.RolePrincipal
import itmo.blps.citilink.security.jwt.JwtProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.security.auth.login.LoginContext
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.PasswordCallback

data class LoginRequest(val username: String, val password: String)

@RestController
@RequestMapping("/auth")
class AuthController(private val jwtProvider: JwtProvider) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        try {
            val callbackHandler = CallbackHandler { callbacks ->
                for (callback in callbacks) {
                    when (callback) {
                        is NameCallback -> callback.name = request.username
                        is PasswordCallback -> callback.password = request.password.toCharArray()
                    }
                }
            }
            val loginContext = LoginContext("CitilinkLogin", callbackHandler)

            loginContext.login()

            val subject = loginContext.subject
            val role = subject.getPrincipals(RolePrincipal::class.java).first().name

            val token = jwtProvider.generateToken(request.username, role)

            return ResponseEntity.ok(mapOf("token" to token))

        } catch (e: Exception) {
            println("Ошибка авторизации: ${e.message}")
            e.printStackTrace()
            return ResponseEntity.status(401).body("Неверный логин или пароль")
        }
    }
}