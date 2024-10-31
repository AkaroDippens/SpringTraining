package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Analyze
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AnalyzeService {
    fun findAllAnalyzes(pageable: Pageable): Page<Analyze>
    fun findAllAnalyzesList(): List<Analyze?>
    fun findAnalyzeById(id: Long?): Analyze?
    fun addAnalyze(analyze: Analyze): Analyze?
    fun updateAnalyze(id: Long, analyze: Analyze): Analyze?
    fun deleteAnalyze(id: Long)
    fun deleteMultipleAnalyzes(analyzeIds: List<Long>)
    fun logicalDeleteAnalyze(id: Long)
}