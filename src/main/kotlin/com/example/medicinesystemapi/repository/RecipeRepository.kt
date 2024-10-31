package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Recipe
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeRepository : JpaRepository<Recipe, Long>