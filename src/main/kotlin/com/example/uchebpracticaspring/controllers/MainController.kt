package com.example.uchebpracticaspring.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {
    @GetMapping("/")
    fun getHome(): String {
        return "index"
    }

    @GetMapping("/calculator")
    fun getCalculator(): String {
        return "calculator"
    }

    @GetMapping("/convert")
    fun getConverter(): String {
        return "converter"
    }
}