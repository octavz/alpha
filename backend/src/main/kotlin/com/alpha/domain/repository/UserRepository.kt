package com.alpha.domain.repository

import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
    fun findByGoogleId(googleId: String): UserEntity?
    fun existsByEmail(email: String): Boolean
    fun findByEmailVerifiedTrue(): List<UserEntity>
    fun findByRole(role: UserRole): List<UserEntity>
    fun findByRegionId(regionId: UUID): List<UserEntity>
    fun findByIsBannedFalse(): List<UserEntity>
    
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    fun searchByEmailOrName(@Param("search") search: String): List<UserEntity>
}