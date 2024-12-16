package com.example.medicinesystemapi.controller
import com.example.medicinesystemapi.service.LoggingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/logs")
class LogController(private val loggingService: LoggingService) {

    @GetMapping
    fun getAllLogs() = loggingService.getAllLogs()

    @DeleteMapping
    fun clearAllLogs(): ResponseEntity<String> {
        loggingService.clearAllLogs()
        return ResponseEntity.ok("All logs cleared")
    }

    @GetMapping("/export")
    fun exportLogs(): ResponseEntity<ByteArray> {
        val csvData = loggingService.exportLogsToCSV()
        return ResponseEntity.ok()
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=logs.csv")
            .body(csvData)
    }

    @PostMapping("/import")
    fun importLogs(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        try {
            loggingService.importLogsFromCSV(file.inputStream)
            return ResponseEntity.ok("Logs imported successfully")
        } catch (e: IOException) {
            return ResponseEntity.status(500).body("Error importing logs: ${e.message}")
        }
    }
}