package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Specialization
import com.example.medicinesystemapi.repository.SpecializationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SpecializationServiceImpl(
    private val specializationRepository: SpecializationRepository,
) : SpecializationService {
    override fun findAllSpecializations(pageable: Pageable): Page<Specialization> {
        return specializationRepository.findAll(pageable)
    }

    override fun findAllSpecializationsList(): List<Specialization?> {
        return specializationRepository.findAll()
    }

    override fun findSpecializationById(id: Long?): Specialization? {
        return specializationRepository.findById(id ?: 0).orElse(null)
    }

    override fun findSpecializationByName(specializationName: String?): List<Specialization> {
        return specializationRepository.findAll().filter { it.specializationName == specializationName }
    }

    override fun addSpecialization(specialization: Specialization): Specialization? {
        return specializationRepository.save(specialization)
    }

    override fun updateSpecialization(
        id: Long,
        specialization: Specialization,
    ): Specialization? {
        return specializationRepository.save(specialization)
    }

    override fun deleteSpecialization(id: Long) {
        specializationRepository.deleteById(id)
    }

    override fun deleteMultipleSpecializations(specializationIds: List<Long>) {
        specializationRepository.deleteAllById(specializationIds)
    }

    override fun logicalDeleteSpecialization(id: Long) {
        val specialization = specializationRepository.findById(id).orElse(null)
        specialization?.let {
            specializationRepository.save(it)
        }
    }
}
