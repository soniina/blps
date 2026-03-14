package itmo.blps.citilink.services

import itmo.blps.citilink.models.User
import itmo.blps.citilink.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun getUser(sessionId: String): User? {
        return userRepository.findBySessionId(sessionId)
    }

    override fun getOrCreateUser(sessionId: String): User {
        return userRepository.findBySessionId(sessionId) ?:
            userRepository.save(User(sessionId = sessionId))
    }

}
