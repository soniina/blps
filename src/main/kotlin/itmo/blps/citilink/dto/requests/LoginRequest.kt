package itmo.blps.citilink.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "Имя пользователя обязательно")
    @field:Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов")
    val username: String,

    @field:NotBlank(message = "Пароль обязателен")
    @field:Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    val password: String
)
