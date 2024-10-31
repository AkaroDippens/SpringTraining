package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Building
import com.example.medicinesystemapi.repository.BuildingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BuildingServiceImpl(
    private val buildingRepository: BuildingRepository
) : BuildingService {

    override fun findAllBuildings(pageable: Pageable): Page<Building> {
        return buildingRepository.findAll(pageable)
    }

    override fun findAllBuildingsList(): List<Building?> {
        return buildingRepository.findAll()
    }

    override fun findBuildingById(id: Long?): Building? {
        return buildingRepository.findById(id ?: 0).orElse(null)
    }

    override fun findBuildingByName(buildingName: String?): List<Building> {
        return buildingRepository.findAll().filter { it.buildingName == buildingName }
    }

    override fun addBuilding(building: Building): Building? {
        return buildingRepository.save(building)
    }

    override fun updateBuilding(id: Long, building: Building): Building? {
        return buildingRepository.save(building)
    }

    override fun deleteBuilding(id: Long) {
        buildingRepository.deleteById(id)
    }

    override fun deleteMultipleBuildings(buildingIds: List<Long>) {
        buildingRepository.deleteAllById(buildingIds)
    }

    override fun logicalDeleteBuilding(id: Long) {
        val building = buildingRepository.findById(id).orElse(null)
        building?.let {
            buildingRepository.save(it)
        }
    }

    override fun findBuildingByAddress(address: String?): List<Building> {
        return buildingRepository.findAll().filter { it.address == address }
    }
}