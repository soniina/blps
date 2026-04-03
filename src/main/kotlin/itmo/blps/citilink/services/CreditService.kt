package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.models.Order
import itmo.blps.citilink.models.User
import jakarta.transaction.Transactional

interface CreditService {
    fun getCreditApplication(applicationId: Long, user: User): CreditApplication
    fun process(request: CreditApplicationRequest, order: Order): CreditApplication
    fun getApplicationsForOperator(): List<CreditApplication>

    @Transactional
    fun approveOfflineSigning(applicationId: Long) : CreditApplication
    fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus)
    fun selectOffer(creditApplication: CreditApplication, offer: CreditOffer)

    @Transactional
    fun signApplication(creditApplication: CreditApplication)
}