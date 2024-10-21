package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.RoleEnum
import com.example.uchebpracticaspring.model.UserModel
import com.example.uchebpracticaspring.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun registerUser(user: UserModel): UserModel {
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
            return user
        } catch (e: Exception) {
            throw Exception("Error registering user: ${e.message}")
        }
    }

    fun getAllUsers(): List<UserModel> = userRepository.findAll()


    fun updateUser(id: Long, user: UserModel): UserModel? {
        return if (userRepository.existsById(id)) {
            user.id = id
            userRepository.save(user)
        } else {
            null
        }
    }
    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
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

    fun getUserById(id: Long): UserModel? {
        try {
            return userRepository.findByIdOrNull(id)
        } catch (e: Exception) {
            throw Exception("Error getting user by name: ${e.message}")
        }
    }
}