package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Specialization
import com.example.medicinesystemapi.service.SpecializationService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Specialization", description = "Operations related to specializations")
@RestController
@RequestMapping("/api/specializations")
class SpecializationController(private val specializationService: SpecializationService) {
    @GetMapping
    fun getAllSpecializations(): ResponseEntity<List<Specialization?>> {
        val specializations = specializationService.findAllSpecializationsList()
        return if (specializations.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(specializations)
        }
    }

    @GetMapping("/{id}")
    fun getSpecializationById(
        @PathVariable id: Long,
    ): ResponseEntity<Specialization?> {
        val specialization = specializationService.findSpecializationById(id)
        return if (specialization == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(specialization)
        }
    }

    @GetMapping("/byname/{specializationName}")
    fun getSpecializationByName(
        @PathVariable specializationName: String,
    ): ResponseEntity<List<Specialization>> {
        val specializations = specializationService.findSpecializationByName(specializationName)
        return if (specializations.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(specializations)
        }
    }

    @PostMapping
    fun addSpecialization(
        @RequestBody specialization: Specialization,
    ): ResponseEntity<Specialization?> {
        if (specialization.id != null) {
            return ResponseEntity.badRequest().build()
        }
        if (specialization.specializationName == null || specialization.specializationName.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        val savedSpecialization = specializationService.addSpecialization(specialization)
        return if (savedSpecialization == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedSpecialization)
        }
    }

    @PutMapping("/{id}")
    fun updateSpecialization(
        @PathVariable id: Long,
        @RequestBody specialization: Specialization,
    ): ResponseEntity<Specialization?> {
        if (specialization.id == null) {
            return ResponseEntity.badRequest().build()
        }
        specializationService.findSpecializationById(id) ?: return ResponseEntity.notFound().build()
        val updatedSpecialization = specializationService.updateSpecialization(id, specialization)
        return if (updatedSpecialization == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedSpecialization)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSpecialization(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        specializationService.findSpecializationById(id) ?: return ResponseEntity.notFound().build()
        specializationService.deleteSpecialization(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleSpecializations(
        @RequestBody specializationIds: List<Long>,
    ): ResponseEntity<Void> {
        if (specializationIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        specializationService.deleteMultipleSpecializations(specializationIds)
        return ResponseEntity.noContent().build()
    }
}
