package com.example.medicinesystemapi

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:.env")
class MedicineSystemApiApplication {

    private val dotenv = Dotenv.configure().load()
    private val token = dotenv["INFLUX_TOKEN"]
    private val org = dotenv["INFLUX_ORG"]
    private val bucket = dotenv["INFLUX_BUCKET"]

    @Bean
    fun influxDBClient(): InfluxDBClient {
        return InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray(), org, bucket)
    }
}

fun main(args: Array<String>) {
    runApplication<MedicineSystemApiApplication>(*args)
}

