package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.model.Record
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AppointmentService {
    fun findAllAppointments(pageable: Pageable): Page<Appointment>
    fun findAllAppointmentsList(): List<Appointment?>
    fun findAppointmentById(id: Long?): Appointment?
    fun findAppointmentsByUserId(userId: Long?): List<Appointment>
    fun findAppointmentByRecordId(recordId: Int): Appointment?
    fun findAppointmentsByDoctorId(doctorId: Int): List<Appointment>?
    fun addAppointment(appointment: Appointment): Appointment?
    fun updateAppointment(id: Long, appointment: Appointment): Appointment?
    fun deleteAppointment(id: Long)
    fun deleteMultipleAppointments(appointmentIds: List<Long>)
    fun logicalDeleteAppointment(id: Long)
}
