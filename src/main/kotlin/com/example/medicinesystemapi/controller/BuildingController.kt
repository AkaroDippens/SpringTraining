package com.example.medicinesystemapi.controller


import com.example.medicinesystemapi.model.Building;
import com.example.medicinesystemapi.service.BuildingService;
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/buildings")
class BuildingController(private val buildingService: BuildingService) {

    @GetMapping
    fun getAllBuildings(): ResponseEntity<List<Building?>> {
        val buildings = buildingService.findAllBuildingsList()
        return if (buildings.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(buildings)
        }
    }

    @GetMapping("/{id}")
    fun getBuildingById(@PathVariable id: Long): ResponseEntity<Building?> {
        val building = buildingService.findBuildingById(id)
        return if (building == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(building)
        }
    }

    @GetMapping("/byname/{buildingName}")
    fun getBuildingByName(@PathVariable buildingName: String): ResponseEntity<List<Building>> {
        val buildings = buildingService.findBuildingByName(buildingName)
        return if (buildings.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(buildings)
        }
    }

    @GetMapping("/byaddress/{address}")
    fun getBuildingByAddress(@PathVariable address: String): ResponseEntity<List<Building>> {
        val buildings = buildingService.findBuildingByAddress(address)
        return if (buildings.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(buildings)
        }
    }

    @PostMapping
    fun addBuilding(@RequestBody building: Building): ResponseEntity<Building?> {
        if (building.id != null) {
            return ResponseEntity.badRequest().build()
        }
        if (building.buildingName == null || building.buildingName.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        val savedBuilding = buildingService.addBuilding(building)
        return if (savedBuilding == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(savedBuilding)
        }
    }

    @PutMapping("/{id}")
    fun updateBuilding(@PathVariable id: Long, @RequestBody building: Building): ResponseEntity<Building?> {
        if (building.id == null) {
            return ResponseEntity.badRequest().build()
        }
        buildingService.findBuildingById(id) ?: return ResponseEntity.notFound().build()
        val updatedBuilding = buildingService.updateBuilding(id, building)
        return if (updatedBuilding == null) {
            ResponseEntity.internalServerError().build()
        } else {
            ResponseEntity.ok(updatedBuilding)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteBuilding(@PathVariable id: Long): ResponseEntity<Void> {
        buildingService.findBuildingById(id) ?: return ResponseEntity.notFound().build()
        buildingService.deleteBuilding(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/multiple")
    fun deleteMultipleBuildings(@RequestBody buildingIds: List<Long>): ResponseEntity<Void> {
        if (buildingIds.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        buildingService.deleteMultipleBuildings(buildingIds)
        return ResponseEntity.noContent().build()
    }
}