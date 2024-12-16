package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.ViewUser
import com.example.medicinesystemapi.service.ViewUserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User", description = "Operations related to users")
@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/users")
class ViewUserController(
    private val viewUserService: ViewUserService,
) {
    @GetMapping
    fun getAllViewUsers(): ResponseEntity<List<ViewUser>> {
        val users = viewUserService.findAllViewUsers()
        return if (users.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(users)
        }
    }

    @GetMapping("/{id}")
    fun getViewUserById(
        @PathVariable id: Int,
    ): ResponseEntity<ViewUser?> {
        val user = viewUserService.findViewUserById(id)
        return if (user == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(user)
        }
    }

    @GetMapping("/export-csv")
    fun exportViewUsersToCsv(): ResponseEntity<String> {
        val csvData = viewUserService.exportViewUsersToCsv()
        val headers = HttpHeaders()
        headers.contentType = MediaType.TEXT_PLAIN
        headers.setContentDispositionFormData("userExport", "users.csv")
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvData)
    }
}
