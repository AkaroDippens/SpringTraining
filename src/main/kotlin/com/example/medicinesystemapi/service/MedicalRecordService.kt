package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.MedicalRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MedicalRecordService {
    fun findAllMedicalRecords(pageable: Pageable): Page<MedicalRecord>
    fun findAllMedicalRecordsList(): List<MedicalRecord?>
    fun findMedicalRecordById(id: Long?): MedicalRecord?
    fun findMedicalRecordsByUserId(userId: Long?): List<MedicalRecord>
    fun addMedicalRecord(medicalRecord: MedicalRecord): MedicalRecord?
    fun updateMedicalRecord(id: Long, medicalRecord: MedicalRecord): MedicalRecord?
    fun deleteMedicalRecord(id: Long)
    fun deleteMultipleMedicalRecords(medicalRecordIds: List<Long>)
    fun logicalDeleteMedicalRecord(id: Long)
}
