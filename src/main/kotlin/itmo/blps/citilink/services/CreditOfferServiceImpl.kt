package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.CreditOfferRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

//import org.springframework.security.access.AccessDeniedException

@Service
class CreditOfferServiceImpl(private val creditOfferRepository: CreditOfferRepository) : CreditOfferService {

    override fun getCreditOffers(application: CreditApplication): List<CreditOffer> =
        creditOfferRepository.findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application)

    override fun getCreditOffer(offerId: Long, user: User): CreditOffer {
        val offer = creditOfferRepository.findCreditOfferById(offerId)
            ?: throw EntityNotFoundException("CreditOffer with id $offerId not found")

//        if (offer.application.order.user.id != user.id) throw AccessDeniedException("Access denied")

        return offer
    }

    override fun saveCreditOffers(creditOffers: List<CreditOffer>) {
        creditOfferRepository.saveAll(creditOffers)
    }

}
