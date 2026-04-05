package itmo.blps.citilink.services

import itmo.blps.citilink.models.User

//interface UserService {
//    fun findUser(sessionId: String): User?
//    fun getUser(sessionId: String): User
//    fun getOrCreateUser(sessionId: String): User
//}

interface UserService {
    fun findUserByUsername(username: String): User?
    fun getOrCreateUser(username: String): User
}