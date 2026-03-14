package itmo.blps.citilink.services

import itmo.blps.citilink.dto.CreditApplicationRequest
import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.models.Order
import jakarta.transaction.Transactional
import java.util.UUID

interface CreditService {
    fun getCreditApplicationByUid(applicationUid: UUID): CreditApplication?
    fun process(request: CreditApplicationRequest, order: Order): CreditApplication
    fun getApplicationsForOperator(): List<CreditApplication>

    @Transactional
    fun approveOfflineSigning(applicationUid: UUID)
    fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus)
    fun selectOffer(creditApplication: CreditApplication, selectedOffer: CreditOffer)

    @Transactional
    fun signApplication(creditApplication: CreditApplication)
}