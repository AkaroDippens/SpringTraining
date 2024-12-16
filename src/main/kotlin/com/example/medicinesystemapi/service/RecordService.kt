package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Doctor
import com.example.medicinesystemapi.model.Record
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant

interface RecordService {
    fun findAllRecords(pageable: Pageable): Page<Record>
    fun findAllRecordsList(): List<Record?>
    fun findRecordById(id: Long?): Record?
    fun findRecordsByUserId(userId: Int?): List<Record>?
    fun findRecordsByDoctorId(doctorId: Int?): List<Record>?
    fun addRecord(record: Record): Record?
    fun updateRecord(id: Long, record: Record): Record?
    fun deleteRecord(id: Long)
    fun deleteMultipleRecords(recordIds: List<Long>)
    fun logicalDeleteRecord(id: Long)
    fun findByDoctorAndTime(doctorId: Long, appointmentDate: Instant): Record?
}