package com.example.medicinesystemapi.config

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordEncoderUtil {
    private val encoder = BCryptPasswordEncoder()

    fun encode(password: String?): String {
        return encoder.encode(password)
    }

    fun matches(
        rawPassword: String?,
        encodedPassword: String?,
    ): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }
}
