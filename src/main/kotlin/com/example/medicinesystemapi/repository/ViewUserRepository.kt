package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.ViewUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ViewUserRepository : JpaRepository<ViewUser, Int>
