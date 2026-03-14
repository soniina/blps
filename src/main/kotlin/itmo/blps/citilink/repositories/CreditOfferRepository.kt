package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CreditOfferRepository : JpaRepository<CreditOffer, Long> {
    fun findCreditOfferByUid(offerUid: UUID): CreditOffer?
    fun findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application: CreditApplication): List<CreditOffer>
}
