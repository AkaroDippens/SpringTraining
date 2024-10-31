package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Record
import org.springframework.data.jpa.repository.JpaRepository

interface RecordRepository : JpaRepository<Record, Long>