package com.example.medicinesystemapi.validation

import com.example.medicinesystemapi.config.JwtConfig
import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest

class Validations {
    fun validatePassword(password: String?): Boolean {
        if (password == null || password.length < 8) {
            return false
        }
        val hasLetter = password.matches(Regex(".*[a-zA-Z].*"))
        val hasDigit = password.matches(Regex(".*\\d.*"))
        val hasSpecialChar = password.matches(Regex(".*[!@#\$%^&*()].*"))
        val hasUpperCase = password.matches(Regex(".*[A-Z].*"))
        return hasLetter && hasDigit && hasSpecialChar && hasUpperCase
    }

    fun validateMhiPolicy(mhiPolicy: String?): Boolean {
        if (mhiPolicy == null || mhiPolicy.length != 8) {
            return false
        }
        return mhiPolicy.matches(Regex("\\d{8}"))
    }

    fun hasAnyRole(request: HttpServletRequest, vararg roles: String): Boolean {
        val token = request.getHeader("Authorization")?.substringAfter("Bearer ") ?: return false
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(JwtConfig.SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .body

            val userRole = claims["role"] as? String
            roles.any { it == userRole }
        } catch (e: Exception) {
            false
        }
    }
}
