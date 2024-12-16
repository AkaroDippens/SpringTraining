package com.example.medicinesystemapi.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/error")
class ErrorController {
    @RequestMapping("/404")
    fun notFoundError(): String {
        return "error" // Возвращает шаблон 404.html
    }
}
