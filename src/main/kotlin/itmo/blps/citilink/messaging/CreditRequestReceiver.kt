package itmo.blps.citilink.messaging

import itmo.blps.citilink.repositories.CreditApplicationRepository
import itmo.blps.citilink.services.BankService
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
@Profile("banks")
class CreditRequestReceiver(
    private val bankService: BankService,
    private val creditApplicationRepository: CreditApplicationRepository
) {
    @JmsListener(destination = "java:/jms/queue/CreditRequestsQueue")
    fun receiveApplication(applicationId: String) {
        println(">>> JMS: Получен запрос на генерацию офферов для заявки №$applicationId")

        val application = creditApplicationRepository.findCreditApplicationsById(applicationId.toLong())
            ?: throw EntityNotFoundException(">>> JMS: CreditApplication with id $applicationId not found")

        bankService.generateOffers(application)
        println(">>> JMS: Офферы для заявки №$applicationId успешно сгенерированы")
    }
}
