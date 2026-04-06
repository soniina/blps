package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.repositories.CreditOfferRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class CreditOfferServiceImpl(private val creditOfferRepository: CreditOfferRepository) : CreditOfferService {

    override fun getCreditOffers(application: CreditApplication): List<CreditOffer> =
        creditOfferRepository.findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application)

    override fun getCreditOffer(offerId: Long, username: String): CreditOffer {
        val offer = creditOfferRepository.findCreditOfferById(offerId)
            ?: throw EntityNotFoundException("CreditOffer with id $offerId not found")

        if (offer.application.order.username != username) throw AccessDeniedException("You cannot access orders of another user")

        return offer
    }

    override fun saveCreditOffers(creditOffers: List<CreditOffer>) {
        creditOfferRepository.saveAll(creditOffers)
    }

}
