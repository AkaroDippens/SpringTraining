package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Analyze
import com.example.medicinesystemapi.service.AnalyzeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/analyzes")
class AnalyzeController(private val analyzeService: AnalyzeService) {

    @GetMapping
    fun getAllAnalyzes(): ResponseEntity<List<Analyze?>> {
        val analyzes = analyzeService.findAllAnalyzesList()
        return if (analyzes.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(analyzes)
        }
    }

    @GetMapping("/{id}")
    fun getAnalyzeById(@PathVariable id: Long): ResponseEntity<Analyze?> {
        val analyze = analyzeService.findAnalyzeById(id)
        return if (analyze == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(analyze)
        }
    }

    @PostMapping
    fun addAnalyze(@RequestBody analyze: Analyze): ResponseEntity<Analyze?> {
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
    fun updateAnalyze(@PathVariable id: Long, @RequestBody analyze: Analyze): ResponseEntity<Analyze?> {
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
    fun deleteAnalyze(@PathVariable id: Long): ResponseEntity<Void> {
        analyzeService.findAnalyzeById(id) ?: return ResponseEntity.notFound().build()
        analyzeService.deleteAnalyze(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleAnalyzes(@RequestBody analyzeIds: List<Long>): ResponseEntity<Void> {
        if (analyzeIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        analyzeService.deleteMultipleAnalyzes(analyzeIds)
        return ResponseEntity.noContent().build()
    }
}