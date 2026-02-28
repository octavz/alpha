package com.alpha.domain.repository

import com.alpha.domain.entity.SessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.*

interface SessionRepository : JpaRepository<SessionEntity, UUID> {
    fun findByToken(token: String): SessionEntity?
    fun findByRefreshToken(refreshToken: String): SessionEntity?
    @Query("SELECT s FROM SessionEntity s WHERE s.user.id = :userId")
    fun findByUserId(@Param("userId") userId: UUID): List<SessionEntity>
    
    @Query("SELECT s FROM SessionEntity s WHERE s.expiresAt < :now")
    fun findExpiredSessions(@Param("now") now: OffsetDateTime): List<SessionEntity>
    
    @Modifying
    @Query("DELETE FROM SessionEntity s WHERE s.expiresAt < :now")
    fun deleteExpiredSessions(@Param("now") now: OffsetDateTime): Int
    
    @Modifying
    @Query("DELETE FROM SessionEntity s WHERE s.user.id = :userId")
    fun deleteByUserId(@Param("userId") userId: UUID): Int
}