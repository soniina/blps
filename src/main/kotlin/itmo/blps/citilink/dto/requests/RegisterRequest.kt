package itmo.blps.citilink.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

enum class UserRole {
    AUTHORIZED,
    OPERATOR
}

data class RegisterRequest(
    @field:NotBlank(message = "Имя пользователя обязательно")
    @field:Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов")
    val username: String = "",

    @field:NotBlank(message = "Пароль обязателен")
    @field:Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    val password: String = "",

    private val roleField: UserRole? = null
) {
    val role: UserRole
        get() = roleField ?: UserRole.AUTHORIZED
}

