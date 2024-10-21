package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.RoleEnum
import com.example.uchebpracticaspring.model.UserModel
import com.example.uchebpracticaspring.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun registerUser(user: UserModel) {
        try {
            // Валидация данных
            if (user.username.isEmpty() || user.password.isEmpty()) {
                throw Exception("Invalid user data")
            }
            if(user.password.length < 4) {
                throw Exception("Invalid password")
            }
            user.isActive = true
            user.password = passwordEncoder.encode(user.password)
            user.roles = Collections.singleton(RoleEnum.USER)
            userRepository.save(user)
        } catch (e: Exception) {
            throw Exception("Error registering user: ${e.message}")
        }
    }

    fun getUserByName(name: String): UserModel? {
        try {
            // Возвращаем пользователя по имени
            return userRepository.findByUsername(name)
        } catch (e: Exception) {
            // Обработка исключений
            throw Exception("Error getting user by name: ${e.message}")
        }
    }
}