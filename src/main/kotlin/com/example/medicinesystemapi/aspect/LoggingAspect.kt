package com.example.medicinesystemapi.aspect

import com.example.medicinesystemapi.model.LogEntry
import com.example.medicinesystemapi.service.LoggingService
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant

@Aspect
@Component
class LoggingAspect(
    private val loggingService: LoggingService,
    private val meterRegistry: MeterRegistry,
    private val influxDBClient: InfluxDBClient
) {

    private val errorCounter: Counter = Counter.builder("log_error_count")
        .description("Количество ошибок в логах приложения")
        .register(meterRegistry)

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    fun restController() {}

    @Around("restController()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val logEntry =
            LogEntry().apply {
                method = request.method
                url = request.requestURI
                userAgent = request.getHeader("User-Agent")
                ipAddress = request.remoteAddr
            }

        return try {
            val result = joinPoint.proceed()
            logEntry.status = 200
            loggingService.logRequest(logEntry)
            writeToInfluxDB(logEntry)
            result
        } catch (e: Exception) {
            logEntry.status = 500
            loggingService.logRequest(logEntry)
            errorCounter.increment()
            writeToInfluxDB(logEntry)
            throw e
        }
    }

    private fun writeToInfluxDB(logEntry: LogEntry) {
        val point = Point.measurement("api_logs")
            .addTag("method", logEntry.method)
            .addTag("url", logEntry.url)
            .addTag("user_agent", logEntry.userAgent ?: "unknown")
            .addTag("ip_address", logEntry.ipAddress)
            .addTag("status", logEntry.status.toString())
            .time(Instant.now(), WritePrecision.NS)

        influxDBClient.writeApiBlocking.writePoint(point)
    }
}
