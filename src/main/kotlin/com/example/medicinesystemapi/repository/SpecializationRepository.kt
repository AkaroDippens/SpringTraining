package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Specialization
import org.springframework.data.jpa.repository.JpaRepository

interface SpecializationRepository : JpaRepository<Specialization, Long>