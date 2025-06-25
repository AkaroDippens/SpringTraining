package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.config.JwtConfig
import com.example.medicinesystemapi.config.PasswordEncoderUtil
import com.example.medicinesystemapi.model.User
import com.example.medicinesystemapi.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.crypto.SecretKey

@RestController
@RequestMapping("/api/auth")
class AuthController
    @Autowired
    constructor(
        private val userService: UserService,
    ) {

        @PostMapping("/login")
        fun login(
            @RequestBody authRequest: AuthRequest,
        ): ResponseEntity<*> {
            val mhiPolicy = authRequest.mhiPolicy
            val password = authRequest.password

            val user: User? = userService.findUserByMhiPolicy(mhiPolicy)
                return if (user != null && PasswordEncoderUtil.matches(password, user.password)) {
                // Генерация токена
                val token = generateToken(mhiPolicy, user.id?.toLong(), user.fullName, user.idRole?.roleName)
                ResponseEntity.ok(AuthResponse(token, user))
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
            }
        }

        // Функция генерации токена
        private fun generateToken(
            mhiPolicy: String,
            userId: Long?,
            fullName: String?,
            role: String?,
        ): String {
            val now = Date()
            val expiration = Date(now.time + 86400000) // токен действителен 24 часа

            return Jwts.builder()
                .setSubject(mhiPolicy)
                .claim("userId", userId)
                .claim("fullName", fullName)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(JwtConfig.SECRET_KEY)
                .compact()
        }
    }

data class AuthRequest(val mhiPolicy: String, val password: String)

data class AuthResponse(val token: String, val user: User)
