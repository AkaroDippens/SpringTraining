package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.LogEntry
import org.springframework.data.jpa.repository.JpaRepository

interface LogEntryRepository : JpaRepository<LogEntry, Long>
