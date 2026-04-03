package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.models.User

interface CreditOfferService {
    fun getCreditOffers(application: CreditApplication): List<CreditOffer>
    fun saveCreditOffers(creditOffers: List<CreditOffer>)
    fun getCreditOffer(offerId: Long, user: User): CreditOffer
}
