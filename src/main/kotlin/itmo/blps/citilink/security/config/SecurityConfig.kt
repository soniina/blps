package itmo.blps.citilink.security.config

import itmo.blps.citilink.security.jwt.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtFilter: JwtFilter) {
    @Bean
    fun userDetailsService(): org.springframework.security.core.userdetails.UserDetailsService {
        // Надо возвратить пустой менеджер, чтобы Спринг не генерировал случайный пароль
        return InMemoryUserDetailsManager()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            // Настраиваем Stateless (без сессий в БД/памяти сервера)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/").permitAll()
                auth.requestMatchers("/auth/**").permitAll() // Путь для логина
                auth.requestMatchers("/operator/**").hasAuthority("MANAGER")
                auth.requestMatchers("/checkout/**").hasAuthority("AUTHORIZED")
                auth.anyRequest().permitAll()
            }
            // Добавляем наш JWT фильтр перед стандартным фильтром логина
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}