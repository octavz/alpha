package com.alpha.domain.repository

import com.alpha.domain.entity.PasswordResetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.*

interface PasswordResetRepository : JpaRepository<PasswordResetEntity, UUID> {
    fun findByToken(token: String): PasswordResetEntity?
    @Query("SELECT pr FROM PasswordResetEntity pr WHERE pr.user.id = :userId")
    fun findByUserId(@Param("userId") userId: UUID): List<PasswordResetEntity>
    
    @Query("SELECT pr FROM PasswordResetEntity pr WHERE pr.expiresAt < :now")
    fun findExpiredResets(@Param("now") now: OffsetDateTime): List<PasswordResetEntity>
    
    @Modifying
    @Query("DELETE FROM PasswordResetEntity pr WHERE pr.expiresAt < :now")
    fun deleteExpiredResets(@Param("now") now: OffsetDateTime): Int
    
    @Modifying
    @Query("DELETE FROM PasswordResetEntity pr WHERE pr.user.id = :userId")
    fun deleteByUserId(@Param("userId") userId: UUID): Int
}