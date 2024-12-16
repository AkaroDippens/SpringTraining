package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Medicine
import com.example.medicinesystemapi.service.MedicineService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/medicines")
class MedicineController(private val medicineService: MedicineService) {
    @GetMapping
    fun getAllMedicines(): ResponseEntity<List<Medicine?>> {
        val medicines = medicineService.findAllMedicinesList()
        return if (medicines.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(medicines)
        }
    }

    @GetMapping("/{id}")
    fun getMedicineById(
        @PathVariable id: Long,
    ): ResponseEntity<Medicine?> {
        val medicine = medicineService.findMedicineById(id)
        return if (medicine == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(medicine)
        }
    }

    @GetMapping("/byname/{medicineName}")
    fun getMedicineByName(
        @PathVariable medicineName: String,
    ): ResponseEntity<List<Medicine>> {
        val medicines = medicineService.findMedicineByName(medicineName)
        return if (medicines.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(medicines)
        }
    }

    @PostMapping
    fun addMedicine(
        @RequestBody medicine: Medicine,
    ): ResponseEntity<Medicine?> {
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
        @PathVariable id: Long,
        @RequestBody medicine: Medicine,
    ): ResponseEntity<Medicine?> {
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
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        medicineService.findMedicineById(id) ?: return ResponseEntity.notFound().build()
        medicineService.deleteMedicine(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleMedicines(
        @RequestBody medicineIds: List<Long>,
    ): ResponseEntity<Void> {
        if (medicineIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        medicineService.deleteMultipleMedicines(medicineIds)
        return ResponseEntity.noContent().build()
    }
}
