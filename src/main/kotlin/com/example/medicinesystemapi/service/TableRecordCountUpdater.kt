package com.example.medicinesystemapi.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TableRecordCountUpdater(private val metricTableRecordCountService: MetricTableRecordCountService) {

    @Scheduled(fixedRate = 30000)
    fun updateTableRecordCounts() {
        metricTableRecordCountService.updateTableRecordCounts()
    }
}