package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Specialization
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SpecializationService {
    fun findAllSpecializations(pageable: Pageable): Page<Specialization>
    fun findAllSpecializationsList(): List<Specialization?>
    fun findSpecializationById(id: Long?): Specialization?
    fun findSpecializationByName(specializationName: String?): List<Specialization>
    fun addSpecialization(specialization: Specialization): Specialization?
    fun updateSpecialization(id: Long, specialization: Specialization): Specialization?
    fun deleteSpecialization(id: Long)
    fun deleteMultipleSpecializations(specializationIds: List<Long>)
    fun logicalDeleteSpecialization(id: Long)
}