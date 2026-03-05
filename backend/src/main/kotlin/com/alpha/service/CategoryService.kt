package com.alpha.service

import com.alpha.domain.entity.CategoryEntity
import com.alpha.domain.repository.CategoryRepository
import com.alpha.service.dto.CategoryResponse
import com.alpha.service.dto.CreateCategoryRequest
import com.alpha.service.dto.UpdateCategoryRequest
import com.alpha.service.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    
    fun getAllCategories(): List<CategoryResponse> {
        return categoryRepository.findRootCategories()
            .map { it.toResponse() }
    }
    
    fun getCategory(id: UUID): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { NotFoundException("Category not found") }
        return category.toResponse()
    }
    
    @Transactional
    fun createCategory(request: CreateCategoryRequest): CategoryResponse {
        val category = CategoryEntity().apply {
            name = request.name
            slug = request.name.lowercase().replace(" ", "-")
            description = request.description
            icon = request.icon
            sortOrder = request.sortOrder
            isActive = request.isActive
        }
        return categoryRepository.save(category).toResponse()
    }
    
    @Transactional
    fun updateCategory(id: UUID, request: UpdateCategoryRequest): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { NotFoundException("Category not found") }
        
        request.name?.let { category.name = it }
        request.description?.let { category.description = it }
        request.icon?.let { category.icon = it }
        request.sortOrder?.let { category.sortOrder = it }
        request.isActive?.let { category.isActive = it }
        
        return categoryRepository.save(category).toResponse()
    }
    
    @Transactional
    fun deleteCategory(id: UUID) {
        if (!categoryRepository.existsById(id)) {
            throw NotFoundException("Category not found")
        }
        categoryRepository.deleteById(id)
    }
    
    private fun CategoryEntity.toResponse(): CategoryResponse {
        return CategoryResponse(
            id = id!!,
            name = name,
            slug = slug,
            description = description,
            icon = icon,
            parentId = parent?.id,
            sortOrder = sortOrder,
            isActive = isActive,
            createdAt = createdAt!!
        )
    }
}
