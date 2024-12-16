package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.service.UserRoleManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// UserRoleController.kt
@RestController
@RequestMapping("/api/users")
class UserRoleController(private val userRoleManagementService: UserRoleManagementService) {

    @PutMapping("/{userId}/role")
    fun changeUserRole(
        @PathVariable userId: Int,
        @RequestParam newRoleId: Int
    ): ResponseEntity<Any> {
        val success = userRoleManagementService.changeUserRole(userId, newRoleId)
        return if (success) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}