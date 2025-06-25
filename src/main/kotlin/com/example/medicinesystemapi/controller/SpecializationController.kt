package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Specialization
import com.example.medicinesystemapi.service.SpecializationService
import com.example.medicinesystemapi.validation.Validations
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Specialization", description = "Operations related to specializations")
@RestController
@RequestMapping("/api/specializations")
class SpecializationController(private val specializationService: SpecializationService) {

    val validations = Validations()

    @GetMapping
    fun getAllSpecializations(request: HttpServletRequest): ResponseEntity<List<Specialization?>> {
        if (!validations.hasAnyRole(request, "ADMIN", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val specializations = specializationService.findAllSpecializationsList()
        return if (specializations.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(specializations)
        }
    }

    @GetMapping("/{id}")
    fun getSpecializationById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Specialization?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val specialization = specializationService.findSpecializationById(id)
        return if (specialization == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(specialization)
        }
    }

    @GetMapping("/byname/{specializationName}")
    fun getSpecializationByName(
        request: HttpServletRequest,
        @PathVariable specializationName: String,
    ): ResponseEntity<List<Specialization>> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val specializations = specializationService.findSpecializationByName(specializationName)
        return if (specializations.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(specializations)
        }
    }

    @PostMapping
    fun addSpecialization(
        request: HttpServletRequest,
        @RequestBody specialization: Specialization,
    ): ResponseEntity<Specialization?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
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
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody specialization: Specialization,
    ): ResponseEntity<Specialization?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
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
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        specializationService.findSpecializationById(id) ?: return ResponseEntity.notFound().build()
        specializationService.deleteSpecialization(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleSpecializations(
        request: HttpServletRequest,
        @RequestBody specializationIds: List<Long>,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (specializationIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        specializationService.deleteMultipleSpecializations(specializationIds)
        return ResponseEntity.noContent().build()
    }
}
