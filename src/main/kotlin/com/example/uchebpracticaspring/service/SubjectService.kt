package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.SubjectModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SubjectService {
    fun findAllSubjects(): List<SubjectModel?>?
    fun findSubjectById(id: Int): SubjectModel?
    fun findPaginatedSubjects(pageable: Pageable): Page<SubjectModel?>
    fun findSubjectByName(name: String?): List<SubjectModel>
    fun addSubject(subject: SubjectModel): SubjectModel?
    fun deleteSubject(id: Int)
    fun deleteMultipleSubjects(subjectIds: List<Int>)
    fun logicalDeleteSubject(id: Int)
}