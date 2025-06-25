package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.MedicalRecord
import com.example.medicinesystemapi.service.MedicalRecordService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/medicalrecords")
class MedicalRecordController(private val medicalRecordService: MedicalRecordService) {
    @GetMapping
    fun getAllMedicalRecords(): ResponseEntity<List<MedicalRecord?>> {
        val medicalRecords = medicalRecordService.findAllMedicalRecordsList()
        return if (medicalRecords.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(medicalRecords)
        }
    }

    @GetMapping("/{id}")
    fun getMedicalRecordById(
        @PathVariable id: Long,
    ): ResponseEntity<MedicalRecord?> {
        val medicalRecord = medicalRecordService.findMedicalRecordById(id)
        return if (medicalRecord == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(medicalRecord)
        }
    }

    @PostMapping
    fun addMedicalRecord(
        @RequestBody medicalRecord: MedicalRecord,
    ): ResponseEntity<MedicalRecord?> {
        if (medicalRecord.id != null) {
            return ResponseEntity.badRequest().build()
        }
        val savedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecord)
        return if (savedMedicalRecord == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedMedicalRecord)
        }
    }

    @PutMapping("/{id}")
    fun updateMedicalRecord(
        @PathVariable id: Long,
        @RequestBody medicalRecord: MedicalRecord,
    ): ResponseEntity<MedicalRecord?> {
        if (medicalRecord.id == null) {
            return ResponseEntity.badRequest().build()
        }
        medicalRecordService.findMedicalRecordById(id) ?: return ResponseEntity.notFound().build()
        val updatedMedicalRecord = medicalRecordService.updateMedicalRecord(id, medicalRecord)
        return if (updatedMedicalRecord == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedMedicalRecord)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMedicalRecord(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        medicalRecordService.findMedicalRecordById(id) ?: return ResponseEntity.notFound().build()
        medicalRecordService.deleteMedicalRecord(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleMedicalRecords(
        @RequestBody medicalRecordIds: List<Long>,
    ): ResponseEntity<Void> {
        if (medicalRecordIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        medicalRecordService.deleteMultipleMedicalRecords(medicalRecordIds)
        return ResponseEntity.noContent().build()
    }
}
