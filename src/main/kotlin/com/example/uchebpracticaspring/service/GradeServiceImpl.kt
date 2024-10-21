package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.GradeModel
import com.example.uchebpracticaspring.repository.GradeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GradeServiceImpl @Autowired constructor(private val gradeRepository: GradeRepository) : GradeService {
    override fun findAllGrades(pageable: Pageable): Page<GradeModel> {
        return gradeRepository.findAllByLogic(pageable)
    }

    override fun findGradeById(id: Int?): GradeModel? {
        return gradeRepository.findById(id!!).orElseThrow()
    }

    override fun findAllGradesList(): List<GradeModel?> {
        return gradeRepository.findAll()
    }

    override fun findGradeByName(grade: String?): List<GradeModel> {
        return gradeRepository.findGradeByGrade(grade)
    }

    override fun addGrade(grade: GradeModel): GradeModel? {
        return gradeRepository.save(grade)
    }

    override fun deleteGrade(id: Int) {
        gradeRepository.deleteById(id)
    }

    override fun deleteMultipleGrades(gradeIds: List<Int>) {
        gradeRepository.deleteMultipleGrades(gradeIds)
    }

    override fun logicalDeleteGrade(id: Int) {
        val grade = gradeRepository.findById(id).orElseThrow()
        grade.isDeleted = true
        gradeRepository.save(grade)
    }
}