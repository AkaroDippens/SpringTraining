package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.MedicalRecord
import com.example.medicinesystemapi.repository.MedicalRecordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MedicalRecordServiceImpl(
    private val medicalRecordRepository: MedicalRecordRepository,
) : MedicalRecordService {
    override fun findAllMedicalRecords(pageable: Pageable): Page<MedicalRecord> {
        return medicalRecordRepository.findAll(pageable)
    }

    override fun findAllMedicalRecordsList(): List<MedicalRecord?> {
        return medicalRecordRepository.findAll()
    }

    override fun findMedicalRecordById(id: Long?): MedicalRecord? {
        return medicalRecordRepository.findById(id ?: 0).orElse(null)
    }

    override fun findMedicalRecordByUserId(id: Long?): MedicalRecord? {
        return medicalRecordRepository.findByIdUser_Id(id)
    }

    override fun findMedicalRecordsByUserId(userId: Long?): List<MedicalRecord> {
        TODO("Not yet implemented")
    }

    override fun addMedicalRecord(medicalRecord: MedicalRecord): MedicalRecord? {
        return medicalRecordRepository.save(medicalRecord)
    }

    override fun updateMedicalRecord(
        id: Long,
        medicalRecord: MedicalRecord,
    ): MedicalRecord? {
        return medicalRecordRepository.save(medicalRecord)
    }

    override fun deleteMedicalRecord(id: Long) {
        medicalRecordRepository.deleteById(id)
    }

    override fun deleteMultipleMedicalRecords(medicalRecordIds: List<Long>) {
        medicalRecordRepository.deleteAllById(medicalRecordIds)
    }

    override fun logicalDeleteMedicalRecord(id: Long) {
        val medicalRecord = medicalRecordRepository.findById(id).orElse(null)
        medicalRecord?.let {
            medicalRecordRepository.save(it)
        }
    }
}
