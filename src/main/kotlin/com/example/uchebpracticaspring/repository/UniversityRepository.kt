package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.UniversityModel
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UniversityRepository : JpaRepository<UniversityModel, Int> {

    @Query("SELECT u FROM UniversityModel u " +
            "WHERE (:name IS NULL OR u.name = :name)")
    fun findUniversityByName(
        @Param("name") name: String?
    ): List<UniversityModel>

    @Modifying
    @Transactional
    @Query("DELETE FROM UniversityModel u WHERE u.id IN :universityIds")
    fun deleteMultipleUniversities(@Param("universityIds") universityIds: List<Int>)
}