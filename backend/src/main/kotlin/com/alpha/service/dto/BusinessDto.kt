package com.alpha.service.dto

import com.alpha.domain.enums.VerificationStatus
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class CreateBusinessRequest(
    @field:NotBlank(message = "Business name is required")
    @field:Size(min = 2, max = 255, message = "Business name must be between 2 and 255 characters")
    val name: String,
    
    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,
    
    @field:NotNull(message = "Category ID is required")
    val categoryId: UUID,
    
    @field:NotNull(message = "Region ID is required")
    val regionId: UUID,
    
    @field:Email(message = "Invalid email format")
    @field:Size(max = 255, message = "Email cannot exceed 255 characters")
    val email: String? = null,
    
    @field:Size(max = 20, message = "Phone cannot exceed 20 characters")
    val phone: String? = null,
    
    @field:Size(max = 500, message = "Website cannot exceed 500 characters")
    val website: String? = null,
    
    @field:Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
    val addressLine1: String? = null,
    
    @field:Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    val addressLine2: String? = null,
    
    @field:Size(max = 100, message = "City cannot exceed 100 characters")
    val city: String? = null,
    
    @field:Size(max = 100, message = "State cannot exceed 100 characters")
    val state: String? = null,
    
    @field:Size(max = 20, message = "Zip code cannot exceed 20 characters")
    val zipCode: String? = null,
    
    @field:Size(max = 100, message = "Country cannot exceed 100 characters")
    val country: String? = null,
    
    @field:DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    val latitude: BigDecimal? = null,
    
    @field:DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    val longitude: BigDecimal? = null,
    
    @field:Size(max = 500, message = "Logo URL cannot exceed 500 characters")
    val logoUrl: String? = null,
    
    @field:Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    val coverImageUrl: String? = null,
    
    @field:PositiveOrZero(message = "Service points count must be zero or positive")
    @field:Max(value = 1000, message = "Service points count cannot exceed 1000")
    val servicePointsCount: Int = 1
)

data class UpdateBusinessRequest(
    @field:Size(min = 2, max = 255, message = "Business name must be between 2 and 255 characters")
    val name: String? = null,
    
    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,
    
    val categoryId: UUID? = null,
    val regionId: UUID? = null,
    
    @field:Email(message = "Invalid email format")
    @field:Size(max = 255, message = "Email cannot exceed 255 characters")
    val email: String? = null,
    
    @field:Size(max = 20, message = "Phone cannot exceed 20 characters")
    val phone: String? = null,
    
    @field:Size(max = 500, message = "Website cannot exceed 500 characters")
    val website: String? = null,
    
    @field:Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
    val addressLine1: String? = null,
    
    @field:Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    val addressLine2: String? = null,
    
    @field:Size(max = 100, message = "City cannot exceed 100 characters")
    val city: String? = null,
    
    @field:Size(max = 100, message = "State cannot exceed 100 characters")
    val state: String? = null,
    
    @field:Size(max = 20, message = "Zip code cannot exceed 20 characters")
    val zipCode: String? = null,
    
    @field:Size(max = 100, message = "Country cannot exceed 100 characters")
    val country: String? = null,
    
    @field:DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    val latitude: BigDecimal? = null,
    
    @field:DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    val longitude: BigDecimal? = null,
    
    @field:Size(max = 500, message = "Logo URL cannot exceed 500 characters")
    val logoUrl: String? = null,
    
    @field:Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    val coverImageUrl: String? = null,
    
    val isActive: Boolean? = null,
    val isFeatured: Boolean? = null,
    val verificationStatus: VerificationStatus? = null,
    
    @field:PositiveOrZero(message = "Service points count must be zero or positive")
    @field:Max(value = 1000, message = "Service points count cannot exceed 1000")
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