package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Doctor
import com.example.medicinesystemapi.service.DoctorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/doctors")
class DoctorController(private val doctorService: DoctorService) {

    @GetMapping
    fun getAllDoctors(): ResponseEntity<List<Doctor?>> {
        val doctors = doctorService.findAllDoctorsList()
        return if (doctors.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(doctors)
        }
    }

    @GetMapping("/{id}")
    fun getDoctorById(@PathVariable id: Long): ResponseEntity<Doctor?> {
        val doctor = doctorService.findDoctorById(id)
        return if (doctor == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(doctor)
        }
    }

    @GetMapping("/byname/{fullName}")
    fun getDoctorByName(@PathVariable fullName: String): ResponseEntity<List<Doctor>> {
        val doctors = doctorService.findDoctorByName(fullName)
        return if (doctors.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(doctors)
        }
    }

    @PostMapping
    fun addDoctor(@RequestBody doctor: Doctor): ResponseEntity<Doctor?> {
        if (doctor.id != null) {
            return ResponseEntity.badRequest().build()
        }
        if (doctor.fullName == null || doctor.fullName.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        val savedDoctor = doctorService.addDoctor(doctor)
        return if (savedDoctor == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedDoctor)
        }
    }

    @PutMapping("/{id}")
    fun updateDoctor(@PathVariable id: Long, @RequestBody doctor: Doctor): ResponseEntity<Doctor?> {
        if (doctor.id == null) {
            return ResponseEntity.badRequest().build()
        }
        doctorService.findDoctorById(id) ?: return ResponseEntity.notFound().build()
        val updatedDoctor = doctorService.updateDoctor(id, doctor)
        return if (updatedDoctor == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedDoctor)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteDoctor(@PathVariable id: Long): ResponseEntity<Void> {
        doctorService.findDoctorById(id) ?: return ResponseEntity.notFound().build()
        doctorService.deleteDoctor(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleDoctors(@RequestBody doctorIds: List<Long>): ResponseEntity<Void> {
        if (doctorIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        doctorService.deleteMultipleDoctors(doctorIds)
        return ResponseEntity.noContent().build()
    }
}