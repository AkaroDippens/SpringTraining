package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Recipe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RecipeService {
    fun findAllRecipes(pageable: Pageable): Page<Recipe>

    fun findAllRecipesList(): List<Recipe?>

    fun findRecipeById(id: Long?): Recipe?

    fun addRecipe(recipe: Recipe): Recipe?

    fun updateRecipe(
        id: Long,
        recipe: Recipe,
    ): Recipe?

    fun deleteRecipe(id: Long)

    fun deleteMultipleRecipes(recipeIds: List<Long>)

    fun logicalDeleteRecipe(id: Long)
}
