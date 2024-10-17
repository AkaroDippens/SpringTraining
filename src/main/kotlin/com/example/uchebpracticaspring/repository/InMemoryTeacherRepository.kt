package com.example.uchebpracticaspring.repository

import com.example.uchebpracticaspring.model.TeacherModel
import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicInteger

@Repository //Репозиторий отвечает за хранение и управление данными учителей в памяти. Он предоставляет методы для выполнения операций(обычные CRUD действия с данными)
class InMemoryTeacherRepository {
    private val teachers: MutableList<TeacherModel?> = ArrayList()
    private val idCounter = AtomicInteger(1) // Генерация уникального ID

    fun addTeacher(teacher: TeacherModel): TeacherModel {
        teacher.id = idCounter.getAndIncrement() // Установка уникального ID
        teachers.add(teacher)
        return teacher
    }

    fun updateTeacher(teacher: TeacherModel): TeacherModel? {
        for (i in teachers.indices) {
            if (teachers[i]?.id == teacher.id) {
                teachers[i] = teacher
                return teacher
            }
        }
        return null // Учитель не найден
    }

    fun deleteTeacher(id: Int) {
        teachers.removeIf { teacher: TeacherModel? -> teacher!!.id == id }
    }

    fun findAllTeachers(): List<TeacherModel?> {
        return ArrayList(teachers)
    }

    fun findTeacherById(id: Int): TeacherModel? {
        return teachers.stream()
            .filter { teacher: TeacherModel? -> teacher!!.id == id }
            .findFirst()
            .orElse(null)
    }

    fun findTeacherByName(name: String?, lastName: String?, subject: String?): List<TeacherModel?> {
        return teachers.filter { teacher: TeacherModel? -> (name.isNullOrEmpty() || teacher?.name == name) && (lastName.isNullOrEmpty()
                || teacher?.lastName == lastName) && (subject.isNullOrEmpty() || teacher?.subject == subject)
        }
    }
}