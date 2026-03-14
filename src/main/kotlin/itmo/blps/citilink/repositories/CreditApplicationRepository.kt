package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CreditApplicationRepository : JpaRepository<CreditApplication, Long> {
    fun findCreditApplicationsByUid(applicationUid: UUID): CreditApplication?
    fun findAllByStatus(status: ApplicationStatus): List<CreditApplication>
}
