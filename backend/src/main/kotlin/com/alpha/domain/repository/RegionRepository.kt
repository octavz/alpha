package com.alpha.domain.repository

import com.alpha.domain.entity.RegionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface RegionRepository : JpaRepository<RegionEntity, UUID> {
    fun findByCode(code: String): RegionEntity?
    fun findByCountry(country: String): List<RegionEntity>
    fun findByIsActiveTrue(): List<RegionEntity>
    fun existsByCode(code: String): Boolean
    
    @Query("SELECT r FROM RegionEntity r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.country) LIKE LOWER(CONCAT('%', :search, '%'))")
    fun search(@Param("search") search: String): List<RegionEntity>
}