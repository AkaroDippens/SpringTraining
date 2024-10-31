package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Recipe
import com.example.medicinesystemapi.service.RecipeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(private val recipeService: RecipeService) {

    @GetMapping
    fun getAllRecipes(): ResponseEntity<List<Recipe?>> {
        val recipes = recipeService.findAllRecipesList()
        return if (recipes.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(recipes)
        }
    }

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<Recipe?> {
        val recipe = recipeService.findRecipeById(id)
        return if (recipe == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(recipe)
        }
    }

    @PostMapping
    fun addRecipe(@RequestBody recipe: Recipe): ResponseEntity<Recipe?> {
        if (recipe.id != null) {
            return ResponseEntity.badRequest().build()
        }
        val savedRecipe = recipeService.addRecipe(recipe)
        return if (savedRecipe == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe)
        }
    }

    @PutMapping("/{id}")
    fun updateRecipe(@PathVariable id: Long, @RequestBody recipe: Recipe): ResponseEntity<Recipe?> {
        if (recipe.id == null) {
            return ResponseEntity.badRequest().build()
        }
        recipeService.findRecipeById(id) ?: return ResponseEntity.notFound().build()
        val updatedRecipe = recipeService.updateRecipe(id, recipe)
        return if (updatedRecipe == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedRecipe)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Void> {
        recipeService.findRecipeById(id) ?: return ResponseEntity.notFound().build()
        recipeService.deleteRecipe(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleRecipes(@RequestBody recipeIds: List<Long>): ResponseEntity<Void> {
        if (recipeIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        recipeService.deleteMultipleRecipes(recipeIds)
        return ResponseEntity.noContent().build()
    }
}