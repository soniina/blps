package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.repositories.CreditOfferRepository
import org.springframework.stereotype.Service

@Service
class CreditOfferServiceImpl(private val creditOfferRepository: CreditOfferRepository) : CreditOfferService {

    override fun getCreditOffers(application: CreditApplication): List<CreditOffer> =
        creditOfferRepository.findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application)

    override fun getCreditOfferById(offerId: Long) = creditOfferRepository.findCreditOfferById(offerId)

}
