package itmo.blps.citilink.configs

import itmo.blps.citilink.security.jwt.JwtFilter
import jakarta.servlet.DispatcherType
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
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): UserDetailsService = InMemoryUserDetailsManager()

    // НОВОЕ: Игнорируем путь REST API полностью
    @Bean
    fun webSecurityCustomizer(): org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer {
        return org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers("/engine-rest/**")
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests { auth ->
                auth.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

                auth.requestMatchers("/camunda/**", "/api/**", "/lib/**").permitAll()

                auth.requestMatchers("/", "/v3/**", "/swagger-ui/**", "/auth/**", "/products/**").permitAll()
                auth.requestMatchers("/operator/**").hasAuthority("OPERATOR")
                auth.requestMatchers("/cart/**", "/checkout/**", "/orders/**", "/credit/**").hasAuthority("AUTHORIZED")

                auth.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}