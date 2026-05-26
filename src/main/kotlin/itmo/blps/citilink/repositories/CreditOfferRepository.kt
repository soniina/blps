package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import org.apache.ibatis.annotations.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CreditOfferRepository : JpaRepository<CreditOffer, Long> {
    fun findCreditOfferById(offerId: Long): CreditOffer?
    fun findAllByApplicationOrderByIsOnlineSigningAvailableDesc(application: CreditApplication): List<CreditOffer>
    //fun findAllByApplicationId(applicationId: Long): List<CreditOffer>
    @Query(value = "SELECT * FROM credits_offers WHERE application_id = ?1", nativeQuery = true)
    fun findAllByApplicationIdNative(applicationId: Long): List<CreditOffer>
}
