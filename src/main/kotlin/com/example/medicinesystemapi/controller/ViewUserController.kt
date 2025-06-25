package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.ViewUser
import com.example.medicinesystemapi.service.ViewUserService
import com.example.medicinesystemapi.validation.Validations
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User", description = "Operations related to users")
@RestController
@RequestMapping("/api/users")
class ViewUserController(
    private val viewUserService: ViewUserService,
) {

    val validations = Validations()

    @GetMapping
    fun getAllViewUsers(request: HttpServletRequest): ResponseEntity<List<ViewUser>> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val users = viewUserService.findAllViewUsers()
        return if (users.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(users)
        }
    }

    @GetMapping("/{id}")
    fun getViewUserById(
        request: HttpServletRequest,
        @PathVariable id: Int,
    ): ResponseEntity<ViewUser?> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val user = viewUserService.findViewUserById(id)
        return if (user == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(user)
        }
    }

    @GetMapping("/export-csv")
    fun exportViewUsersToCsv(request: HttpServletRequest): ResponseEntity<String> {
        if (!validations.hasAnyRole(request, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val csvData = viewUserService.exportViewUsersToCsv()
        val headers = HttpHeaders()
        headers.contentType = MediaType.TEXT_PLAIN
        headers.setContentDispositionFormData("userExport", "users.csv")
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvData)
    }
}
