package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Medicine
import com.example.medicinesystemapi.service.MedicineService
import com.example.medicinesystemapi.validation.Validations
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/medicines")
class MedicineController(private val medicineService: MedicineService) {

    val validations = Validations()

    @GetMapping
    fun getAllMedicines(request: HttpServletRequest): ResponseEntity<List<Medicine?>> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val medicines = medicineService.findAllMedicinesList()
        return if (medicines.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(medicines)
        }
    }

    @GetMapping("/{id}")
    fun getMedicineById(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Medicine?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val medicine = medicineService.findMedicineById(id)
        return if (medicine == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(medicine)
        }
    }

    @GetMapping("/byname/{medicineName}")
    fun getMedicineByName(
        request: HttpServletRequest,
        @PathVariable medicineName: String,
    ): ResponseEntity<List<Medicine>> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val medicines = medicineService.findMedicineByName(medicineName)
        return if (medicines.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(medicines)
        }
    }

    @PostMapping
    fun addMedicine(
        request: HttpServletRequest,
        @RequestBody medicine: Medicine,
    ): ResponseEntity<Medicine?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (medicine.id != null) {
            return ResponseEntity.badRequest().build()
        }
        if (medicine.medicineName == null || medicine.medicineName.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        val savedMedicine = medicineService.addMedicine(medicine)
        return if (savedMedicine == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedMedicine)
        }
    }

    @PutMapping("/{id}")
    fun updateMedicine(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestBody medicine: Medicine,
    ): ResponseEntity<Medicine?> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (medicine.id == null) {
            return ResponseEntity.badRequest().build()
        }
        medicineService.findMedicineById(id) ?: return ResponseEntity.notFound().build()
        val updatedMedicine = medicineService.updateMedicine(id, medicine)
        return if (updatedMedicine == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedMedicine)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMedicine(
        request: HttpServletRequest,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        medicineService.findMedicineById(id) ?: return ResponseEntity.notFound().build()
        medicineService.deleteMedicine(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleMedicines(
        request: HttpServletRequest,
        @RequestBody medicineIds: List<Long>,
    ): ResponseEntity<Void> {
        if (!validations.hasAnyRole(request, "ADMIN", "DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (medicineIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        medicineService.deleteMultipleMedicines(medicineIds)
        return ResponseEntity.noContent().build()
    }
}
