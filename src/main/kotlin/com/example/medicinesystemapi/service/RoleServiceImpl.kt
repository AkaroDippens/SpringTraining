package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Role
import com.example.medicinesystemapi.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
) : RoleService {
    override fun findAllRoles(pageable: Pageable): Page<Role> {
        return roleRepository.findAll(pageable)
    }

    override fun findAllRolesList(): List<Role> {
        return roleRepository.findAll()
    }

    override fun findRoleById(id: Long?): Role? {
        return roleRepository.findById(id ?: 0).orElse(null)
    }

    override fun findRoleByName(name: String?): Role? {
        return roleRepository.findAll().firstOrNull { it.roleName == name }
    }

    override fun addRole(role: Role): Role? {
        return roleRepository.save(role)
    }

    override fun updateRole(
        id: Long,
        role: Role,
    ): Role? {
        return roleRepository.save(role)
    }

    override fun deleteRole(id: Long) {
        roleRepository.deleteById(id)
    }

    override fun deleteMultipleRoles(roleIds: List<Long>) {
        roleRepository.deleteAllById(roleIds)
    }

    override fun logicalDeleteRole(id: Long) {
        val role = roleRepository.findById(id).orElse(null)
        role?.let {
            roleRepository.save(it)
        }
    }
}
