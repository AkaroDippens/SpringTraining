package com.example.uchebpracticaspring.config

import com.example.uchebpracticaspring.model.UserModel
import com.example.uchebpracticaspring.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(
    private val userRepository: UserRepository,
    @Lazy private val passwordEncoder: PasswordEncoder
) {

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(object : UserDetailsService {
            override fun loadUserByUsername(username: String?): UserDetails {
                val user: UserModel = userRepository.findByUsername(username)
                    ?: throw UsernameNotFoundException("User not found")

                return org.springframework.security.core.userdetails.User(
                    user.username,
                    user.password,
                    user.isActive,
                    true,
                    true,
                    true,
                    user.roles
                )
            }
        }).passwordEncoder(passwordEncoder)
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers("/register", "/login").permitAll()
                .requestMatchers( "/teachers").hasAuthority("ADMIN")
                .requestMatchers( "/students").hasAuthority("MANAGER")
                .anyRequest().authenticated()
        }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            }
            .sessionManagement { session ->
                session
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
                    .expiredUrl("/login?expired")
                session.sessionFixation().migrateSession()
                session.invalidSessionUrl("/login?invalid")
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

            }
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.disable() }


        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers("/h2-console/**")
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher {
        return HttpSessionEventPublisher()
    }
}