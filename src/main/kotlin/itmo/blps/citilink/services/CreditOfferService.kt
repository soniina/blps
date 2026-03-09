package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.repositories.CreditOfferRepository
import org.springframework.stereotype.Service

@Service
class CreditOfferService(private val creditOfferRepository: CreditOfferRepository) {

    fun getCreditOffers(application: CreditApplication): List<CreditOffer> =
        creditOfferRepository.findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application)
}
