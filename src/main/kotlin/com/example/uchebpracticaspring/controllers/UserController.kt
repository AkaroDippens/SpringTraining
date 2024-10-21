package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.UserModel
import com.example.uchebpracticaspring.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController @Autowired constructor(
    private val userService: UserService
) {
    @GetMapping
    fun getAllUsers(): List<UserModel> = userService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserModel> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: UserModel): Any {
        try{
            userService.registerUser(user)
            return ResponseEntity.ok(user)
        }
        catch (ex:Exception){
            return ResponseEntity.badRequest()
        }
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: UserModel): ResponseEntity<UserModel> {
        val updatedUser = userService.updateUser(id, user)
        return if (updatedUser != null) {
            ResponseEntity.ok(updatedUser)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return if (userService.deleteUser(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}