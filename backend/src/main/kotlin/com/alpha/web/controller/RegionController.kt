package com.alpha.web.controller

import com.alpha.service.RegionService
import com.alpha.service.dto.*
import com.alpha.web.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/regions")
@Tag(name = "Regions", description = "Region management endpoints")
class RegionController(
    private val regionService: RegionService
) {
    
    @GetMapping
    @Operation(summary = "Get all active regions")
    fun getAllRegions(): ResponseEntity<ApiResponse<List<RegionResponse>>> {
        val regions = regionService.getAllRegions()
        return ResponseEntity.ok(ApiResponse.success(regions))
    }
    
    @GetMapping("/{regionId}")
    @Operation(summary = "Get region by ID")
    fun getRegion(
        @PathVariable regionId: UUID
    ): ResponseEntity<ApiResponse<RegionResponse>> {
        val region = regionService.getRegion(regionId)
        return ResponseEntity.ok(ApiResponse.success(region))
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get region by code")
    fun getRegionByCode(
        @PathVariable code: String
    ): ResponseEntity<ApiResponse<RegionResponse>> {
        val region = regionService.getRegionByCode(code)
        return ResponseEntity.ok(ApiResponse.success(region))
    }
    
    @PostMapping
    @Operation(summary = "Create new region (admin only)")
    fun createRegion(
        @Valid @RequestBody request: CreateRegionRequest
    ): ResponseEntity<ApiResponse<RegionResponse>> {
        val region = regionService.createRegion(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(region))
    }
    
    @PutMapping("/{regionId}")
    @Operation(summary = "Update region (admin only)")
    fun updateRegion(
        @PathVariable regionId: UUID,
        @Valid @RequestBody request: UpdateRegionRequest
    ): ResponseEntity<ApiResponse<RegionResponse>> {
        val region = regionService.updateRegion(regionId, request)
        return ResponseEntity.ok(ApiResponse.success(region))
    }
    
    @DeleteMapping("/{regionId}")
    @Operation(summary = "Delete region (admin only)")
    fun deleteRegion(
        @PathVariable regionId: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        regionService.deleteRegion(regionId)
        return ResponseEntity.ok(ApiResponse.success())
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search regions")
    fun searchRegions(
        @RequestParam search: String
    ): ResponseEntity<ApiResponse<List<RegionResponse>>> {
        val regions = regionService.searchRegions(search)
        return ResponseEntity.ok(ApiResponse.success(regions))
    }
}