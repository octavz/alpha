package com.alpha.domain.repository

import com.alpha.domain.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CategoryRepository : JpaRepository<CategoryEntity, UUID> {
    fun findBySlug(slug: String): CategoryEntity?
    @Query("SELECT c FROM CategoryEntity c WHERE c.parent.id = :parentId")
    fun findByParentId(@Param("parentId") parentId: UUID?): List<CategoryEntity>
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.isActive = true")
    fun findByIsActiveTrue(): List<CategoryEntity>
    
    fun existsBySlug(slug: String): Boolean
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.parent IS NULL ORDER BY c.sortOrder")
    fun findRootCategories(): List<CategoryEntity>
    
    @Query("SELECT c FROM CategoryEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.slug) LIKE LOWER(CONCAT('%', :search, '%'))")
    fun search(@Param("search") search: String): List<CategoryEntity>
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.parent.id = :parentId ORDER BY c.sortOrder")
    fun findByParentIdOrdered(@Param("parentId") parentId: UUID): List<CategoryEntity>
}