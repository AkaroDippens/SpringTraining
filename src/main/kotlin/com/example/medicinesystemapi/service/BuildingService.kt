package com.example.medicinesystemapi.service

import com.example.medicinesystemapi.model.Building
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BuildingService {
    fun findAllBuildings(pageable: Pageable): Page<Building>
    fun findAllBuildingsList(): List<Building?>
    fun findBuildingById(id: Long?): Building?
    fun findBuildingByName(buildingName: String?): List<Building>
    fun addBuilding(building: Building): Building?
    fun updateBuilding(id: Long, building: Building): Building?
    fun deleteBuilding(id: Long)
    fun deleteMultipleBuildings(buildingIds: List<Long>)
    fun logicalDeleteBuilding(id: Long)
    fun findBuildingByAddress(address: String?): List<Building>
}