package com.example.medicinesystemapi.controller

import com.example.medicinesystemapi.model.Building
import com.example.medicinesystemapi.service.BuildingService
import com.example.medicinesystemapi.service.MetricApiRequestTimerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/buildings")
class BuildingController(
    private val buildingService: BuildingService,
    private val metricApiRequestTimerService: MetricApiRequestTimerService
) {

    @GetMapping("/error")
    fun triggerError(): ResponseEntity<String> {
        throw RuntimeException("Simulated 500 error")
    }

    @GetMapping
    fun getAllBuildings(): ResponseEntity<List<Building?>> {
        return metricApiRequestTimerService.recordApiRequest {
            val buildings = buildingService.findAllBuildingsList()
            if (buildings.isEmpty()) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.ok(buildings)
            }
        }!!
    }

    @GetMapping("/{id}")
    fun getBuildingById(
        @PathVariable id: Long,
    ): ResponseEntity<Building?> {
        return metricApiRequestTimerService.recordApiRequest {
            val building = buildingService.findBuildingById(id)
            if (building == null) {
                ResponseEntity.notFound().build()
            } else {
                ResponseEntity.ok(building)
            }
        }!!
    }

    @GetMapping("/byname/{buildingName}")
    fun getBuildingByName(
        @PathVariable buildingName: String,
    ): ResponseEntity<List<Building>> {
        return metricApiRequestTimerService.recordApiRequest {
            val buildings = buildingService.findBuildingByName(buildingName)
            if (buildings.isEmpty()) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.ok(buildings)
            }
        }!!
    }

    @GetMapping("/byaddress/{address}")
    fun getBuildingByAddress(
        @PathVariable address: String,
    ): ResponseEntity<List<Building>> {
        return metricApiRequestTimerService.recordApiRequest {
            val buildings = buildingService.findBuildingByAddress(address)
            if (buildings.isEmpty()) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.ok(buildings)
            }
        }!!
    }

    @PostMapping
    fun addBuilding(
        @RequestBody building: Building,
    ): ResponseEntity<Building?> {
        return metricApiRequestTimerService.recordApiRequest {
            if (building.id != null) {
                ResponseEntity.badRequest().build()
            } else if (building.buildingName == null || building.buildingName.isNullOrEmpty()) {
                ResponseEntity.badRequest().build()
            } else {
                val savedBuilding = buildingService.addBuilding(building)
                if (savedBuilding == null) {
                    ResponseEntity.internalServerError().build()
                } else {
                    ResponseEntity.status(HttpStatus.CREATED).body(savedBuilding)
                }
            }
        }!!
    }

    @PutMapping("/{id}")
    fun updateBuilding(
        @PathVariable id: Long,
        @RequestBody building: Building,
    ): ResponseEntity<Building?> {
        return metricApiRequestTimerService.recordApiRequest {
            if (building.id == null) {
                ResponseEntity.badRequest().build()
            } else {
                buildingService.findBuildingById(id) ?: return@recordApiRequest ResponseEntity.notFound().build()
                val updatedBuilding = buildingService.updateBuilding(id, building)
                if (updatedBuilding == null) {
                    ResponseEntity.internalServerError().build()
                } else {
                    ResponseEntity.ok(updatedBuilding)
                }
            }
        }!!
    }

    @DeleteMapping("/{id}")
    fun deleteBuilding(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        return metricApiRequestTimerService.recordApiRequest {
            buildingService.findBuildingById(id) ?: return@recordApiRequest ResponseEntity.notFound().build()
            buildingService.deleteBuilding(id)
            ResponseEntity.noContent().build()
        }!!
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleBuildings(
        @RequestBody buildingIds: List<Long>,
    ): ResponseEntity<Void> {
        return metricApiRequestTimerService.recordApiRequest {
            if (buildingIds.isEmpty()) {
                ResponseEntity.badRequest().build()
            } else {
                buildingService.deleteMultipleBuildings(buildingIds)
                ResponseEntity.noContent().build()
            }
        }!!
    }
}