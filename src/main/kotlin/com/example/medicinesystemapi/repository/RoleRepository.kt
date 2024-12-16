package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long>
