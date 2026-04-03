package itmo.blps.citilink.dto.responses

import itmo.blps.citilink.models.CreditOffer

data class CreditOfferResponse(
    val id: Long,
    val bankName: String,
    val interestRate: Double,
    val isOnlineSigningAvailable: Boolean
)

fun CreditOffer.toResponse() = CreditOfferResponse(
    id = requireNotNull(id) { "CreditOffer ID must not be null" },
    bankName = bankName,
    interestRate = interestRate,
    isOnlineSigningAvailable = isOnlineSigningAvailable
)
