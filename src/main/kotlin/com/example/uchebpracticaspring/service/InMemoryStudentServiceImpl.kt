package com.example.uchebpracticaspring.service

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.repository.InMemoryStudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


//Сервисный слой отвечает за бизнес-логику приложения. Он использует репозиторий для выполнения операций с данными и может включать дополнительные проверки или преобразования данных
//так же мы тут можем настроить инкапсуляцию
//А если простыми словами тут происходит разделенние запросов от контроллера к сервису
@Service
class InMemoryStudentServiceImpl(private val studentRepository: InMemoryStudentRepository) : StudentService {
    override fun findAllStudent(): List<StudentModel?> {
        return studentRepository.findAllStudents()
    }

    override fun findStudentById(id: Int): StudentModel? {
        return studentRepository.findStudentById(id)
    }

    override fun addStudent(student: StudentModel): StudentModel? {
        return studentRepository.addStudent(student)
    }

    override fun updateStudent(student: StudentModel): StudentModel? {
        return studentRepository.updateStudent(student)
    }

    override fun deleteStudent(id: Int) {
        studentRepository.deleteStudent(id)
    }

    override fun findStudentByName(name: String?, lastName: String?, firstName: String?, middleName: String?): List<StudentModel?> {
        return studentRepository.findStudentByName(name, lastName, firstName, middleName)
    }

    override fun deleteMultipleStudents(studentIds: List<Int>) {
        studentRepository.deleteMultipleStudents(studentIds)
    }

    override fun logicalDeleteStudent(id: Int) {
        studentRepository.logicalDeleteStudent(id)
    }

    override fun findPaginatedStudents(pageable: Pageable): Page<StudentModel?> {
        return studentRepository.findPaginatedStudents(pageable)
    }
}
