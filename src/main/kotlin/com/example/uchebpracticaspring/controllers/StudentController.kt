package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.service.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.PageRequest

//Основная бизнес-логика нашего проекта
@Controller
class StudentController {
    @Autowired
    private val studentService: StudentService? = null

    @GetMapping("/students")
    fun getAllStudents(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val studentsPage = studentService?.findPaginatedStudents(pageable)

        val students = studentsPage?.content
        val availableUniversities = students?.map { it?.university }?.distinct()
        val availableCourses = students?.map { it?.course }?.distinct()
        val availableGrades = students?.map { it?.averageGrade }?.distinct()

        model.addAttribute("availableUniversities", availableUniversities)
        model.addAttribute("availableCourses", availableCourses)
        model.addAttribute("availableGrades", availableGrades)
        model.addAttribute("students", studentsPage?.content)
        model.addAttribute("currentPage", studentsPage?.number)
        model.addAttribute("totalPages", studentsPage?.totalPages)
        model.addAttribute("pageSize", size)

        return "studentList"
    }

    @GetMapping("/students/filter")
    fun filterStudents(
        @RequestParam(required = false) university: String?,
        @RequestParam(required = false) course: String?,
        @RequestParam(required = false) averageGrade: String?,
        model: Model
    ): String {
        val students = studentService?.findAllStudent()

        // Фильтрация по университету, курсу и средней оценке
        val filteredStudents = students?.filter { student ->
            (university.isNullOrEmpty() || student?.university == university) &&
            (course.isNullOrEmpty() || student?.course == course) &&
            (averageGrade.isNullOrEmpty() || student?.averageGrade == averageGrade)
        }

        // Получаем уникальные значения для фильтров
        val availableUniversities = students?.map { it?.university }?.distinct()
        val availableCourses = students?.map { it?.course }?.distinct()
        val availableGrades = students?.map { it?.averageGrade }?.distinct()

        model.addAttribute("students", filteredStudents)
        model.addAttribute("availableUniversities", availableUniversities)
        model.addAttribute("availableCourses", availableCourses)
        model.addAttribute("availableGrades", availableGrades)

        return "studentList"
    }

    @PostMapping("/students/add")
    fun addStudent(
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam firstName: String?,
        @RequestParam middleName: String?,
        @RequestParam averageGrade: String?,
        @RequestParam course: String?,
        @RequestParam university: String?
    ): String {
        val newStudent = StudentModel(
            0,
            name,
            lastName,
            firstName,
            middleName,
            isDeleted = false,
            averageGrade,
            course,
            university
        ) // тут мы получаем данные с главных полей, id задается автоматически из нашего репозитория
        studentService?.addStudent(newStudent) // добавление студента в оперативную память(после перезапуска проекта, все данные стираются)
        return "redirect:/students" // Здесь мы с вами используем redirect на наш GetMapping, чтобы не создавать много однотипных страниц, считай просто презагружаем страницу с новыми данными
    }

    @PostMapping("/students/update")
    fun updateStudent(
        @RequestParam id: Int,
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam firstName: String?,
        @RequestParam middleName: String?,
        @RequestParam averageGrade: String?,
        @RequestParam course: String?,
        @RequestParam university: String?,
    ): String {
        val updatedStudent =
            StudentModel(id, name, lastName, firstName, middleName, isDeleted = false, averageGrade, course, university) // Получаем новые данные из полей для обновления
        studentService?.updateStudent(updatedStudent) // Ссылаемся на наш сервис для обновления по id
        return "redirect:/students" // Здесь мы с вами используем redirect на наш GetMapping, чтобы не создавать много однотипных страниц, считай просто презагружаем страницу с новыми данными
    }

    @PostMapping("/students/delete")
    fun deleteStudent(@RequestParam id: Int, @RequestParam action: String): String {
        when (action) {
            "logical" -> {
                studentService?.logicalDeleteStudent(id)
            }
            "physical" -> {
                studentService?.deleteStudent(id)
            }
        }
        return "redirect:/students"
    }

    @GetMapping("/students/find")
    fun findStudentByName(model: Model,
          @RequestParam name: String?,
          @RequestParam lastName: String?,
          @RequestParam firstName: String?, @RequestParam middleName: String?): String {
        model.addAttribute("students", studentService?.findStudentByName(name, lastName, firstName, middleName))
        return "studentList"
    }

    @PostMapping("/students/deleteMultiple")
    fun deleteMultipleStudents(@RequestParam studentIds: List<Int>?): String {
        if (!studentIds.isNullOrEmpty()) {
            studentService?.deleteMultipleStudents(studentIds)
        } else {
            return "redirect:/students"
        }
        return "redirect:/students" // Перенаправляем на страницу списка студентов
    }
}
