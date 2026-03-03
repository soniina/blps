package itmo.blps.citilink.services

import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getUser(sessionId: String): User? {
        return userRepository.findBySessionId(sessionId)
    }

    fun getOrCreateUser(sessionId: String): User {
        return userRepository.findBySessionId(sessionId) ?:
            userRepository.save(User(sessionId = sessionId))
    }

}