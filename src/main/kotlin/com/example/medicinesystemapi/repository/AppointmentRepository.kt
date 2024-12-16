package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Appointment
import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<Appointment, Long> {
    fun existsByIdRecordId(recordId: Int): Boolean
}
