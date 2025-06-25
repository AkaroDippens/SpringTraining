package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.LogEntry
import com.example.medicinesystemapi.service.LoggingService
import com.example.medicinesystemapi.validation.Validations
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/logs")
class LogController(private val loggingService: LoggingService) {

    val validations = Validations()

    @GetMapping
    fun getAllLogs(request: HttpServletRequest): ResponseEntity<List<LogEntry>>{
        if (!validations.hasAnyRole(request, "DB_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return ResponseEntity.ok(loggingService.getAllLogs())
    }

    @DeleteMapping
    fun clearAllLogs(request: HttpServletRequest): ResponseEntity<String> {
        if (!validations.hasAnyRole(request, "DB_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        loggingService.clearAllLogs()
        return ResponseEntity.ok("All logs cleared")
    }

    @GetMapping("/export")
    fun exportLogs(request: HttpServletRequest): ResponseEntity<ByteArray> {
        if (!validations.hasAnyRole(request, "DB_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val csvData = loggingService.exportLogsToCSV()
        return ResponseEntity.ok()
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=logs.csv")
            .body(csvData)
    }

    @PostMapping("/import")
    fun importLogs(
        request: HttpServletRequest,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {
        if (!validations.hasAnyRole(request, "DB_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        try {
            loggingService.importLogsFromCSV(file.inputStream)
            return ResponseEntity.ok("Logs imported successfully")
        } catch (e: IOException) {
            return ResponseEntity.status(500).body("Error importing logs: ${e.message}")
        }
    }
}
