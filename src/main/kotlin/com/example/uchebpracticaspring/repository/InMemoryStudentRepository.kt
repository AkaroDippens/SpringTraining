package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.model.TeacherModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicInteger

@Repository //Репозиторий отвечает за хранение и управление данными студентов в памяти. Он предоставляет методы для выполнения операций(обычные CRUD действия с данными)
class InMemoryStudentRepository {
    private val students: MutableList<StudentModel?> = ArrayList()
    private val idCounter = AtomicInteger(1) // Генерация уникального ID

    fun addStudent(student: StudentModel): StudentModel {
        student.id = idCounter.getAndIncrement() // Установка уникального ID
        students.add(student)
        return student
    }

    fun updateStudent(student: StudentModel): StudentModel? {
        for (i in students.indices) {
            if (students[i]!!.id == student.id) {
                students[i] = student
                return student
            }
        }
        return null // Студент не найден
    }

    fun deleteStudent(id: Int) {
        students.removeIf { student: StudentModel? -> student!!.id == id }
    }

    fun findAllStudents(): List<StudentModel?> {
        return students.filter { it?.isDeleted != true }
    }

    fun findStudentById(id: Int): StudentModel? {
        return students.stream()
            .filter { student: StudentModel? -> student!!.id == id }
            .findFirst()
            .orElse(null)
    }

    fun findStudentByName(name: String?, lastName: String?, firstName: String?, middleName: String?): List<StudentModel?> {
        return students.filter { student: StudentModel? -> (name.isNullOrEmpty() || student?.name == name)
                && (lastName.isNullOrEmpty() || student?.lastName == lastName)
                && (middleName.isNullOrEmpty() || student?.middleName == middleName)
                && (firstName.isNullOrEmpty() || student?.firstName == firstName)  }
    }

    fun deleteMultipleStudents(studentIds: List<Int>?) {
        if (studentIds!!.isNotEmpty()) {
            students.removeIf { student -> student?.id in studentIds }
        }
    }

    fun logicalDeleteStudent(id: Int) {
        val student = students.find { it?.id == id }
        student?.isDeleted = true
    }

    fun findPaginatedStudents(pageable: Pageable): Page<StudentModel?> {
        val filteredStudents = findAllStudents()
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(filteredStudents.size)
        val pageContent = filteredStudents.subList(start, end)
        return PageImpl(pageContent, pageable, filteredStudents.size.toLong())
    }
}