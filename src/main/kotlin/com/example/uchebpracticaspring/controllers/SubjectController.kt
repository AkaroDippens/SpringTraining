package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.SubjectModel
import com.example.uchebpracticaspring.service.SubjectService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SubjectController {
    @Autowired
    private val subjectService: SubjectService? = null

    @GetMapping("/subjects")
    fun getAllSubjects(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val subjectsPage = subjectService?.findPaginatedSubjects(pageable)
        val filteredSubjects = subjectsPage?.filter{ !it!!.isDeleted }

        model.addAttribute("subjects", filteredSubjects)
        model.addAttribute("currentPage", subjectsPage?.number)
        model.addAttribute("totalPages", subjectsPage?.totalPages)
        model.addAttribute("pageSize", size)
        model.addAttribute("subject", SubjectModel())

        return "subjectList"
    }

    @PostMapping("/subjects/addOrUpdate")
    fun addOrUpdateSubject(
        @Valid @ModelAttribute newSubject: SubjectModel,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            val subjects = subjectService?.findAllSubjects()
            model.addAttribute("subjects", subjects)
            model.addAttribute("subject", newSubject) // Добавьте эту строку
            return "subjectList"
        }
        subjectService?.addSubject(newSubject)
        return "redirect:/subjects"
    }

    @PostMapping("/subjects/delete")
    fun deleteSubject(@RequestParam id: Int, @RequestParam action: String): String {
        when (action) {
            "logical" -> {
                subjectService?.logicalDeleteSubject(id)
            }
            "physical" -> {
                subjectService?.deleteSubject(id)
            }
        }
        return "redirect:/subjects"
    }

    @PostMapping("/subjects/deleteMultiple")
    fun deleteMultipleSubjects(@RequestParam subjectIds: List<Int>?): String {
        if (subjectIds.isNullOrEmpty()) return "redirect:/subjects"
        subjectService?.deleteMultipleSubjects(subjectIds)
        return "redirect:/subjects"
    }
}