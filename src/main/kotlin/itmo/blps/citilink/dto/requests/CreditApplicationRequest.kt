package itmo.blps.citilink.dto.requests

import jakarta.validation.constraints.*

data class CreditApplicationRequest(

    @field:NotNull(message = "Введите срок кредита")
    @field:Min(3) @field:Max(36)
    val termMonths: Int,

    @field:NotNull(message = "Введите сумму взноса")
    @field:Min(0)
    val initialPayment: Double,

    @field:NotBlank(message = "Укажите серию паспорта")
    @field:Pattern(regexp = "^\\d{4}$", message = "Серия — 4 цифры")
    val passportSeries: String,

    @field:NotBlank(message = "Укажите номер паспорта")
    @field:Pattern(regexp = "^\\d{6}$", message = "Номер — 6 цифр")
    val passportNumber: String,

    @field:NotBlank(message = "Электронная почта обязательна")
    @field:Email(message = "Неверный формат почты")
    val email: String,

    @field:NotBlank(message = "Телефон обязателен")
    @field:Pattern(regexp = "^(\\+7|8)[0-9]{10}$", message = "Формат: +79991234567")
    val phone: String
)
