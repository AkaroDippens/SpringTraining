package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


//Сервисный слой отвечает за бизнес-логику приложения. Он использует репозиторий для выполнения операций с данными и может включать дополнительные проверки или преобразования данных
//так же мы тут можем настроить инкапсуляцию
//А если простыми словами тут происходит разделенние запросов от контроллера к сервису
@Service
class StudentServiceImpl @Autowired constructor(private val studentRepository: StudentRepository) : StudentService {
    override fun findAllStudent(): List<StudentModel?> {
        return studentRepository.findAll()
    }

    override fun findStudentById(id: Int): StudentModel? {
        return studentRepository.findById(id).orElseThrow()
    }

    override fun addStudent(student: StudentModel): StudentModel? {
        return studentRepository.saveAndFlush(student)
    }

    override fun deleteStudent(id: Int) {
        studentRepository.deleteById(id)
    }

    override fun findStudentByName(name: String?, lastName: String?, firstName: String?, middleName: String?): List<StudentModel> {
        return studentRepository.findStudentByName(name, lastName, firstName, middleName)
    }

    override fun deleteMultipleStudents(studentIds: List<Int>) {
        studentRepository.deleteMultipleStudents(studentIds)
    }

    override fun logicalDeleteStudent(id: Int) {
        val student = studentRepository.findById(id).orElseThrow()
        student.isDeleted = true
        studentRepository.save(student)
    }

    override fun findPaginatedStudents(pageable: Pageable): Page<StudentModel?> {
        return studentRepository.findAll(pageable)
    }
}
