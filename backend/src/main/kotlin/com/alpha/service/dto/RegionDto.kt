package com.alpha.service.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.*

data class RegionResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val country: String,
    val timezone: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime
)

data class CreateRegionRequest(
    @field:NotBlank(message = "Region name is required")
    @field:Size(min = 2, max = 100, message = "Region name must be between 2 and 100 characters")
    val name: String,
    
    @field:NotBlank(message = "Region code is required")
    @field:Size(min = 2, max = 10, message = "Region code must be between 2 and 10 characters")
    val code: String,
    
    @field:NotBlank(message = "Country is required")
    val country: String,
    
    @field:NotBlank(message = "Timezone is required")
    val timezone: String,
    
    val isActive: Boolean = true
)

data class UpdateRegionRequest(
    @field:Size(min = 2, max = 100, message = "Region name must be between 2 and 100 characters")
    val name: String? = null,
    
    @field:Size(min = 2, max = 10, message = "Region code must be between 2 and 10 characters")
    val code: String? = null,
    
    val country: String? = null,
    val timezone: String? = null,
    val isActive: Boolean? = null
)