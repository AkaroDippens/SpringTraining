package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RoleService {
    fun findAllRoles(pageable: Pageable): Page<Role>
    fun findAllRolesList(): List<Role>
    fun findRoleById(id: Long?): Role?
    fun findRoleByName(roleName: String?): Role?
    fun addRole(role: Role): Role?
    fun updateRole(id: Long, role: Role): Role?
    fun deleteRole(id: Long)
    fun deleteMultipleRoles(roleIds: List<Long>)
    fun logicalDeleteRole(id: Long)
}

