package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {
    fun findAllUsers(pageable: Pageable): Page<User>
    fun findAllUsersList(): List<User?>
    fun findUserById(id: Long?): User?
    fun findUserByName(fullName: String?): List<User>
    fun addUser(user: User): User?
    fun updateUser(id: Long, user: User): User?
    fun deleteUser(id: Long)
    fun deleteMultipleUsers(userIds: List<Long>)
    fun logicalDeleteUser(id: Long)
    fun findUserByMhiPolicy(mhiPolicy: String?): User?
}

