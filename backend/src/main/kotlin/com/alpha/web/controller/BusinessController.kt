package com.alpha.web.controller

import com.alpha.service.BusinessService
import com.alpha.service.SecurityContextService
import com.alpha.service.dto.*
import com.alpha.web.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/businesses")
@Tag(name = "Businesses", description = "Business management endpoints")
class BusinessController(
    private val businessService: BusinessService,
    private val securityContextService: SecurityContextService
) {
    
    @PostMapping
    @Operation(summary = "Create a new business")
    fun createBusiness(
        @Valid @RequestBody request: CreateBusinessRequest
    ): ResponseEntity<ApiResponse<BusinessDto>> {
        val business = businessService.createBusiness(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(business))
    }
    
    @PutMapping("/{businessId}")
    @Operation(summary = "Update business")
    fun updateBusiness(
        @PathVariable businessId: UUID,
        @Valid @RequestBody request: UpdateBusinessRequest
    ): ResponseEntity<ApiResponse<BusinessDto>> {
        val business = businessService.updateBusiness(businessId, request)
        return ResponseEntity.ok(ApiResponse.success(business))
    }
    
    @GetMapping("/{businessId}")
    @Operation(summary = "Get business by ID")
    fun getBusiness(
        @PathVariable businessId: UUID
    ): ResponseEntity<ApiResponse<BusinessDto>> {
        val business = businessService.getBusiness(businessId)
        return ResponseEntity.ok(ApiResponse.success(business))
    }
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get business by slug")
    fun getBusinessBySlug(
        @PathVariable slug: String
    ): ResponseEntity<ApiResponse<BusinessDto>> {
        val business = businessService.getBusinessBySlug(slug)
        return ResponseEntity.ok(ApiResponse.success(business))
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search businesses")
    fun searchBusinesses(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) regionId: UUID?,
        @RequestParam(required = false) categoryId: UUID?,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BusinessDto>>> {
        val result = businessService.searchBusinesses(query, regionId, categoryId, pageable)
        return ResponseEntity.ok(ApiResponse.success(result))
    }
    
    @GetMapping("/my-businesses")
    @Operation(summary = "Get current user's businesses")
    fun getUserBusinesses(
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BusinessDto>>> {
        val userId = securityContextService.getCurrentUserId()
        val businesses = businessService.getUserBusinesses(userId, pageable)
        return ResponseEntity.ok(ApiResponse.success(businesses))
    }
    
    @GetMapping("/region/{regionId}")
    @Operation(summary = "Get businesses by region")
    fun getBusinessesByRegion(
        @PathVariable regionId: UUID,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BusinessDto>>> {
        val businesses = businessService.getBusinessesByRegion(regionId, pageable)
        return ResponseEntity.ok(ApiResponse.success(businesses))
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get businesses by category")
    fun getBusinessesByCategory(
        @PathVariable categoryId: UUID,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BusinessDto>>> {
        val businesses = businessService.getBusinessesByCategory(categoryId, pageable)
        return ResponseEntity.ok(ApiResponse.success(businesses))
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Get featured businesses")
    fun getFeaturedBusinesses(
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<BusinessDto>>> {
        val businesses = businessService.getFeaturedBusinesses(pageable)
        return ResponseEntity.ok(ApiResponse.success(businesses))
    }
    
    @PostMapping("/{businessId}/verify")
    @Operation(summary = "Verify business (admin only)")
    fun verifyBusiness(
        @PathVariable businessId: UUID
    ): ResponseEntity<ApiResponse<BusinessDto>> {
        val business = businessService.verifyBusiness(businessId)
        return ResponseEntity.ok(ApiResponse.success(business))
    }
    
    @DeleteMapping("/{businessId}")
    @Operation(summary = "Delete business")
    fun deleteBusiness(
        @PathVariable businessId: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        businessService.deleteBusiness(businessId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}