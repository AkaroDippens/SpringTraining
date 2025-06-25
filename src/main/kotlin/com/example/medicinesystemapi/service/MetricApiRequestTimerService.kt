package com.example.medicinesystemapi.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Service

@Service
class MetricApiRequestTimerService(
    meterRegistry: MeterRegistry
) {

    private val apiRequestTimer: Timer = Timer.builder("api_request_duration_seconds")
        .description("Время обработки запросов API")
        .register(meterRegistry)

    fun <T> recordApiRequest(block: () -> T): T {
        return apiRequestTimer.recordCallable { block() }!!
    }
}