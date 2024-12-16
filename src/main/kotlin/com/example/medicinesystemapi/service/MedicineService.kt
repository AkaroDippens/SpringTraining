package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Medicine
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MedicineService {
    fun findAllMedicines(pageable: Pageable): Page<Medicine>

    fun findAllMedicinesList(): List<Medicine?>

    fun findMedicineById(id: Long?): Medicine?

    fun findMedicineByName(medicineName: String?): List<Medicine>

    fun addMedicine(medicine: Medicine): Medicine?

    fun updateMedicine(
        id: Long,
        medicine: Medicine,
    ): Medicine?

    fun deleteMedicine(id: Long)

    fun deleteMultipleMedicines(medicineIds: List<Long>)

    fun logicalDeleteMedicine(id: Long)
}
