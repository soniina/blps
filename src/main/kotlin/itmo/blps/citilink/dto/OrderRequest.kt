package itmo.blps.citilink.dto

import itmo.blps.citilink.models.PaymentMethod
import itmo.blps.citilink.models.ReceiptMethod
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OrderRequest (
    @field:NotBlank(message = "Имя обязательно")
    var name: String? = null,

    @field:NotBlank(message = "Фамилия обязательна")
    var surname: String? = null,

    @field:NotBlank(message = "Телефон обязателен")
    @field:Pattern(regexp = "^(\\+7|8)[0-9]{10}$", message = "Неверный формат телефона")
    var phone: String? = null,

    var receiptMethod: ReceiptMethod = ReceiptMethod.PICKUP,

    var deliveryAddress: String? = null,

    val paymentMethod: PaymentMethod = PaymentMethod.CREDIT,
) {
    @get:AssertTrue(message = "При выборе доставки необходимо указать адрес")
    val deliveryAddressValid: Boolean
        get() = if (receiptMethod == ReceiptMethod.DELIVERY) !deliveryAddress.isNullOrBlank()
                else true
}
