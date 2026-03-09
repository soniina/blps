package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditOfferRepository : JpaRepository<CreditOffer, Long> {
    fun findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application: CreditApplication): List<CreditOffer>
}
