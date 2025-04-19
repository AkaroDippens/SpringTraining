package com.example.medicinesystemapi.service

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class MetricApiRequestTimerService(
    meterRegistry: MeterRegistry,
    private val influxDBClient: InfluxDBClient
) {

    private val apiRequestTimer: Timer = Timer.builder("api_request_duration_seconds")
        .description("Время обработки запросов API")
        .register(meterRegistry)

    fun <T> recordApiRequest(block: () -> T): T {
        return apiRequestTimer.recordCallable {
            val result = block()
            // Запись метрики в InfluxDB
            val point = Point
                .measurement("api_request_duration")
                .addField("duration", apiRequestTimer.totalTime(TimeUnit.MILLISECONDS))
                .time(Instant.now(), WritePrecision.NS)
            influxDBClient.writeApiBlocking.writePoint(point)
            result
        }!!
    }
}