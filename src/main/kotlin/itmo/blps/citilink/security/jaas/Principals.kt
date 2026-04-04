package itmo.blps.citilink.security.jaas

import java.security.Principal

class UserPrincipal(private val name: String) : Principal {
    override fun getName() = name
}

class RolePrincipal(private val role: String) : Principal {
    override fun getName() = role
}