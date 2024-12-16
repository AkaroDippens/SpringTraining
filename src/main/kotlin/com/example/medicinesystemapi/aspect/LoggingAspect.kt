package com.example.medicinesystemapi.aspect

import com.example.medicinesystemapi.model.LogEntry
import com.example.medicinesystemapi.service.LoggingService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class LoggingAspect(private val loggingService: LoggingService) {
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
            result
        } catch (e: Exception) {
            logEntry.status = 500
            loggingService.logRequest(logEntry)
            throw e
        }
    }
}
