package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.MedicalRecord
import org.springframework.data.jpa.repository.JpaRepository

interface MedicalRecordRepository : JpaRepository<MedicalRecord, Long>{
    fun findByIdUser_Id(userId: Long?): MedicalRecord?
}