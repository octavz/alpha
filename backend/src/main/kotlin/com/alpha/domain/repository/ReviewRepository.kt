package com.alpha.domain.repository

import com.alpha.domain.entity.ReviewEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ReviewRepository : JpaRepository<ReviewEntity, UUID> {
    @Query("SELECT r FROM ReviewEntity r WHERE r.business.id = :businessId")
    fun findByBusinessId(@Param("businessId") businessId: UUID): List<ReviewEntity>
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.customer.id = :customerId")
    fun findByCustomerId(@Param("customerId") customerId: UUID): List<ReviewEntity>
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.appointment.id = :appointmentId")
    fun findByAppointmentId(@Param("appointmentId") appointmentId: UUID): ReviewEntity?
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.isApproved = true")
    fun findByIsApprovedTrue(): List<ReviewEntity>
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.isFeatured = true")
    fun findByIsFeaturedTrue(): List<ReviewEntity>
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.business.id = :businessId AND r.isApproved = true ORDER BY r.createdAt DESC")
    fun findApprovedByBusiness(@Param("businessId") businessId: UUID, pageable: Pageable): Page<ReviewEntity>
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.business.id = :businessId AND r.isApproved = true AND r.isFeatured = true ORDER BY r.createdAt DESC")
    fun findFeaturedByBusiness(@Param("businessId") businessId: UUID, pageable: Pageable): Page<ReviewEntity>
    
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.business.id = :businessId AND r.isApproved = true")
    fun calculateAverageRating(@Param("businessId") businessId: UUID): Double?
    
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.business.id = :businessId AND r.isApproved = true")
    fun countApprovedByBusiness(@Param("businessId") businessId: UUID): Long
    
    @Query("""
        SELECT r.rating, COUNT(r) 
        FROM ReviewEntity r 
        WHERE r.business.id = :businessId AND r.isApproved = true 
        GROUP BY r.rating 
        ORDER BY r.rating DESC
    """)
    fun getRatingDistribution(@Param("businessId") businessId: UUID): List<Array<Any>>
}