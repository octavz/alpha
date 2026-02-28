package com.alpha.service.dto

import com.alpha.domain.enums.VerificationStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class CreateBusinessRequest(
    @field:NotBlank(message = "Business name is required")
    @field:Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    val name: String,
    
    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,
    
    @field:NotNull(message = "Category ID is required")
    val categoryId: UUID,
    
    @field:NotNull(message = "Region ID is required")
    val regionId: UUID,
    
    // Contact Information
    val email: String? = null,
    val phone: String? = null,
    val website: String? = null,
    
    // Address
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    
    // Business Details
    val logoUrl: String? = null,
    val coverImageUrl: String? = null,
    
    @field:PositiveOrZero(message = "Service points count must be zero or positive")
    val servicePointsCount: Int = 1
)

data class UpdateBusinessRequest(
    @field:Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    val name: String? = null,
    
    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,
    
    val categoryId: UUID? = null,
    val regionId: UUID? = null,
    
    // Contact Information
    val email: String? = null,
    val phone: String? = null,
    val website: String? = null,
    
    // Address
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    
    // Business Details
    val logoUrl: String? = null,
    val coverImageUrl: String? = null,
    val isActive: Boolean? = null,
    val isFeatured: Boolean? = null,
    val verificationStatus: VerificationStatus? = null,
    
    @field:PositiveOrZero(message = "Service points count must be zero or positive")
    val servicePointsCount: Int? = null
)

data class BusinessResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val slug: String,
    val description: String?,
    val categoryId: UUID?,
    val regionId: UUID,
    
    // Contact Information
    val email: String?,
    val phone: String?,
    val website: String?,
    
    // Address
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    
    // Business Details
    val logoUrl: String?,
    val coverImageUrl: String?,
    val isVerified: Boolean,
    val isActive: Boolean,
    val isFeatured: Boolean,
    val verificationStatus: VerificationStatus,
    val servicePointsCount: Int,
    
    // Metadata
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class BusinessSearchRequest(
    val search: String? = null,
    val regionId: UUID? = null,
    val categoryId: UUID? = null,
    val isVerified: Boolean? = true,
    val isActive: Boolean? = true,
    val isFeatured: Boolean? = null,
    val verificationStatus: VerificationStatus? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "createdAt",
    val sortDirection: String = "desc"
)

data class BusinessSearchResponse(
    val businesses: List<BusinessResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)

data class NearbyBusinessRequest(
    @field:NotNull(message = "Latitude is required")
    val latitude: BigDecimal,
    
    @field:NotNull(message = "Longitude is required")
    val longitude: BigDecimal,
    
    @field:PositiveOrZero(message = "Radius must be positive")
    val radius: Double = 10.0, // in kilometers
    
    val page: Int = 0,
    val size: Int = 20
)