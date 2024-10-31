package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Medicine
import com.example.medicinesystemapi.repository.MedicineRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MedicineServiceImpl(
    private val medicineRepository: MedicineRepository
) : MedicineService {

    override fun findAllMedicines(pageable: Pageable): Page<Medicine> {
        return medicineRepository.findAll(pageable)
    }

    override fun findAllMedicinesList(): List<Medicine?> {
        return medicineRepository.findAll()
    }

    override fun findMedicineById(id: Long?): Medicine? {
        return medicineRepository.findById(id ?: 0).orElse(null)
    }

    override fun findMedicineByName(medicineName: String?): List<Medicine> {
        return medicineRepository.findAll().filter { it.medicineName == medicineName }
    }

    override fun addMedicine(medicine: Medicine): Medicine? {
        return medicineRepository.save(medicine)
    }

    override fun updateMedicine(id: Long, medicine: Medicine): Medicine? {
        return medicineRepository.save(medicine)
    }

    override fun deleteMedicine(id: Long) {
        medicineRepository.deleteById(id)
    }

    override fun deleteMultipleMedicines(medicineIds: List<Long>) {
        medicineRepository.deleteAllById(medicineIds)
    }

    override fun logicalDeleteMedicine(id: Long) {
        val medicine = medicineRepository.findById(id).orElse(null)
        medicine?.let {
            medicineRepository.save(it)
        }
    }
}