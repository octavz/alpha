package com.alpha.service

import com.alpha.domain.entity.RegionEntity
import com.alpha.domain.repository.RegionRepository
import com.alpha.service.dto.*
import com.alpha.service.exception.ConflictException
import com.alpha.service.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class RegionService(
    private val regionRepository: RegionRepository
) {
    
    fun getAllRegions(): List<RegionResponse> {
        val regions = regionRepository.findByIsActiveTrue()
        return regions.map { mapToRegionResponse(it) }
    }
    
    fun getRegion(regionId: UUID): RegionResponse {
        val region = regionRepository.findById(regionId)
            .orElseThrow { NotFoundException("Region not found") }
        
        return mapToRegionResponse(region)
    }
    
    fun getRegionByCode(code: String): RegionResponse {
        val region = regionRepository.findByCode(code)
            ?: throw NotFoundException("Region not found")
        
        return mapToRegionResponse(region)
    }
    
    fun createRegion(request: CreateRegionRequest): RegionResponse {
        // Check if code already exists
        if (regionRepository.existsByCode(request.code)) {
            throw ConflictException("Region with this code already exists")
        }
        
        val region = RegionEntity().apply {
            name = request.name
            code = request.code
            country = request.country
            timezone = request.timezone
            isActive = request.isActive
        }
        
        val savedRegion = regionRepository.save(region)
        return mapToRegionResponse(savedRegion)
    }
    
    fun updateRegion(regionId: UUID, request: UpdateRegionRequest): RegionResponse {
        val region = regionRepository.findById(regionId)
            .orElseThrow { NotFoundException("Region not found") }
        
        request.name?.let { region.name = it }
        request.code?.let { 
            // Check if new code already exists (if different from current)
            if (it != region.code && regionRepository.existsByCode(it)) {
                throw ConflictException("Region with this code already exists")
            }
            region.code = it
        }
        request.country?.let { region.country = it }
        request.timezone?.let { region.timezone = it }
        request.isActive?.let { region.isActive = it }
        
        val updatedRegion = regionRepository.save(region)
        return mapToRegionResponse(updatedRegion)
    }
    
    fun deleteRegion(regionId: UUID) {
        val region = regionRepository.findById(regionId)
            .orElseThrow { NotFoundException("Region not found") }
        
        // Soft delete by marking as inactive
        region.isActive = false
        regionRepository.save(region)
    }
    
    fun searchRegions(search: String): List<RegionResponse> {
        val regions = regionRepository.search(search)
        return regions.map { mapToRegionResponse(it) }
    }
    
    private fun mapToRegionResponse(region: RegionEntity): RegionResponse {
        return RegionResponse(
            id = region.requiredId,
            name = region.name,
            code = region.code,
            country = region.country,
            timezone = region.timezone,
            isActive = region.isActive,
            createdAt = region.createdAt!!
        )
    }
}