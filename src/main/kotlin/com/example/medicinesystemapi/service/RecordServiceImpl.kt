package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Doctor
import com.example.medicinesystemapi.model.Record
import com.example.medicinesystemapi.repository.RecipeRepository
import com.example.medicinesystemapi.repository.RecordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RecordServiceImpl(
    private val recordRepository: RecordRepository
) : RecordService {

    override fun findAllRecords(pageable: Pageable): Page<Record> {
        return recordRepository.findAll(pageable)
    }

    override fun findAllRecordsList(): List<Record?> {
        return recordRepository.findAll()
    }

    override fun findRecordById(id: Long?): Record? {
        return recordRepository.findById(id ?: 0).orElse(null)
    }

    override fun findRecordsByUserId(userId: Int?): List<Record>? {
        return recordRepository.findAll().filter { it.idUser?.id == userId }
    }

    override fun findRecordsByDoctorId(doctorId: Int?): List<Record>? {
        return recordRepository.findAll().filter { it.idDoctor?.id == doctorId }
    }

    override fun addRecord(record: Record): Record? {
        return recordRepository.save(record)
    }

    override fun updateRecord(id: Long, record: Record): Record? {
        return recordRepository.save(record)
    }

    override fun deleteRecord(id: Long) {
        recordRepository.deleteById(id)
    }

    override fun deleteMultipleRecords(recordIds: List<Long>) {
        recordRepository.deleteAllById(recordIds)
    }

    override fun logicalDeleteRecord(id: Long) {
        val record = recordRepository.findById(id).orElse(null)
        record?.let {
            recordRepository.save(it)
        }
    }

    override fun findByDoctorAndTime(doctorId: Long, appointmentDate: Instant): Record? {
        return recordRepository.findAll()
            .firstOrNull { it.idDoctor?.id == doctorId.toInt() && it.appointmentDate == appointmentDate }
    }
}