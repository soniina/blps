package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication
import org.springframework.transaction.annotation.Transactional

interface BankService {
    fun generateOffers(application: CreditApplication)
}
