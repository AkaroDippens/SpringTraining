package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.TeacherModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicInteger

@Repository
class InMemoryTeacherRepository {
    private val teachers: MutableList<TeacherModel?> = ArrayList()
    private val idCounter = AtomicInteger(1)

    fun addTeacher(teacher: TeacherModel): TeacherModel {
        teacher.id = idCounter.getAndIncrement()
        teachers.add(teacher)
        return teacher
    }

    fun updateTeacher(teacher: TeacherModel): TeacherModel? {
        for (i in teachers.indices) {
            if (teachers[i]!!.id == teacher.id) {
                teachers[i] = teacher
                return teacher
            }
        }
        return null
    }

    fun deleteTeacher(id: Int) {
        teachers.removeIf { teacher: TeacherModel? -> teacher!!.id == id }
    }

    fun findAllTeachers(): List<TeacherModel?> {
        return teachers.filter { it?.isDeleted != true }
    }

    fun findTeacherById(id: Int): TeacherModel? {
        return teachers.stream()
            .filter { teacher: TeacherModel? -> teacher!!.id == id }
            .findFirst()
            .orElse(null)
    }

    fun findTeacherByName(name: String?, lastName: String?): List<TeacherModel?> {
        return teachers.filter { teacher: TeacherModel? -> (name.isNullOrEmpty() || teacher?.name == name)
                && (lastName.isNullOrEmpty() || teacher?.lastName == lastName) }
    }

    fun deleteMultipleTeachers(teacherIds: List<Int>?) {
        if (teacherIds!!.isNotEmpty()) {
            teachers.removeIf { teacher -> teacher?.id in teacherIds }
        }
    }

    fun logicalDeleteTeacher(id: Int) {
        val teacher = teachers.find { it?.id == id }
        teacher?.isDeleted = true
    }

    fun findPaginatedTeachers(pageable: Pageable): Page<TeacherModel?> {
        val filteredTeachers = findAllTeachers()
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(filteredTeachers.size)
        val pageContent = filteredTeachers.subList(start, end)
        return PageImpl(pageContent, pageable, filteredTeachers.size.toLong())
    }
}