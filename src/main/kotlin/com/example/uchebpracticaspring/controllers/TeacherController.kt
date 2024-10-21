package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.model.TeacherModel
import com.example.uchebpracticaspring.service.StudentService
import com.example.uchebpracticaspring.service.SubjectService
import com.example.uchebpracticaspring.service.TeacherService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

//Основная бизнес-логика нашего проекта
@Controller
class TeacherController {
    @Autowired
    private val teacherService: TeacherService? = null
    @Autowired
    private val subjectService: SubjectService? = null

    @GetMapping("/teachers")
    fun getAllTeachers(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val teachersPage = teacherService?.findPaginatedTeachers(pageable)
        val filteredTeachers = teachersPage?.filter{ !it!!.isDeleted }
        val availableSubjects = subjectService?.findAllSubjects()

        model.addAttribute("availableSubjects", availableSubjects)
        model.addAttribute("teachers", filteredTeachers)
        model.addAttribute("currentPage", teachersPage?.number)
        model.addAttribute("totalPages", teachersPage?.totalPages)
        model.addAttribute("pageSize", size)
        model.addAttribute("teacher", TeacherModel())

        return "teacherList"
    }

    @GetMapping("/teachers/filter")
    fun filterTeachers(
        @RequestParam(required = false) subject: String?,
        @RequestParam(required = false) age: Int?,
        @RequestParam(required = false) stage: Int?,
        model: Model
    ): String {
        val teachers = teacherService?.findAllTeachers()

        // Фильтрация учителей по предмету, возрасту и стажу
        val filteredTeachers = teachers?.filter { teacher ->
            (subject.isNullOrEmpty() || teacher?.subject == subject) &&
                    (age == null || (teacher?.age ?: 0) > age) &&
                    (stage == null || (teacher?.stage ?: 0) > stage)
        }

        val availableSubjects = subjectService?.findAllSubjects()

        model.addAttribute("availableSubjects", availableSubjects)
        model.addAttribute("teachers", filteredTeachers)
        model.addAttribute("teacher", TeacherModel()) // Добавьте эту строку

        return "teacherList"
    }

    @PostMapping("/teachers/addOrUpdate")
    fun addOrUpdateTeacher(
        @Valid @ModelAttribute newTeacher: TeacherModel,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            val teachers = teacherService?.findAllTeachers()
            val availableSubjects = subjectService?.findAllSubjects()
            model.addAttribute("availableSubjects", availableSubjects)
            model.addAttribute("teachers", teachers)
            model.addAttribute("teacher", newTeacher) // Добавьте эту строку
            return "teacherList"
        }
        teacherService?.addTeacher(newTeacher)
        return "redirect:/teachers"
    }

    @PostMapping("/teachers/delete")
    fun deleteTeacher(@RequestParam id: Int, @RequestParam action: String): String {
        when (action) {
            "logical" -> {
                teacherService?.logicalDeleteTeacher(id)
            }
            "physical" -> {
                teacherService?.deleteTeacher(id)
            }
        }
        return "redirect:/teachers"
    }

    @GetMapping("/teachers/find")
    fun findTeacherByName(
        model: Model,
        @RequestParam name: String?,
        @RequestParam lastName: String?
    ): String {
        val foundTeachers = teacherService?.findTeacherByName(name, lastName)
        model.addAttribute("teachers", foundTeachers)
        model.addAttribute("teacher", TeacherModel()) // Добавьте эту строку
        return "teacherList"
    }

    @PostMapping("/teachers/deleteMultiple")
    fun deleteMultipleTeachers(@RequestParam teacherIds: List<Int>?): String {
        if (teacherIds.isNullOrEmpty()) return "redirect:/teachers"
        teacherService?.deleteMultipleTeachers(teacherIds)
        return "redirect:/teachers"
    }
}
