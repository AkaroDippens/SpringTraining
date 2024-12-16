package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.example.medicinesystemapi.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import com.example.medicinesystemapi.validation.Validations

@Tag(name = "User", description = "Operations related to users")
@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    val validations = Validations()

    /*@GetMapping
    fun getAllUsers(): ResponseEntity<List<User?>> {
        val users = userService.findAllUsersList()
        return if (users.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(users)
        }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User?> {
        val user = userService.findUserById(id)
        return if (user == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(user)
        }
    }*/

    @GetMapping("/byname/{fullName}")
    fun getUserByName(@PathVariable fullName: String): ResponseEntity<List<User>> {
        val users = userService.findUserByName(fullName)
        return if (users.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(users)
        }
    }

    @GetMapping("/mhipolicy/{mhiPolicy}")
    fun getUserByMhiPolicy(@PathVariable mhiPolicy: String): ResponseEntity<User?> {
        val user = userService.findUserByMhiPolicy(mhiPolicy)
        return if (user?.mhiPolicy.isNullOrEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(user)
        }
    }

    @PostMapping("/add")
    fun addUser(@RequestBody user: User): ResponseEntity<Any?> {
        val errors = mutableMapOf<String, String>()

        if (user.id != null) {
            return ResponseEntity.badRequest().build()
        }
        if (user.fullName == null || user.fullName.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        if (!validations.validatePassword(user.password)) {
            errors["password"] = "Пароль должен содержать минимум 8 символов, включая заглавную букву, цифру и специальный символ."
            ResponseEntity.badRequest().body(errors)
        }
        if (!validations.validateMhiPolicy(user.mhiPolicy)) {
            errors["mhiPolicy"] = "Полис ОМС должен состоять из 8 цифр."
            ResponseEntity.badRequest().body(errors)
        }

        // Если есть ошибки, возвращаем их клиенту


        val userExistMHI = userService.findUserByMhiPolicy(user.mhiPolicy)
        if (userExistMHI != null) {
            errors["mhiPolicy"] = "Пользователь с таким полисом ОМС уже существует."
            ResponseEntity.status(HttpStatus.CONFLICT).body(errors)
        }

        if (errors.isNotEmpty()) {
            return ResponseEntity.badRequest().body(errors)
        }

        val savedUser = userService.addUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User?> {
        if (user.id == null) {
            return ResponseEntity.badRequest().build()
        }
        userService.findUserById(id) ?: return ResponseEntity.notFound().build()
        val updatedUser = userService.updateUser(id, user)
        return if (updatedUser == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedUser)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.findUserById(id) ?: return ResponseEntity.notFound().build()
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleUsers(@RequestBody userIds: List<Long>): ResponseEntity<Void> {
        if (userIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        userService.deleteMultipleUsers(userIds)
        return ResponseEntity.noContent().build()
    }
}