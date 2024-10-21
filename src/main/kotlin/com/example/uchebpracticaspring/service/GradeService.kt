package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.GradeModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GradeService {
    fun findAllGrades(pageable: Pageable): Page<GradeModel>
    fun findAllGradesList(): List<GradeModel?>?
    fun findGradeById(id: Int?): GradeModel?
    fun findGradeByName(grade: String?): List<GradeModel>
    fun addGrade(grade: GradeModel): GradeModel?
    fun deleteGrade(id: Int)
    fun deleteMultipleGrades(gradeIds: List<Int>)
    fun logicalDeleteGrade(id: Int)
}
