package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.User
import com.example.medicinesystemapi.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import com.example.medicinesystemapi.repository.RoleRepository

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : UserService {

    override fun findAllUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    override fun findAllUsersList(): List<User?> {
        return userRepository.findAll()
    }

    override fun findUserById(id: Long?): User? {
        return userRepository.findById(id ?: 0).orElse(null)
    }

    override fun findUserByName(fullName: String?): List<User> {
        return userRepository.findAll().filter{ it.fullName == fullName }
    }

    override fun addUser(user: User): User? {
        user.idRole = roleRepository.findAll().firstOrNull { it.roleName == "USER" }
        return userRepository.save(user)
    }

    override fun updateUser(id: Long, user: User): User? {
        return userRepository.save(user)
    }

    override fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

    override fun deleteMultipleUsers(userIds: List<Long>) {
        userRepository.deleteAllById(userIds)
    }

    override fun logicalDeleteUser(id: Long) {
        val user = userRepository.findById(id).orElse(null)
        user?.let {
            userRepository.save(it)
        }
    }

    override fun findUserByMhiPolicy(mhiPolicy: String?): User? {
        return userRepository.findAll().firstOrNull { it.mhiPolicy == mhiPolicy }
    }
}
