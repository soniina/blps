package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditApplicationRepository : JpaRepository<CreditApplication, Long> {
    fun findCreditApplicationById(applicationId: Long): CreditApplication?
    fun findAllByStatus(status: ApplicationStatus): List<CreditApplication>
    fun findByIdAndStatus(applicationId: Long, status: ApplicationStatus): CreditApplication?
}
