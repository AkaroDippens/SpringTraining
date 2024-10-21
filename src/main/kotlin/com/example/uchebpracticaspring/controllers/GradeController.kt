package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.GradeModel
import com.example.uchebpracticaspring.service.GradeService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class GradeController(private val gradeService: GradeService) {

    @GetMapping("/grades")
    fun getAllGrades(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val gradesPage = gradeService.findAllGrades(pageable)
        val allGradeModels = gradeService.findAllGradesList()
        val gradeModels = gradesPage.content
        model.addAttribute("gradeModels", gradeModels)
        model.addAttribute("allGradeModels", allGradeModels)
        model.addAttribute("currentPage", gradesPage.number)
        model.addAttribute("totalPages", gradesPage.totalPages)
        model.addAttribute("pageSize", size)
        model.addAttribute("gradeModel", GradeModel())

        return "gradeList"
    }

    @PostMapping("/grades/addOrUpdate")
    fun addOrUpdateGrade(
        @Valid @ModelAttribute newGrade: GradeModel,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            val gradeModels = gradeService.findAllGradesList()
            model.addAttribute("gradeModels", gradeModels)
            model.addAttribute("gradeModel", newGrade)
            return "gradeList"
        }
        gradeService.addGrade(newGrade)
        return "redirect:/grades"
    }

    @PostMapping("/grades/delete")
    fun deleteGrade(@RequestParam id: Int, @RequestParam action: String): String {
        when (action) {
            "logical" -> {
                gradeService.logicalDeleteGrade(id)
            }
            "physical" -> {
                gradeService.deleteGrade(id)
            }
        }
        return "redirect:/grades"
    }

    @PostMapping("/grades/deleteMultiple")
    fun deleteMultipleGrades(@RequestParam gradeIds: List<Int>?): String {
        if (gradeIds.isNullOrEmpty()) return "redirect:/grades"
        gradeService.deleteMultipleGrades(gradeIds)
        return "redirect:/grades"
    }
}