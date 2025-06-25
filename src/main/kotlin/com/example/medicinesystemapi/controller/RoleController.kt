package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Role
import com.example.medicinesystemapi.service.RoleService
import com.example.medicinesystemapi.validation.Validations
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Role", description = "Operations related to roles")
@RestController
@RequestMapping("/api/roles")
class RoleController(private val roleService: RoleService) {

    val validations = Validations()

    @GetMapping
    fun getAllRoles(request: HttpServletRequest): ResponseEntity<List<Role?>> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val roles = roleService.findAllRolesList()
        return if (roles.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(roles)
        }
    }

    @GetMapping("/{id}")
    fun getRoleById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Role?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val role = roleService.findRoleById(id)
        return if (role == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(role)
        }
    }

    @GetMapping("/byname/{roleName}")
    fun getRoleByName(
        request: HttpServletRequest,
        @PathVariable roleName: String,
    ): ResponseEntity<Role?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val roles = roleService.findRoleByName(roleName)
        return if (roles == null) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(roles)
        }
    }

    @PostMapping
    fun addRole(
        request: HttpServletRequest,
        @RequestBody role: Role,
    ): ResponseEntity<Role?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (role.id != null) {
            return ResponseEntity.badRequest().build()
        }
        if (role.roleName == null || role.roleName.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        val savedRole = roleService.addRole(role)
        return if (savedRole == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedRole)
        }
    }

    @PutMapping("/{id}")
    fun updateRole(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody role: Role,
    ): ResponseEntity<Role?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (role.id == null) {
            return ResponseEntity.badRequest().build()
        }
        roleService.findRoleById(id) ?: return ResponseEntity.notFound().build()
        val updatedRole = roleService.updateRole(id, role)
        return if (updatedRole == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedRole)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteRole(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        roleService.findRoleById(id) ?: return ResponseEntity.notFound().build()
        roleService.deleteRole(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleRoles(
        @RequestBody roleIds: List<Long>,
    ): ResponseEntity<Void> {
        if (roleIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        roleService.deleteMultipleRoles(roleIds)
        return ResponseEntity.noContent().build()
    }
}
