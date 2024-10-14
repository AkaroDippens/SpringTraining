package com.example.uchebpracticaspring.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import net.objecthunter.exp4j.ExpressionBuilder

@Controller
class CalculatorController {

    @PostMapping("calculator/calculate")
    fun calculate(
        model: Model,
        @RequestParam("inputData") inputData: String
    ): String {
        val result = try {
            val e = ExpressionBuilder(inputData).build()
            e.evaluate()
        } catch (e: Exception) {
            "Ошибка в выражении"
        }

        model.addAttribute("result", result)
        return "result"
    }
}