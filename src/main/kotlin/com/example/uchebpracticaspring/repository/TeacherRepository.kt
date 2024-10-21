package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.TeacherModel
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository : JpaRepository<TeacherModel, Int> {

    @Query("SELECT t FROM TeacherModel t " +
            "WHERE (:name IS NULL OR t.name = :name) " +
            "OR (:lastName IS NULL OR t.lastName = :lastName)")
    fun findTeacherByName(
        @Param("name") name: String?,
        @Param("lastName") lastName: String?
    ): List<TeacherModel>

    @Modifying
    @Transactional
    @Query("DELETE FROM TeacherModel t WHERE t.id IN :teacherIds")
    fun deleteMultipleTeachers(@Param("teacherIds") teacherIds: List<Int>)
}