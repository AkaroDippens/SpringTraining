package com.example.medicinesystemapi.validation

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
}
