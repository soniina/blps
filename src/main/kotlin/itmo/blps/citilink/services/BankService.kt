package itmo.blps.citilink.services

import itmo.blps.citilink.models.CreditApplication

interface BankService {
    fun generateOffers(application: CreditApplication): Boolean
}
