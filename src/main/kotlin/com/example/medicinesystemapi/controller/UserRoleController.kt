package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.service.UserRoleManagementService
import com.example.medicinesystemapi.validation.Validations
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// UserRoleController.kt
@RestController
@RequestMapping("/api/users")
class UserRoleController(private val userRoleManagementService: UserRoleManagementService) {

    val validations = Validations()
    @PutMapping("/{userId}/role")
    fun changeUserRole(
        request: HttpServletRequest,
        @PathVariable userId: Int,
        @RequestParam newRoleId: Int,
    ): ResponseEntity<Any> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val success = userRoleManagementService.changeUserRole(userId, newRoleId)
        return if (success) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
