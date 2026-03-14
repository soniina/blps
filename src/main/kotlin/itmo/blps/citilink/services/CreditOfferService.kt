package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import java.util.UUID

interface CreditOfferService {
    fun getCreditOffers(application: CreditApplication): List<CreditOffer>
    fun getCreditOfferByUid(offerUid: UUID): CreditOffer?
}