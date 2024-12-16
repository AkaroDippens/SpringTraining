package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Doctor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DoctorService {
    fun findAllDoctors(pageable: Pageable): Page<Doctor>

    fun findAllDoctorsList(): List<Doctor?>

    fun findDoctorById(id: Long?): Doctor?

    fun findDoctorBySpecializationId(id: Long?): List<Doctor>?

    fun findDoctorByFullName(fullName: String?): Doctor?

    fun findDoctorByName(fullName: String?): List<Doctor>

    fun addDoctor(doctor: Doctor): Doctor?

    fun updateDoctor(
        id: Long,
        doctor: Doctor,
    ): Doctor?

    fun deleteDoctor(id: Long)

    fun deleteMultipleDoctors(doctorIds: List<Long>)

    fun logicalDeleteDoctor(id: Long)

    fun updateDoctorSpecialization(
        doctorId: Long,
        specializationId: Long,
    ): Doctor?
}
