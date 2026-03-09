package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.CreditApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditApplicationRepository : JpaRepository<CreditApplication, Long> {
    fun findCreditApplicationsById(applicationId: Long): CreditApplication?
}
