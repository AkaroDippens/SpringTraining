package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.repository.AppointmentRepository
import com.example.medicinesystemapi.repository.RecordRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RecordToAppointmentScheduler(
    private val recordRepository: RecordRepository,
    private val appointmentRepository: AppointmentRepository
) {

    @Scheduled(fixedRate = 60000) // Запускается каждую минуту
    fun processRecordsToAppointments() {
        val now = Instant.now()
        val overdueRecords = recordRepository.findAll()
            .filter { it.appointmentDate?.isAfter(now) == true }

        for (record in overdueRecords) {
            // Проверяем, что ещё не существует приёма для этой записи
            if (!appointmentRepository.existsByIdRecordId(record.id!!)) {
                val appointment = Appointment().apply {
                    idRecord = record
                    reason = "Причина не указана"
                    diagnosis = "Диагноз не указан"
                    recommendations = "Рекомендации не указаны"
                }
                appointmentRepository.save(appointment)
            }
        }
    }
}