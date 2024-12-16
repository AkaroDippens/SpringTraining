package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Recipe
import com.example.medicinesystemapi.repository.RecipeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RecipeServiceImpl(
    private val recipeRepository: RecipeRepository,
) : RecipeService {
    override fun findAllRecipes(pageable: Pageable): Page<Recipe> {
        return recipeRepository.findAll(pageable)
    }

    override fun findAllRecipesList(): List<Recipe?> {
        return recipeRepository.findAll()
    }

    override fun findRecipeById(id: Long?): Recipe? {
        return recipeRepository.findById(id ?: 0).orElse(null)
    }

    override fun addRecipe(recipe: Recipe): Recipe? {
        return recipeRepository.save(recipe)
    }

    override fun updateRecipe(
        id: Long,
        recipe: Recipe,
    ): Recipe? {
        return recipeRepository.save(recipe)
    }

    override fun deleteRecipe(id: Long) {
        recipeRepository.deleteById(id)
    }

    override fun deleteMultipleRecipes(recipeIds: List<Long>) {
        recipeRepository.deleteAllById(recipeIds)
    }

    override fun logicalDeleteRecipe(id: Long) {
        val recipe = recipeRepository.findById(id).orElse(null)
        recipe?.let {
            recipeRepository.save(it)
        }
    }
}
