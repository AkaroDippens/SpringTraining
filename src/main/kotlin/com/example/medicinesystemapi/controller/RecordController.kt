package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Record
import com.example.medicinesystemapi.service.RecordService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Record", description = "Operations related to records")
@RestController
@RequestMapping("/api/records")
class RecordController(private val recordService: RecordService) {
    @GetMapping
    fun getAllRecords(): ResponseEntity<List<Record?>> {
        val records = recordService.findAllRecordsList()
        return if (records.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(records)
        }
    }

    @GetMapping("/{id}")
    fun getRecordById(
        @PathVariable id: Long,
    ): ResponseEntity<Record?> {
        val record = recordService.findRecordById(id)
        return if (record == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(record)
        }
    }

    @GetMapping("/byuser/{id}")
    fun getRecordByUserId(
        @PathVariable id: Long,
    ): ResponseEntity<List<Record>?> {
        val record = recordService.findRecordsByUserId(id.toInt())
        return if (record == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(record)
        }
    }

    @GetMapping("/bydoctor/{doctorId}")
    fun getRecordByDoctorId(
        @PathVariable doctorId: Long,
    ): ResponseEntity<List<Record>?> {
        val record = recordService.findRecordsByDoctorId(doctorId.toInt())
        return if (record == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(record)
        }
    }

    @PostMapping
    fun addRecord(
        @RequestBody record: Record,
    ): ResponseEntity<Record?> {
        // Проверяем, занято ли время
        val existingRecord = recordService.findByDoctorAndTime(record.idDoctor?.id!!.toLong(), record.appointmentDate!!)
        if (existingRecord != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null)
        }

        val savedRecord = recordService.addRecord(record)
        return if (savedRecord == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedRecord)
        }
    }

    @PutMapping("/{id}")
    fun updateRecord(
        @PathVariable id: Long,
        @RequestBody record: Record,
    ): ResponseEntity<Record?> {
        if (record.id == null) {
            return ResponseEntity.badRequest().build()
        }
        recordService.findRecordById(id) ?: return ResponseEntity.notFound().build()
        val updatedRecord = recordService.updateRecord(id, record)
        return if (updatedRecord == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedRecord)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteRecord(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        recordService.findRecordById(id) ?: return ResponseEntity.notFound().build()
        recordService.deleteRecord(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleRecords(
        @RequestBody recordIds: List<Long>,
    ): ResponseEntity<Void> {
        if (recordIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        recordService.deleteMultipleRecords(recordIds)
        return ResponseEntity.noContent().build()
    }
}
