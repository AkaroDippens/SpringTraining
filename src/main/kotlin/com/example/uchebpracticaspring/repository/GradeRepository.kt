package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.GradeModel
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GradeRepository : JpaRepository<GradeModel, Int> {

    @Query(
        "SELECT g FROM GradeModel g " +
            "WHERE (:grade IS NULL OR g.gradeContent = :grade)"
    )
    fun findGradeByGrade(
        @Param("grade") grade: String?
    ): List<GradeModel>

    @Query("SELECT g FROM GradeModel g WHERE g.isDeleted = false")
    fun findAllByLogic(pageable: Pageable): Page<GradeModel>

    @Modifying
    @Transactional
    @Query("DELETE FROM GradeModel g WHERE g.id IN :gradeIds")
    fun deleteMultipleGrades(@Param("gradeIds") gradeIds: List<Int>)
}