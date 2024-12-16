package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Analyze
import com.example.medicinesystemapi.repository.AnalyzeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AnalyzeServiceImpl(
    private val analyzeRepository: AnalyzeRepository,
) : AnalyzeService {
    override fun findAllAnalyzes(pageable: Pageable): Page<Analyze> {
        return analyzeRepository.findAll(pageable)
    }

    override fun findAllAnalyzesList(): List<Analyze?> {
        return analyzeRepository.findAll()
    }

    override fun findAnalyzeById(id: Long?): Analyze? {
        return analyzeRepository.findById(id ?: 0).orElse(null)
    }

    override fun addAnalyze(analyze: Analyze): Analyze? {
        return analyzeRepository.save(analyze)
    }

    override fun updateAnalyze(
        id: Long,
        analyze: Analyze,
    ): Analyze? {
        return analyzeRepository.save(analyze)
    }

    override fun deleteAnalyze(id: Long) {
        analyzeRepository.deleteById(id)
    }

    override fun deleteMultipleAnalyzes(analyzeIds: List<Long>) {
        analyzeRepository.deleteAllById(analyzeIds)
    }

    override fun logicalDeleteAnalyze(id: Long) {
        val analyze = analyzeRepository.findById(id).orElse(null)
        analyze?.let {
            analyzeRepository.save(it)
        }
    }
}
