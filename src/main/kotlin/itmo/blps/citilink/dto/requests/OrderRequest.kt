package itmo.blps.citilink.dto.requests

import itmo.blps.citilink.models.PaymentMethod
import itmo.blps.citilink.models.ReceiptMethod
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OrderRequest(
    @field:NotBlank(message = "Имя обязательно")
    val name: String,

    @field:NotBlank(message = "Фамилия обязательна")
    val surname: String,

    @field:NotBlank(message = "Телефон обязателен")
    @field:Pattern(regexp = "^(\\+7|8)[0-9]{10}$", message = "Неверный формат телефона")
    val phone: String,

    val receiptMethod: ReceiptMethod = ReceiptMethod.PICKUP,

    val deliveryAddress: String? = null,

    val paymentMethod: PaymentMethod = PaymentMethod.CREDIT,
) {
    @get:AssertTrue(message = "При выборе доставки необходимо указать адрес")
    val deliveryAddressValid: Boolean
        get() {
            if (receiptMethod == ReceiptMethod.DELIVERY) return !deliveryAddress.isNullOrBlank()
            return true
        }
}
