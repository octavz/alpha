package com.alpha.domain.repository

import com.alpha.domain.entity.EmailVerificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.*

interface EmailVerificationRepository : JpaRepository<EmailVerificationEntity, UUID> {
    fun findByToken(token: String): EmailVerificationEntity?
    @Query("SELECT ev FROM EmailVerificationEntity ev WHERE ev.user.id = :userId")
    fun findByUserId(@Param("userId") userId: UUID): List<EmailVerificationEntity>
    
    @Query("SELECT ev FROM EmailVerificationEntity ev WHERE ev.expiresAt < :now")
    fun findExpiredVerifications(@Param("now") now: OffsetDateTime): List<EmailVerificationEntity>
    
    @Modifying
    @Query("DELETE FROM EmailVerificationEntity ev WHERE ev.expiresAt < :now")
    fun deleteExpiredVerifications(@Param("now") now: OffsetDateTime): Int
    
    @Modifying
    @Query("DELETE FROM EmailVerificationEntity ev WHERE ev.user.id = :userId")
    fun deleteByUserId(@Param("userId") userId: UUID): Int
}