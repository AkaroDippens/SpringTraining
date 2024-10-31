package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Medicine
import org.springframework.data.jpa.repository.JpaRepository

interface MedicineRepository : JpaRepository<Medicine, Long>