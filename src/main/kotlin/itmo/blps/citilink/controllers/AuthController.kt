package itmo.blps.citilink.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import itmo.blps.citilink.dto.requests.LoginRequest
import itmo.blps.citilink.dto.requests.RegisterRequest
import itmo.blps.citilink.configs.JaasConfig
import itmo.blps.citilink.security.jaas.RolePrincipal
import itmo.blps.citilink.security.jwt.JwtProvider
import itmo.blps.citilink.security.model.UserXmlModel
import itmo.blps.citilink.security.model.UsersList
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.login.LoginContext

@Tag(name = "Авторизация", description = "Регистрация и вход в систему")
@RestController
@RequestMapping("/auth")
class AuthController(private val jwtProvider: JwtProvider, private val passwordEncoder: PasswordEncoder) {

    private val xmlMapper = com.fasterxml.jackson.dataformat.xml.XmlMapper.builder()
        .addModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
        .build()

    private val xmlFile = File(JaasConfig.USERS_XML_PATH)

    @Operation(summary = "Вход в систему", description = "Проверяет учетные данные через JAAS и возвращает JWT токен")
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
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
            return ResponseEntity.status(401).body("Ошибка при авторизации: ${e.message}")
        }
    }

    @Operation(summary = "Регистрация", description = "Создает нового пользователя и сохраняет его в XML файл")
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<Any> {
        try {
            val usersList = if (xmlFile.exists()) {
                xmlMapper.readValue(xmlFile, UsersList::class.java)
            } else {
                UsersList(mutableListOf())
            }

            if (usersList.users.any { it.username == request.username }) {
                return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует")
            }

            val newUser = UserXmlModel(
                username = request.username,
                password = passwordEncoder.encode(request.password)!!,
                role = request.role.name
            )

            usersList.users.add(newUser)
            xmlMapper.writeValue(xmlFile, usersList)

            return ResponseEntity.ok("Пользователь успешно зарегистрирован")

        } catch (e: Exception) {
            return ResponseEntity.internalServerError().body("Ошибка при регистрации: ${e.message}")
        }
    }
}
