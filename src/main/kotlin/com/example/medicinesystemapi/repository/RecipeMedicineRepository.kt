package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.RecipesMedicine
import com.example.medicinesystemapi.model.RecipesMedicineId
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeMedicineRepository : JpaRepository<RecipesMedicine, RecipesMedicineId>
