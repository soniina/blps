package itmo.blps.citilink.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreditApplicationRequest (

    @field:NotNull(message = "Выберите срок кредита")
    @field:Min(3) @field:Max(36)
    var termMonths: Int? = 6,

    @field:NotNull(message = "Введите сумму взноса")
    @field:Min(0)
    var initialPayment: Double? = 0.0,

    @field:NotBlank(message = "Укажите серию паспорта")
    @field:Pattern(regexp = "^\\d{4}$", message = "Серия — 4 цифры")
    var passportSeries: String? = null,

    @field:NotBlank(message = "Укажите номер паспорта")
    @field:Pattern(regexp = "^\\d{6}$", message = "Номер — 6 цифр")
    var passportNumber: String? = null,

    @field:NotBlank(message = "Электронная почта обязательна")
    @field:Email(message = "Неверный формат почты")
    var email: String? = null,

    @field:NotBlank(message = "Телефон обязателен")
    @field:Pattern(regexp = "^(\\+7|8)[0-9]{10}$", message = "Формат: +79991234567")
    var phone: String? = null
)
