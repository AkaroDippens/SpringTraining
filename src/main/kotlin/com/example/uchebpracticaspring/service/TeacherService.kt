package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.TeacherModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TeacherService {
    fun findAllTeachers(): List<TeacherModel?>?
    fun findTeacherById(id: Int): TeacherModel?
    fun findPaginatedTeachers(pageable: Pageable): Page<TeacherModel?>
    fun findTeacherByName(name: String?, lastName: String?): List<TeacherModel?>
    fun addTeacher(teacher: TeacherModel): TeacherModel?
    fun deleteTeacher(id: Int)
    fun deleteMultipleTeachers(teacherIds: List<Int>)
    fun logicalDeleteTeacher(id: Int)
}
