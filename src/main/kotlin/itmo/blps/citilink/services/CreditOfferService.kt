package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer

interface CreditOfferService {
    fun getCreditOffers(application: CreditApplication): List<CreditOffer>
    fun getCreditOfferById(offerId: Long): CreditOffer?
}