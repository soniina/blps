package itmo.blps.citilink.repositories

import itmo.blps.citilink.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findBySessionId(sessionId: String): User?
}