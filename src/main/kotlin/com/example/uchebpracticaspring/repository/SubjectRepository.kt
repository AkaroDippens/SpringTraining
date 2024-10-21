package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.SubjectModel
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : JpaRepository<SubjectModel, Int> {

    @Query("SELECT s FROM SubjectModel s " +
            "WHERE (:name IS NULL OR s.name = :name)")
    fun findSubjectByName(
        @Param("name") name: String?
    ): List<SubjectModel>

    @Modifying
    @Transactional
    @Query("DELETE FROM SubjectModel s WHERE s.id IN :subjectIds")
    fun deleteMultipleSubjects(@Param("subjectIds") subjectIds: List<Int>)
}