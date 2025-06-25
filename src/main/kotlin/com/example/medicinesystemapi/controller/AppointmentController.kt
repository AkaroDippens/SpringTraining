package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.service.AppointmentService
import com.example.medicinesystemapi.validation.Validations
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(private val appointmentService: AppointmentService) {

    val validations = Validations()

    @GetMapping
    fun getAllAppointments(request: HttpServletRequest): ResponseEntity<List<Appointment?>> {
        if (!validations.hasAnyRole(request, "DOCTOR", "USER", "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val appointments = appointmentService.findAllAppointmentsList()
        return if (appointments.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(appointments)
        }
    }

    @GetMapping("/{id}")
    fun getAppointmentById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Appointment?> {
        if (!validations.hasAnyRole(request, "DOCTOR", "USER", "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val appointment = appointmentService.findAppointmentById(id)
        return if (appointment == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(appointment)
        }
    }

    @GetMapping("/record/{recordId}")
    fun getAppointmentByRecordId(
        request: HttpServletRequest,
        @PathVariable recordId: Int,
    ): ResponseEntity<Appointment> {
        if (!validations.hasAnyRole(request, "DOCTOR", "USER", "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val appointment = appointmentService.findAppointmentByRecordId(recordId)
        return if (appointment != null) {
            ResponseEntity.ok(appointment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/doctor/{doctorId}")
    fun getAppointmentsByDoctorId(
        request: HttpServletRequest,
        @PathVariable doctorId: Int,
    ): ResponseEntity<List<Appointment>> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val appointments = appointmentService.findAppointmentsByDoctorId(doctorId)
        return if (appointments != null) {
            ResponseEntity.ok(appointments)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addAppointment(
        request: HttpServletRequest,
        @RequestBody appointment: Appointment,
    ): ResponseEntity<Appointment?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (appointment.id != null) {
            return ResponseEntity.badRequest().build()
        }
        val savedAppointment = appointmentService.addAppointment(appointment)
        return if (savedAppointment == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment)
        }
    }

    @PutMapping("/{id}")
    fun updateAppointment(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody partialUpdate: Map<String, Any>,
    ): ResponseEntity<Appointment?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val existingAppointment = appointmentService.findAppointmentById(id) ?: return ResponseEntity.notFound().build()

        partialUpdate["reason"]?.let { existingAppointment.reason = it as String }
        partialUpdate["diagnosis"]?.let { existingAppointment.diagnosis = it as String }
        partialUpdate["recommendations"]?.let { existingAppointment.recommendations = it as String }

        val updatedAppointment = appointmentService.updateAppointment(id, existingAppointment)
        return if (updatedAppointment == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedAppointment)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteAppointment(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        appointmentService.findAppointmentById(id) ?: return ResponseEntity.notFound().build()
        appointmentService.deleteAppointment(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleAppointments(
        request: HttpServletRequest,
        @RequestBody appointmentIds: List<Long>,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (appointmentIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        appointmentService.deleteMultipleAppointments(appointmentIds)
        return ResponseEntity.noContent().build()
    }
}