package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.TeacherModel
import com.example.uchebpracticaspring.repository.InMemoryStudentRepository
import com.example.uchebpracticaspring.repository.InMemoryTeacherRepository
import org.springframework.stereotype.Service


//Сервисный слой отвечает за бизнес-логику приложения. Он использует репозиторий для выполнения операций с данными и может включать дополнительные проверки или преобразования данных
//так же мы тут можем настроить инкапсуляцию
//А если простыми словами тут происходит разделенние запросов от контроллера к сервису
@Service
class InMemoryTeacherServiceImpl(private val teacherRepository: InMemoryTeacherRepository): TeacherService {
    override fun findAllTeacher(): List<TeacherModel?> {
        return teacherRepository.findAllTeachers()
    }

    override fun findTeacherById(id: Int): TeacherModel? {
        return teacherRepository.findTeacherById(id)
    }

    override fun addTeacher(teacher: TeacherModel): TeacherModel? {
        return teacherRepository.addTeacher(teacher)
    }

    override fun updateTeacher(teacher: TeacherModel): TeacherModel? {
        return teacherRepository.updateTeacher(teacher)
    }

    override fun deleteTeacher(id: Int) {
        return teacherRepository.deleteTeacher(id)
    }

    override fun findTeacherByName(name: String?, lastName: String?, subject: String?): List<TeacherModel?> {
        return teacherRepository.findTeacherByName(name, lastName, subject)
    }
}
