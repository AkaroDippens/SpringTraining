package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorRepository : JpaRepository<Doctor, Long>
