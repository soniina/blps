package itmo.blps.citilink.security.config

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
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/").permitAll()
                auth.requestMatchers("/auth/**").permitAll()
                auth.requestMatchers("/products/**").permitAll()

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
