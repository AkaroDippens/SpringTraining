package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Appointment
import com.example.medicinesystemapi.repository.AppointmentRepository
import com.example.medicinesystemapi.repository.RecordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AppointmentServiceImpl(
    private val appointmentRepository: AppointmentRepository,
    private val recordRepository: RecordRepository,
) : AppointmentService {
    override fun findAllAppointments(pageable: Pageable): Page<Appointment> {
        return appointmentRepository.findAll(pageable)
    }

    override fun findAllAppointmentsList(): List<Appointment?> {
        return appointmentRepository.findAll()
    }

    override fun findAppointmentById(id: Long?): Appointment? {
        return appointmentRepository.findById(id ?: 0).orElse(null)
    }

    override fun findAppointmentByRecordId(recordId: Int): Appointment? {
        return appointmentRepository.findAll().firstOrNull { it.idRecord?.id == recordId }
    }

    override fun findAppointmentsByDoctorId(doctorId: Int): List<Appointment>? {
        return appointmentRepository.findAll().filter { it.idRecord?.idDoctor?.id == doctorId }
    }

    override fun findAppointmentsByUserId(userId: Long?): List<Appointment> {
        TODO("Not yet implemented")
    }

    override fun addAppointment(appointment: Appointment): Appointment? {
        return appointmentRepository.save(appointment)
    }

    override fun updateAppointment(
        id: Long,
        appointment: Appointment,
    ): Appointment? {
        return appointmentRepository.findById(id).map { existingAppointment ->
            existingAppointment.apply {
                reason = appointment.reason ?: reason
                diagnosis = appointment.diagnosis ?: diagnosis
                recommendations = appointment.recommendations ?: recommendations
            }
        }.map { appointmentRepository.save(it) }.orElse(null)
    }

    override fun deleteAppointment(id: Long) {
        appointmentRepository.deleteById(id)
    }

    override fun deleteMultipleAppointments(appointmentIds: List<Long>) {
        appointmentRepository.deleteAllById(appointmentIds)
    }

    override fun logicalDeleteAppointment(id: Long) {
        val appointment = appointmentRepository.findById(id).orElse(null)
        appointment?.let {
            appointmentRepository.save(it)
        }
    }
}
