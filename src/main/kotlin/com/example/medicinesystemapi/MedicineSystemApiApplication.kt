package com.example.medicinesystemapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MedicineSystemApiApplication

fun main(args: Array<String>) {
    runApplication<MedicineSystemApiApplication>(*args)
}
