package itmo.blps.citilink.services

import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun findUser(sessionId: String) = userRepository.findBySessionId(sessionId)

    override fun getUser(sessionId: String) = userRepository.findBySessionId(sessionId)
        ?: throw EntityNotFoundException("Invalid or expired session")

    override fun getOrCreateUser(sessionId: String): User {
        return userRepository.findBySessionId(sessionId) ?: userRepository.save(User(sessionId = sessionId))
    }

}
