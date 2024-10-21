package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.UniversityModel
import com.example.uchebpracticaspring.service.UniversityService
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
class UniversityController {
    @Autowired
    private val universityService: UniversityService? = null

    @GetMapping("/universities")
    fun getAllUniversities(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size)
        val universitiesPage = universityService?.findPaginatedUniversities(pageable)
        val filteredUniversities = universitiesPage?.filter{ !it!!.isDeleted }

        model.addAttribute("universities", filteredUniversities)
        model.addAttribute("currentPage", universitiesPage?.number)
        model.addAttribute("totalPages", universitiesPage?.totalPages)
        model.addAttribute("pageSize", size)
        model.addAttribute("university", UniversityModel())

        return "universityList"
    }

    @PostMapping("/universities/addOrUpdate")
    fun addOrUpdateUniversity(
        @Valid @ModelAttribute newUniversity: UniversityModel,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            val universities = universityService?.findAllUniversities()
            model.addAttribute("universities", universities)
            model.addAttribute("university", newUniversity) // Добавьте эту строку
            return "universityList"
        }
        universityService?.addUniversity(newUniversity)
        return "redirect:/universities"
    }

    @PostMapping("/universities/delete")
    fun deleteUniversity(@RequestParam id: Int, @RequestParam action: String): String {
        when (action) {
            "logical" -> {
                universityService?.logicalDeleteUniversity(id)
            }
            "physical" -> {
                universityService?.deleteUniversity(id)
            }
        }
        return "redirect:/universities"
    }

    @PostMapping("/universities/deleteMultiple")
    fun deleteMultipleUniversities(@RequestParam universityIds: List<Int>?): String {
        if (universityIds.isNullOrEmpty()) return "redirect:/universities"
        universityService?.deleteMultipleUniversities(universityIds)
        return "redirect:/universities"
    }
}