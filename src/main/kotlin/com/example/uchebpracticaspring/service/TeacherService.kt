package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.TeacherModel

interface TeacherService {
    fun findAllTeacher(): List<TeacherModel?>?
    fun findTeacherById(id: Int): TeacherModel?
    fun findTeacherByName(name: String?, lastName: String?, subject: String?): List<TeacherModel?>
    fun addTeacher(teacher: TeacherModel): TeacherModel?
    fun updateTeacher(teacher: TeacherModel): TeacherModel?
    fun deleteTeacher(id: Int)
}
