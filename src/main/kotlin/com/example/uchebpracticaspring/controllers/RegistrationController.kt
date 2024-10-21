package com.example.uchebpracticaspring.controllers

import com.example.uchebpracticaspring.model.UserModel
import com.example.uchebpracticaspring.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RegistrationController @Autowired constructor(
    private val userService: UserService
) {
    @GetMapping("/register")
    fun showRegistrationForm(@RequestParam(value = "error", required = false) error: String?, model: Model): String {
        model.addAttribute("user", UserModel())
        if (error != null) {
            when (error) {
                "invalid_fields" -> model.addAttribute("message", "Есть неправильно заполненные поля")
                "validation_error" -> model.addAttribute("message", "Слишком короткий пароль (< 4)")
                "empty_fields_error" -> model.addAttribute("message", "Есть пустые поля")
                "user_exists" -> model.addAttribute("message", "Пользователь с таким логином уже существует")
                "registration_error" -> model.addAttribute("message", "Ошибка при сохранении данных")
                else -> model.addAttribute("message", "Неизвестная ошибка")
            }
        }
        return "register"
    }

    @PostMapping("/register")
    fun registerUser(@Valid @ModelAttribute user: UserModel, bindingResult: BindingResult, model: Model): String {
        try {
            if(bindingResult.hasErrors()) {
                return "redirect:/register?error=invalid_fields"
            }
            if (userService.getUserByName(user.username) != null) {
                return "redirect:/register?error=user_exists"
            }
            userService.registerUser(user)
            return "redirect:/login"
        } catch (e: Exception) {
            if(e.message!!.contains("Invalid user data"))
                return "redirect:/register?error=empty_fields_error"
            if(e.message!!.contains("Invalid password"))
                return "redirect:/register?error=validation_error"
            return "redirect:/register?error=registration_error"
        }
    }
}