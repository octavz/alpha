package com.alpha.domain.repository

import com.alpha.domain.entity.BusinessEntity
import com.alpha.domain.enums.VerificationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface BusinessRepository : JpaRepository<BusinessEntity, UUID> {
    fun findBySlug(slug: String): BusinessEntity?
    @Query("SELECT b FROM BusinessEntity b WHERE b.user.id = :userId")
    fun findByUserId(@Param("userId") userId: UUID): List<BusinessEntity>
    
    @Query("SELECT b FROM BusinessEntity b WHERE b.region.id = :regionId")
    fun findByRegionId(@Param("regionId") regionId: UUID): List<BusinessEntity>
    
    @Query("SELECT b FROM BusinessEntity b WHERE b.category.id = :categoryId")
    fun findByCategoryId(@Param("categoryId") categoryId: UUID): List<BusinessEntity>
    fun findByIsActiveTrue(): List<BusinessEntity>
    fun findByIsVerifiedTrue(): List<BusinessEntity>
    fun findByIsFeaturedTrue(): List<BusinessEntity>
    fun findByVerificationStatus(status: VerificationStatus): List<BusinessEntity>
    fun existsBySlug(slug: String): Boolean
    
    @Query("SELECT b FROM BusinessEntity b WHERE b.isActive = true AND b.isVerified = true ORDER BY b.createdAt DESC")
    fun findActiveVerifiedBusinesses(pageable: Pageable): Page<BusinessEntity>
    
    @Query("SELECT b FROM BusinessEntity b WHERE b.region.id = :regionId AND b.isActive = true AND b.isVerified = true")
    fun findActiveVerifiedByRegion(@Param("regionId") regionId: UUID, pageable: Pageable): Page<BusinessEntity>
    
    @Query("SELECT b FROM BusinessEntity b WHERE b.category.id = :categoryId AND b.isActive = true AND b.isVerified = true")
    fun findActiveVerifiedByCategory(@Param("categoryId") categoryId: UUID, pageable: Pageable): Page<BusinessEntity>
    
    @Query("""
        SELECT b FROM BusinessEntity b 
        WHERE (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) 
        OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%')))
        AND b.isActive = true AND b.isVerified = true
    """)
    fun searchActiveVerified(@Param("search") search: String, pageable: Pageable): Page<BusinessEntity>
    
    @Query(
        value = """
        SELECT b.* FROM businesses b 
        WHERE b.latitude IS NOT NULL AND b.longitude IS NOT NULL
        AND b.is_active = true AND b.is_verified = true
        AND ST_DWithin(
            ST_MakePoint(b.longitude, b.latitude)::geography,
            ST_MakePoint(:longitude, :latitude)::geography,
            :radius
        )
        """,
        countQuery = """
        SELECT COUNT(*) FROM businesses b 
        WHERE b.latitude IS NOT NULL AND b.longitude IS NOT NULL
        AND b.is_active = true AND b.is_verified = true
        AND ST_DWithin(
            ST_MakePoint(b.longitude, b.latitude)::geography,
            ST_MakePoint(:longitude, :latitude)::geography,
            :radius
        )
        """,
        nativeQuery = true
    )
    fun findNearby(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("radius") radius: Double,
        pageable: Pageable
    ): Page<BusinessEntity>
}