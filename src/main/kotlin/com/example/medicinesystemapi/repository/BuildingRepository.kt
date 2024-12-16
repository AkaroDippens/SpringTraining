package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Building
import org.springframework.data.jpa.repository.JpaRepository

interface BuildingRepository : JpaRepository<Building, Long>
