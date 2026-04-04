package itmo.blps.citilink.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(private val jwtProvider: JwtProvider) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = resolveToken(request)

        if (token != null && jwtProvider.validateToken(token)) {
            val username = jwtProvider.getUsernameFromToken(token)
            val role = jwtProvider.getRoleFromToken(token)

            // Создаем объект авторизации для Spring Security, роль из токена
            val authority = SimpleGrantedAuthority(role)
            val auth = UsernamePasswordAuthenticationToken(username, null, listOf(authority))

            // Устанавливаем пользователя в контекст безопасности
            SecurityContextHolder.getContext().authentication = auth
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}