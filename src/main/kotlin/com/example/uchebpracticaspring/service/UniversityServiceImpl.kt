package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.UniversityModel
import com.example.uchebpracticaspring.repository.UniversityRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class UniversityServiceImpl @Autowired constructor(private val universityRepository: UniversityRepository) : UniversityService {
    override fun findAllUniversities(): List<UniversityModel?> {
        return universityRepository.findAll()
    }

    override fun findUniversityById(id: Int): UniversityModel? {
        return universityRepository.findById(id).orElseThrow()
    }

    override fun findPaginatedUniversities(pageable: Pageable): Page<UniversityModel?> {
        return universityRepository.findAll(pageable)
    }


    override fun findUniversityByName(name: String): List<UniversityModel> {
        return universityRepository.findUniversityByName(name)
    }

    override fun addUniversity(university: UniversityModel): UniversityModel? {
        return universityRepository.save(university)
    }

    override fun deleteUniversity(id: Int) {
        universityRepository.deleteById(id)
    }

    override fun deleteMultipleUniversities(universityIds: List<Int>) {
        universityRepository.deleteMultipleUniversities(universityIds)
    }

    override fun logicalDeleteUniversity(id: Int) {
        val university = universityRepository.findById(id).orElseThrow()
        university.isDeleted = true
        universityRepository.save(university)
    }
}