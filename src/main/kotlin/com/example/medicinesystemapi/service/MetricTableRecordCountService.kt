package com.example.medicinesystemapi.service

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

@Service
class MetricTableRecordCountService(
    private val meterRegistry: MeterRegistry,
    private val dataSource: DataSource
) {

    private val tableCounts = mutableMapOf<String, AtomicInteger>()

    init {
        updateTableRecordCounts()
    }

    fun updateTableRecordCounts() {
        val tableNames = getTableNames()
        for (tableName in tableNames) {
            val count = getRecordCount(tableName)
            val atomicCount = tableCounts.getOrPut(tableName) { AtomicInteger(0) }
            atomicCount.set(count)

            Gauge.builder("table_record_count", atomicCount) { it.get().toDouble() }
                .description("Количество записей в таблице")
                .tag("table", tableName)
                .register(meterRegistry)
        }
    }

    private fun getTableNames(): List<String> {
        val tableNames = mutableListOf<String>()
        dataSource.connection.use { connection ->
            val metaData = connection.metaData
            val resultSet = metaData.getTables(null, null, "%", arrayOf("TABLE"))
            while (resultSet.next()) {
                tableNames.add(resultSet.getString("TABLE_NAME"))
            }
        }
        return tableNames
    }

    private fun getRecordCount(tableName: String): Int {
        dataSource.connection.use { connection ->
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT COUNT(*) FROM $tableName")
            if (resultSet.next()) {
                return resultSet.getInt(1)
            }
        }
        return 0
    }
}