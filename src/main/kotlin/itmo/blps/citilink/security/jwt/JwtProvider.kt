package itmo.blps.citilink.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtProvider {
    private val secret = "super_secret_key_for_blps_lab2_at _least_32_symbols"
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    // Генерируем токен и записываем туда роль пользователя
    fun generateToken(username: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + 3600000) // 1 час

        return Jwts.builder()
            .subject(username)
            .claim("role", role) // Записываем роль в токен
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return getClaims(token).subject
    }

    fun getRoleFromToken(token: String): String {
        return getClaims(token).get("role", String::class.java)
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
    }
}