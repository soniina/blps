package itmo.blps.citilink.configs

import itmo.blps.citilink.security.jwt.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtFilter: JwtFilter) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return InMemoryUserDetailsManager()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests { auth ->
                // 1. Разрешаем пути Camunda (статику, API и интерфейс)
                auth.requestMatchers("/camunda/**").permitAll()
                auth.requestMatchers("/api/**").permitAll()
                auth.requestMatchers("/lib/**").permitAll()

                // 2. Стандартные публичные пути
                auth.requestMatchers("/").permitAll()
                auth.requestMatchers("/v3/**").permitAll()
                auth.requestMatchers("/swagger-ui/**").permitAll()
                auth.requestMatchers("/auth/**").permitAll()
                auth.requestMatchers("/products/**").permitAll()

                // 3. Ролевая модель
                auth.requestMatchers("/operator/**").hasAuthority("OPERATOR")

                auth.requestMatchers("/cart/**").hasAuthority("AUTHORIZED")
                auth.requestMatchers("/checkout/**").hasAuthority("AUTHORIZED")
                auth.requestMatchers("/orders/**").hasAuthority("AUTHORIZED")
                auth.requestMatchers("/credit/**").hasAuthority("AUTHORIZED")

                auth.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}