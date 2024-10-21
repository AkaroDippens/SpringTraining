package com.example.uchebpracticaspring.controllers

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
class MainController {
    @GetMapping("/")
    fun getHome(model: Model): String {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val currentUser = authentication.principal as org.springframework.security.core.userdetails.User
        model.addAttribute("currentUser", currentUser)
        return "index"
    }
}