package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.UserModel
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserModel, Long> {
    fun findByUsername(username: String?): UserModel?
}