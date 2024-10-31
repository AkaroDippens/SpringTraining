package com.example.medicinesystemapi.repository

import com.example.medicinesystemapi.model.Analyze
import org.springframework.data.jpa.repository.JpaRepository

interface AnalyzeRepository : JpaRepository<Analyze, Long>