package itmo.blps.citilink.services

import itmo.blps.citilink.models.ApplicationStatus
import itmo.blps.citilink.models.CreditApplication
import itmo.blps.citilink.models.CreditOffer
import itmo.blps.citilink.repositories.CreditApplicationRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.roundToInt
import kotlin.random.Random

@Profile("banks")
@Service
class BankServiceImpl(
    private val creditOfferService: CreditOfferService,
    private val creditApplicationRepository: CreditApplicationRepository
) : BankService {

    @Transactional
    override fun generateOffers(application: CreditApplication) {
        val allBanks = listOf(
            "Сбербанк", "ВТБ", "Альфа-Банк", "Тинькофф", "Газпромбанк", "Райффайзенбанк", "МТС Банк"
        )
        val approvedOffers = mutableListOf<CreditOffer>()

        for (bank in allBanks) {
            val isApproved = Random.nextDouble() < 0.6
            if (isApproved) {
                approvedOffers.add(
                    CreditOffer(
                        application = application,
                        bankName = bank,
                        interestRate = (Random.nextDouble(12.0, 22.0) * 10.0).roundToInt() / 10.0,
                        isOnlineSigningAvailable = Random.nextBoolean()
                    )
                )
            }
        }
        if (approvedOffers.isNotEmpty()) {
            creditOfferService.saveCreditOffers(approvedOffers)
            application.status = ApplicationStatus.OFFERS_READY
        } else {
            application.status = ApplicationStatus.REJECTED
        }
        creditApplicationRepository.save(application)
    }
}
