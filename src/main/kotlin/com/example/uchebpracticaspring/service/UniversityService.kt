package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.UniversityModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UniversityService {
    fun findAllUniversities(): List<UniversityModel?>?
    fun findUniversityById(id: Int): UniversityModel?
    fun findPaginatedUniversities(pageable: Pageable): Page<UniversityModel?>
    fun findUniversityByName(name: String): List<UniversityModel>
    fun addUniversity(university: UniversityModel): UniversityModel?
    fun deleteUniversity(id: Int)
    fun deleteMultipleUniversities(universityIds: List<Int>)
    fun logicalDeleteUniversity(id: Int)
}
