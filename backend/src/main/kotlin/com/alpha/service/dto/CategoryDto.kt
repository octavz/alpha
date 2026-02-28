package com.alpha.service.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.*

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val slug: String,
    val description: String?,
    val icon: String?,
    val parentId: UUID?,
    val sortOrder: Int,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val children: List<CategoryResponse> = emptyList()
)

data class CreateCategoryRequest(
    @field:NotBlank(message = "Category name is required")
    @field:Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    val name: String,
    
    @field:Size(max = 500, message = "Description cannot exceed 500 characters")
    val description: String? = null,
    
    val icon: String? = null,
    val parentId: UUID? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)

data class UpdateCategoryRequest(
    @field:Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    val name: String? = null,
    
    @field:Size(max = 500, message = "Description cannot exceed 500 characters")
    val description: String? = null,
    
    val icon: String? = null,
    val parentId: UUID? = null,
    val sortOrder: Int? = null,
    val isActive: Boolean? = null
)