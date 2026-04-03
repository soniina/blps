package itmo.blps.citilink.dto.responses

import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication

data class CreditApplicationResponse(
    val id: Long,
    val orderId: Long,
    val status: ApplicationStatus,
    val termMonths: Int,
    val initialPayment: Double,
    val passportSeries: String,
    val passportNumber: String,
    val email: String,
    val phone: String,
    val selectedOffer: CreditOfferResponse? = null
)

fun CreditApplication.toResponse() = CreditApplicationResponse(
    id = requireNotNull(id) { "CreditApplication ID must not be null" },
    orderId = requireNotNull(order.id) { "CreditApplication's Order ID must not be null" },
    status = status,
    termMonths = termMonths,
    initialPayment = initialPayment,
    passportSeries = passportSeries,
    passportNumber = passportNumber,
    email = email,
    phone = phone,
    selectedOffer = selectedOffer?.toResponse()
)
