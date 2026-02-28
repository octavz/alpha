package com.alpha.domain.repository

import com.alpha.domain.entity.ServiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ServiceRepository : JpaRepository<ServiceEntity, UUID> {
    @Query("SELECT s FROM ServiceEntity s WHERE s.business.id = :businessId")
    fun findByBusinessId(@Param("businessId") businessId: UUID): List<ServiceEntity>
    
    @Query("SELECT s FROM ServiceEntity s WHERE s.business.id = :businessId AND s.isActive = true")
    fun findByBusinessIdAndIsActiveTrue(@Param("businessId") businessId: UUID): List<ServiceEntity>
    
    @Query("SELECT s FROM ServiceEntity s WHERE s.business.id = :businessId ORDER BY s.sortOrder")
    fun findByBusinessIdOrdered(@Param("businessId") businessId: UUID): List<ServiceEntity>
}