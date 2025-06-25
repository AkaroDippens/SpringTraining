package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Doctor
import com.example.medicinesystemapi.service.DoctorService
import com.example.medicinesystemapi.validation.Validations
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/doctors")
class DoctorController(private val doctorService: DoctorService) {

    val validations = Validations()

    @GetMapping
    fun getAllDoctors(request: HttpServletRequest): ResponseEntity<List<Doctor?>> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val doctors = doctorService.findAllDoctorsList()
        return if (doctors.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(doctors)
        }
    }

    @GetMapping("/{id}")
    fun getDoctorById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Doctor?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val doctor = doctorService.findDoctorById(id)
        return if (doctor == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(doctor)
        }
    }

    @GetMapping("/byspecialization/{id}")
    fun getDoctorBySpecializationId(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<List<Doctor>?> {
        if (!validations.hasAnyRole(request, "ADMIN", "USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val doctor = doctorService.findDoctorBySpecializationId(id)
        return if (doctor == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(doctor)
        }
    }

    @GetMapping("/byfullname/{fullName}")
    fun getDoctorByFullName(
        request: HttpServletRequest,
        @PathVariable fullName: String,
    ): ResponseEntity<Doctor?> {
        if (!validations.hasAnyRole(request, "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val doctor = doctorService.findDoctorByFullName(fullName)
        return if (doctor == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(doctor)
        }
    }

    @GetMapping("/byname/{fullName}")
    fun getDoctorByName(
        request: HttpServletRequest,
        @PathVariable fullName: String,
    ): ResponseEntity<List<Doctor>> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val doctors = doctorService.findDoctorByName(fullName)
        return if (doctors.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(doctors)
        }
    }

    @PostMapping
    fun addDoctor(
        request: HttpServletRequest,
        @RequestBody doctor: Doctor,
    ): ResponseEntity<Doctor?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
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
    fun updateDoctor(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody doctor: Doctor,
    ): ResponseEntity<Doctor?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
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

    @PutMapping("/{id}/specialization")
    fun updateDoctorSpecialization(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestParam specializationId: Long,
    ): ResponseEntity<Doctor?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val updatedDoctor = doctorService.updateDoctorSpecialization(id, specializationId)
        return if (updatedDoctor == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(updatedDoctor)
        }
    }

    @PutMapping("/{id}/building")
    fun updateDoctorBuilding(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestParam buildingId: Long,
    ): ResponseEntity<Doctor?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val updatedDoctor = doctorService.updateDoctorBuilding(id, buildingId)
        return if (updatedDoctor == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(updatedDoctor)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteDoctor(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        doctorService.findDoctorById(id) ?: return ResponseEntity.notFound().build()
        doctorService.deleteDoctor(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleDoctors(
        request: HttpServletRequest,
        @RequestBody doctorIds: List<Long>,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (doctorIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        doctorService.deleteMultipleDoctors(doctorIds)
        return ResponseEntity.noContent().build()
    }
}
