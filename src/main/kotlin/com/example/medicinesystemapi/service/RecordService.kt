package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Record
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RecordService {
    fun findAllRecords(pageable: Pageable): Page<Record>
    fun findAllRecordsList(): List<Record?>
    fun findRecordById(id: Long?): Record?
    fun addRecord(record: Record): Record?
    fun updateRecord(id: Long, record: Record): Record?
    fun deleteRecord(id: Long)
    fun deleteMultipleRecords(recordIds: List<Long>)
    fun logicalDeleteRecord(id: Long)
}