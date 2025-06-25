package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.model.Record
import com.example.medicinesystemapi.service.RecordService
import com.example.medicinesystemapi.service.RecordToAppointmentScheduler
import com.example.medicinesystemapi.validation.Validations
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Record", description = "Operations related to records")
@RestController
@RequestMapping("/api/records")
class RecordController(
    private val recordService: RecordService,
    private val recordToAppointmentScheduler: RecordToAppointmentScheduler
) {

    val validations = Validations()

    @GetMapping
    fun getAllRecords(request: HttpServletRequest): ResponseEntity<List<Record?>> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val records = recordService.findAllRecordsList()
        return if (records.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(records)
        }
    }

    @GetMapping("/{id}")
    fun getRecordById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Record?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val record = recordService.findRecordById(id)
        return if (record == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(record)
        }
    }

    @GetMapping("/byuser/{id}")
    fun getRecordByUserId(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<List<Record>?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER", "DB_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val record = recordService.findRecordsByUserId(id.toInt())
        return if (record == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(record)
        }
    }

    @GetMapping("/bydoctor/{doctorId}")
    fun getRecordByDoctorId(
        request: HttpServletRequest,
        @PathVariable doctorId: Long,
    ): ResponseEntity<List<Record>?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val record = recordService.findRecordsByDoctorId(doctorId.toInt())
        return if (record == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(record)
        }
    }

    @PostMapping
    fun addRecord(
        request: HttpServletRequest,
        @RequestBody record: Record,
    ): ResponseEntity<Record?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
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
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody record: Record,
    ): ResponseEntity<Record?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
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
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        recordService.findRecordById(id) ?: return ResponseEntity.notFound().build()
        recordService.deleteRecord(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleRecords(
        request: HttpServletRequest,
        @RequestBody recordIds: List<Long>,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (recordIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        recordService.deleteMultipleRecords(recordIds)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/start-appointment")
    fun startAppointment(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<Appointment?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (recordService.findRecordById(id) == null) {
            return ResponseEntity.notFound().build()
        }
        val appointment = recordToAppointmentScheduler.createAppointmentFromRecord(id)
        return if (appointment == null) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(null) // Приём уже существует
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(appointment)
        }
    }
}
