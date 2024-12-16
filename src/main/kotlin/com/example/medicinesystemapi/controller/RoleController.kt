package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Role
import com.example.medicinesystemapi.service.RoleService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Role", description = "Operations related to roles")
@RestController
@RequestMapping("/api/roles")
class RoleController(private val roleService: RoleService) {
    @GetMapping
    fun getAllRoles(): ResponseEntity<List<Role?>> {
        val roles = roleService.findAllRolesList()
        return if (roles.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(roles)
        }
    }

    @GetMapping("/{id}")
    fun getRoleById(
        @PathVariable id: Long,
    ): ResponseEntity<Role?> {
        val role = roleService.findRoleById(id)
        return if (role == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(role)
        }
    }

    @GetMapping("/byname/{roleName}")
    fun getRoleByName(
        @PathVariable roleName: String,
    ): ResponseEntity<Role?> {
        val roles = roleService.findRoleByName(roleName)
        return if (roles == null) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(roles)
        }
    }

    @PostMapping
    fun addRole(
        @RequestBody role: Role,
    ): ResponseEntity<Role?> {
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
        @PathVariable id: Long,
        @RequestBody role: Role,
    ): ResponseEntity<Role?> {
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
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
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
