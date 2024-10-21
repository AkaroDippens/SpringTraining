package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.GradeModel
import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.service.GradeService
import com.example.uchebpracticaspring.service.StudentService
import com.example.uchebpracticaspring.service.UniversityService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.PageRequest
import org.springframework.validation.BindingResult

//Основная бизнес-логика нашего проекта
@Controller
class StudentController {
    @Autowired
    private val studentService: StudentService? = null

    @Autowired
    private val universityService: UniversityService? = null

    @Autowired
    private val gradeService: GradeService? = null

    @GetMapping("/students")
    fun getAllStudents(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val studentsPage = studentService?.findPaginatedStudents(pageable)
        val filteredStudents = studentsPage?.filter{ !it!!.isDeleted }

        val availableUniversities = universityService?.findAllUniversities()
        val availableGrades = gradeService?.findAllGradesList()

        model.addAttribute("availableUniversities", availableUniversities)
        model.addAttribute("availableGrades", availableGrades)
        model.addAttribute("students", filteredStudents)
        model.addAttribute("currentPage", studentsPage?.number)
        model.addAttribute("totalPages", studentsPage?.totalPages)
        model.addAttribute("pageSize", size)
        model.addAttribute("student", StudentModel())

        return "studentList"
    }

    @GetMapping("/students/filter")
    fun filterStudents(
        @RequestParam(required = false) university: String?,
        @RequestParam(required = false) grade: String?,
        model: Model
    ): String {
        val students = studentService?.findAllStudent()

        // Фильтрация по университету и оценке
        val filteredStudents = students?.filter { student ->
            (university.isNullOrEmpty() || student?.university?.name == university) &&
                    (grade.isNullOrEmpty() || student?.grade?.gradeContent == grade)
        }

        val availableUniversities = universityService?.findAllUniversities()
        val availableGrades = gradeService?.findAllGradesList()

        model.addAttribute("students", filteredStudents)
        model.addAttribute("availableUniversities", availableUniversities)
        model.addAttribute("availableGrades", availableGrades)
        model.addAttribute("student", StudentModel()) // Добавьте эту строку

        return "studentList"
    }

    @PostMapping("/students/addOrUpdate")
    fun addOrUpdateStudent(
        @Valid @ModelAttribute newStudent: StudentModel,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            val students = studentService?.findAllStudent()
            val availableUniversities = universityService?.findAllUniversities()
            val availableGrades = gradeService?.findAllGradesList()
            model.addAttribute("students", students)
            model.addAttribute("availableUniversities", availableUniversities)
            model.addAttribute("availableGrades", availableGrades)
            model.addAttribute("student", newStudent)
            return "studentList"
        }

        val grade = gradeService?.findGradeById(newStudent.grade?.id)
        newStudent.grade = grade
        studentService?.addStudent(newStudent)
        return "redirect:/students"
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
    fun findStudentByName(
        model: Model,
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam firstName: String?,
        @RequestParam middleName: String?
    ): String {
        model.addAttribute("students", studentService?.findStudentByName(name, lastName, firstName, middleName))
        model.addAttribute("student", StudentModel()) // Добавьте эту строку
        return "studentList"
    }

    @PostMapping("/students/deleteMultiple")
    fun deleteMultipleStudents(@RequestParam studentIds: List<Int>?): String {
        if (studentIds.isNullOrEmpty()) return "redirect:/students"
        studentService?.deleteMultipleStudents(studentIds)
        return "redirect:/students"
    }
}
