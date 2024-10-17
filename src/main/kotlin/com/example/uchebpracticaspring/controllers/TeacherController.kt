package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.StudentModel
import com.example.uchebpracticaspring.model.TeacherModel
import com.example.uchebpracticaspring.service.StudentService
import com.example.uchebpracticaspring.service.TeacherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

//Основная бизнес-логика нашего проекта
@Controller
class TeacherController {
    @Autowired
    private val teacherService: TeacherService? = null

    @GetMapping("/teachers")
    fun getAllTeachers(model: Model): String {
        model.addAttribute("teachers", teacherService?.findAllTeacher()) // просто выгрузка учителей на экран
        return "teacherList"
    }

    @PostMapping("/teachers/add")
    fun addTeacher(
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam subject: String?,
    ): String {
        val newTeacher: TeacherModel = TeacherModel(
            0,
            name,
            lastName,
            subject
        ) // тут мы получаем данные с главных полей, id задается автоматически из нашего репозитория
        teacherService?.addTeacher(newTeacher) // добавление учителя в оперативную память(после перезапуска проекта, все данные стираются)
        return "redirect:/teachers" // Здесь мы с вами используем redirect на наш GetMapping, чтобы не создавать много однотипных страниц, считай просто презагружаем страницу с новыми данными
    }

    @PostMapping("/teachers/update")
    fun updateTeacher(
        @RequestParam id: Int,
        @RequestParam name: String?,
        @RequestParam lastName: String?,
        @RequestParam subject: String?,
    ): String {
        val updatedTeacher: TeacherModel =
            TeacherModel(id, name, lastName, subject) // Получаем новые данные из полей для обновления
        teacherService?.updateTeacher(updatedTeacher) // Ссылаемся на наш сервис для обновления по id
        return "redirect:/teachers" // Здесь мы с вами используем redirect на наш GetMapping, чтобы не создавать много однотипных страниц, считай просто презагружаем страницу с новыми данными
    }

    @PostMapping("/teachers/delete")
    fun deleteTeacher(@RequestParam id: Int): String {
        teacherService?.deleteTeacher(id) // Ссылаемся на наш сервис для удаления по id
        return "redirect:/teachers" // Здесь мы с вами используем redirect на наш GetMapping, чтобы не создавать много однотипных страниц, считай просто презагружаем страницу с новыми данными
    }

    @PostMapping("/teachers/{id}")
    fun findTeacherById(@RequestParam id: Int): String {
        teacherService?.findTeacherById(id)
        return "redirect:/teachers"
    }

    @GetMapping("/teachers/find")
    fun findTeacherByName(model: Model, @RequestParam name: String?, @RequestParam lastName: String?,
                          @RequestParam subject: String?): String {
        model.addAttribute("teachers", teacherService?.findTeacherByName(name, lastName, subject))
        return "teacherList"
    }
}
