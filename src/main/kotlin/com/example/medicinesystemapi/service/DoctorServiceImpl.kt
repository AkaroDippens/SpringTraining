package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Doctor
import com.example.medicinesystemapi.repository.BuildingRepository
import com.example.medicinesystemapi.repository.DoctorRepository
import com.example.medicinesystemapi.repository.SpecializationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class DoctorServiceImpl(
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
    private val buildingRepository: BuildingRepository,
) : DoctorService {
    override fun findAllDoctors(pageable: Pageable): Page<Doctor> {
        return doctorRepository.findAll(pageable)
    }

    override fun findAllDoctorsList(): List<Doctor?> {
        return doctorRepository.findAll()
    }

    override fun findDoctorById(id: Long?): Doctor? {
        return doctorRepository.findById(id ?: 0).orElse(null)
    }

    override fun findDoctorBySpecializationId(id: Long?): List<Doctor>? {
        return doctorRepository.findAll().filter { it.idSpecialization?.id == id?.toInt() }
    }

    override fun findDoctorByFullName(fullName: String?): Doctor? {
        return doctorRepository.findAll().firstOrNull { it.fullName == fullName }
    }

    override fun findDoctorByName(fullName: String?): List<Doctor> {
        return doctorRepository.findAll().filter { it.fullName == fullName }
    }

    override fun addDoctor(doctor: Doctor): Doctor? {
        return doctorRepository.save(doctor)
    }

    override fun updateDoctor(
        id: Long,
        doctor: Doctor,
    ): Doctor? {
        return doctorRepository.save(doctor)
    }

    override fun updateDoctorSpecialization(
        doctorId: Long,
        specializationId: Long,
    ): Doctor? {
        val doctor = doctorRepository.findById(doctorId).orElse(null) ?: return null
        val specialization = specializationRepository.findById(specializationId).orElse(null) ?: return null

        doctor.idSpecialization = specialization
        return doctorRepository.save(doctor)
    }

    override fun updateDoctorBuilding(doctorId: Long, buildingId: Long): Doctor? {
        val doctor = doctorRepository.findById(doctorId).orElse(null) ?: return null
        val building = buildingRepository.findById(buildingId).orElse(null) ?: return null

        doctor.idBuilding = building
        return doctorRepository.save(doctor)
    }

    override fun deleteDoctor(id: Long) {
        doctorRepository.deleteById(id)
    }

    override fun deleteMultipleDoctors(doctorIds: List<Long>) {
        doctorRepository.deleteAllById(doctorIds)
    }

    override fun logicalDeleteDoctor(id: Long) {
        val doctor = doctorRepository.findById(id).orElse(null)
        doctor?.let {
            doctorRepository.save(it)
        }
    }
}
