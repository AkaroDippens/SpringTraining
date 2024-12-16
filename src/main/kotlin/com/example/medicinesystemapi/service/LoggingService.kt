package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.LogEntry
import com.example.medicinesystemapi.repository.LogEntryRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Service
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class LoggingService(private val logEntryRepository: LogEntryRepository) {

    fun logRequest(logEntry: LogEntry) {
        logEntryRepository.save(logEntry)
    }

    fun getAllLogs(): List<LogEntry> {
        return logEntryRepository.findAll()
    }

    fun clearAllLogs() {
        logEntryRepository.deleteAll()
    }

    fun exportLogsToCSV(): ByteArray {
        val logs = getAllLogs()
        val outputStream = ByteArrayOutputStream()
        val writer = PrintWriter(outputStream)

        // Write CSV header
        writer.println("Timestamp,Method,URL,Status,UserAgent,IPAddress")

        // Write each log entry
        logs.forEach { log ->
            writer.println("${log.timestamp},${log.method},${log.url},${log.status},${log.userAgent},${log.ipAddress}")
        }

        writer.flush()
        return outputStream.toByteArray()
    }

    fun importLogsFromCSV(inputStream: InputStream) {
        try {
            val csvFormat = CSVFormat.DEFAULT
                .withHeader("Timestamp", "Method", "URL", "Status", "UserAgent", "IPAddress")
                .withFirstRecordAsHeader()
                .withTrim()

            CSVParser(InputStreamReader(inputStream), csvFormat).use { parser ->
                for (record in parser) {
                    try {
                        val timestamp = LocalDateTime.parse(
                            record.get("Timestamp"),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        )

                        val logEntry = LogEntry().apply {
                            this.timestamp = timestamp
                            method = record.get("Method")
                            url = record.get("URL")
                            status = record.get("Status").toInt()
                            userAgent = record.get("UserAgent")
                            ipAddress = record.get("IPAddress")
                        }

                        logEntryRepository.save(logEntry)
                    } catch (e: DateTimeParseException) {
                        println("Error parsing date for record: ${record.get("Timestamp")}")
                        e.printStackTrace()
                    } catch (e: Exception) {
                        println("Error processing record: ${record.toMap()}")
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            println("Error parsing CSV file")
            e.printStackTrace()
            throw e
        }
    }
}