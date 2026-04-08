package itmo.blps.citilink.services

import itmo.blps.citilink.dto.requests.CreditApplicationRequest
import itmo.blps.citilink.models.*
import jakarta.transaction.Transactional

interface CreditService {
    fun getCreditApplication(applicationId: Long, username: String): CreditApplication
    fun process(request: CreditApplicationRequest, order: Order): CreditApplication
    fun getApplicationsForOperator(): List<CreditApplication>

    fun approveOfflineSigning(applicationId: Long): CreditApplication
    fun updateStatus(creditApplication: CreditApplication, status: ApplicationStatus)
    fun selectOffer(creditApplication: CreditApplication, offer: CreditOffer)

    fun signApplication(creditApplication: CreditApplication)
}
