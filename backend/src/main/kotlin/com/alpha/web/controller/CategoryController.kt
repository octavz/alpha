package com.alpha.web.controller

import com.alpha.service.CategoryService
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management endpoints")
class CategoryController(
    private val categoryService: CategoryService
) {
    
    @GetMapping
    @Operation(summary = "Get all active categories")
    fun getAllCategories(): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        val categories = categoryService.getAllCategories()
        return ResponseEntity.ok(ApiResponse.success(categories))
    }
    
    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID")
    fun getCategory(
        @PathVariable categoryId: UUID
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        val category = categoryService.getCategory(categoryId)
        return ResponseEntity.ok(ApiResponse.success(category))
    }
    
    @PostMapping
    @Operation(summary = "Create new category (admin only)")
    fun createCategory(
        @Valid @RequestBody request: CreateCategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        val category = categoryService.createCategory(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(category))
    }
    
    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category (admin only)")
    fun updateCategory(
        @PathVariable categoryId: UUID,
        @Valid @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        val category = categoryService.updateCategory(categoryId, request)
        return ResponseEntity.ok(ApiResponse.success(category))
    }
    
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category (admin only)")
    fun deleteCategory(
        @PathVariable categoryId: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        categoryService.deleteCategory(categoryId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}
