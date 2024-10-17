package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.model.TeacherModel
import com.example.uchebpracticaspring.service.StudentService
import com.example.uchebpracticaspring.service.TeacherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

//Основная бизнес-логика нашего проекта
@Controller
class TeacherController {
    @Autowired
    private val teacherService: TeacherService? = null

    @GetMapping("/teachers")
    fun getAllTeachers(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val teachersPage = teacherService?.findPaginatedTeachers(pageable)
        val teachersS = teacherService?.findAllTeachers()
        val availableSubjects = teachersS?.map { it?.subject }?.distinct()

        val teachers = teachersPage?.content
        model.addAttribute("availableSubjects", availableSubjects)
        model.addAttribute("teachers", teachers)
        model.addAttribute("currentPage", teachersPage?.number)
        model.addAttribute("totalPages", teachersPage?.totalPages)
        model.addAttribute("pageSize", size)

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

        val availableSubjects = teachers?.map { it?.subject }?.distinct()

        model.addAttribute("availableSubjects", availableSubjects)
        model.addAttribute("teachers", filteredTeachers)
        return "teacherList"
    }

    @PostMapping("/teachers/add")
    fun addTeacher(
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam subject: String?,
        @RequestParam age: Int?,
        @RequestParam stage: Int?
    ): String {
        val newTeacher = TeacherModel(
            0,
            name,
            lastName,
            isDeleted = false,
            subject,
            age,
            stage
        )
        teacherService?.addTeacher(newTeacher)
        return "redirect:/teachers"
    }

    @PostMapping("/teachers/update")
    fun updateTeacher(
        @RequestParam id: Int,
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam subject: String?,
        @RequestParam age: Int?,
        @RequestParam stage: Int?
    ): String {
        val updatedTeacher = TeacherModel(id, name, lastName, isDeleted = false, subject, age, stage)
        teacherService?.updateTeacher(updatedTeacher)
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
    fun findTeacherByName(model: Model,
          @RequestParam name: String?,
          @RequestParam lastName: String?): String {
        model.addAttribute("teachers", teacherService?.findTeacherByName(name, lastName))
        return "teacherList"
    }

    @PostMapping("/teachers/deleteMultiple")
    fun deleteMultipleTeachers(@RequestParam teacherIds: List<Int>?): String {
        if (!teacherIds.isNullOrEmpty()) {
            teacherService?.deleteMultipleTeachers(teacherIds)
        } else {
            return "redirect:/teachers"
        }
        return "redirect:/teachers"
    }
}
