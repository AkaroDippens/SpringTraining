package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.service.AppointmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(private val appointmentService: AppointmentService) {

    @GetMapping
    fun getAllAppointments(): ResponseEntity<List<Appointment?>> {
        val appointments = appointmentService.findAllAppointmentsList()
        return if (appointments.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(appointments)
        }
    }

    @GetMapping("/{id}")
    fun getAppointmentById(@PathVariable id: Long): ResponseEntity<Appointment?> {
        val appointment = appointmentService.findAppointmentById(id)
        return if (appointment == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(appointment)
        }
    }

    @GetMapping("/record/{recordId}")
    fun getAppointmentByRecordId(@PathVariable recordId: Int): ResponseEntity<Appointment> {
        val appointment = appointmentService.findAppointmentByRecordId(recordId)
        return if (appointment != null) {
            ResponseEntity.ok(appointment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/doctor/{doctorId}")
    fun getAppointmentsByRecordId(@PathVariable doctorId: Int): ResponseEntity<List<Appointment>> {
        val appointment = appointmentService.findAppointmentsByDoctorId(doctorId)
        return if (appointment != null) {
            ResponseEntity.ok(appointment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addAppointment(@RequestBody appointment: Appointment): ResponseEntity<Appointment?> {
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
    fun updateAppointment(@PathVariable id: Long, @RequestBody partialUpdate: Map<String, Any>): ResponseEntity<Appointment?> {
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
    fun deleteAppointment(@PathVariable id: Long): ResponseEntity<Void> {
        appointmentService.findAppointmentById(id) ?: return ResponseEntity.notFound().build()
        appointmentService.deleteAppointment(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleAppointments(@RequestBody appointmentIds: List<Long>): ResponseEntity<Void> {
        if (appointmentIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        appointmentService.deleteMultipleAppointments(appointmentIds)
        return ResponseEntity.noContent().build()
    }
}