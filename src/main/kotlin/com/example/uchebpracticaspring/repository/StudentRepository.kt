package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.StudentModel
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface StudentRepository : JpaRepository<StudentModel, Int> {

    @Query("SELECT s FROM StudentModel s " +
            "WHERE (:name IS NULL OR s.name = :name) " +
            "OR (:lastName IS NULL OR s.lastName = :lastName) " +
            "OR (:firstName IS NULL OR s.firstName = :firstName) " +
            "OR (:middleName IS NULL OR s.middleName = :middleName)")
    fun findStudentByName(
        @Param("name") name: String?,
        @Param("lastName") lastName: String?,
        @Param("firstName") firstName: String?,
        @Param("middleName") middleName: String?
    ): List<StudentModel>

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentModel s WHERE s.id IN :studentIds")
    fun deleteMultipleStudents(@Param("studentIds") studentIds: List<Int>)
}
