package com.example.medicinesystemapi

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:.env")
class MedicineSystemApiApplication

fun main(args: Array<String>) {
    runApplication<MedicineSystemApiApplication>(*args)
}

