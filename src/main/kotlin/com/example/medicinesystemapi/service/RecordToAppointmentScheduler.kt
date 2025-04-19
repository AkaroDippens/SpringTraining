package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.repository.AppointmentRepository
import com.example.medicinesystemapi.repository.RecordRepository
import org.springframework.stereotype.Service

@Service
class RecordToAppointmentScheduler(
    private val recordRepository: RecordRepository,
    private val appointmentRepository: AppointmentRepository,
) {
    fun createAppointmentFromRecord(recordId: Long): Appointment? {
        val record = recordRepository.findById(recordId).orElse(null) ?: return null

        if (appointmentRepository.existsByIdRecordId(record.id!!)) {
            return null
        }

        val appointment = Appointment().apply {
            idRecord = record
            reason = null
            diagnosis = null
            recommendations = null
        }
        return appointmentRepository.save(appointment)
    }
}