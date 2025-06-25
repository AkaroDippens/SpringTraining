package com.example.medicinesystemapi.config

import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey

object JwtConfig {
    private val dotenv = Dotenv.configure().load()
    private val secretKeyString = dotenv["JWT_SECRET_KEY"]
        ?: throw IllegalStateException("JWT_SECRET_KEY not found in .env")

    val SECRET_KEY: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())

    const val TOKEN_VALIDITY = 86400000L // 24 hours in milliseconds
}