package com.alpha.domain.repository

import com.alpha.domain.entity.BusinessHoursEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface BusinessHoursRepository : JpaRepository<BusinessHoursEntity, UUID> {
    @Query("SELECT bh FROM BusinessHoursEntity bh WHERE bh.business.id = :businessId")
    fun findByBusinessId(@Param("businessId") businessId: UUID): List<BusinessHoursEntity>
    
    @Query("SELECT bh FROM BusinessHoursEntity bh WHERE bh.business.id = :businessId ORDER BY bh.dayOfWeek")
    fun findByBusinessIdOrdered(@Param("businessId") businessId: UUID): List<BusinessHoursEntity>
    
    @Query("SELECT bh FROM BusinessHoursEntity bh WHERE bh.business.id = :businessId AND bh.dayOfWeek = :dayOfWeek")
    fun findByBusinessIdAndDayOfWeek(@Param("businessId") businessId: UUID, @Param("dayOfWeek") dayOfWeek: Int): BusinessHoursEntity?
}