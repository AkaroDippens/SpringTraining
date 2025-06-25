package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Analyze
import com.example.medicinesystemapi.service.AnalyzeService
import com.example.medicinesystemapi.validation.Validations
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/analyzes")
class AnalyzeController(private val analyzeService: AnalyzeService) {

    val validations = Validations()
    @GetMapping
    fun getAllAnalyzes(request: HttpServletRequest): ResponseEntity<List<Analyze?>> {
        if (!validations.hasAnyRole(request, "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val analyzes = analyzeService.findAllAnalyzesList()
        return if (analyzes.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(analyzes)
        }
    }

    @GetMapping("/{id}")
    fun getAnalyzeById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Analyze?> {
        if (!validations.hasAnyRole(request, "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val analyze = analyzeService.findAnalyzeById(id)
        return if (analyze == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(analyze)
        }
    }

    @PostMapping
    fun addAnalyze(
        request: HttpServletRequest,
        @RequestBody analyze: Analyze,
    ): ResponseEntity<Analyze?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (analyze.id != null) {
            return ResponseEntity.badRequest().build()
        }
        val savedAnalyze = analyzeService.addAnalyze(analyze)
        return if (savedAnalyze == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedAnalyze)
        }
    }

    @PutMapping("/{id}")
    fun updateAnalyze(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody analyze: Analyze,
    ): ResponseEntity<Analyze?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (analyze.id == null) {
            return ResponseEntity.badRequest().build()
        }
        analyzeService.findAnalyzeById(id) ?: return ResponseEntity.notFound().build()
        val updatedAnalyze = analyzeService.updateAnalyze(id, analyze)
        return if (updatedAnalyze == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedAnalyze)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteAnalyze(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        analyzeService.findAnalyzeById(id) ?: return ResponseEntity.notFound().build()
        analyzeService.deleteAnalyze(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleAnalyzes(
        request: HttpServletRequest,
        @RequestBody analyzeIds: List<Long>,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (analyzeIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        analyzeService.deleteMultipleAnalyzes(analyzeIds)
        return ResponseEntity.noContent().build()
    }
}
