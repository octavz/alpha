package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.SessionEntity
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Transactional
class SessionRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var sessionRepository: SessionRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: UserEntity

    @BeforeEach
    fun setUp() {
        testUser = userRepository.save(UserEntity().apply {
            email = "session-user-${UUID.randomUUID()}@example.com"
            name = "Session User"
            role = UserRole.CUSTOMER
        })
    }

    private fun createSession(
        user: UserEntity = testUser,
        token: String? = "token-${UUID.randomUUID()}",
        refreshToken: String = "refresh-${UUID.randomUUID()}",
        expiresAt: OffsetDateTime = OffsetDateTime.now().plusDays(7)
    ): SessionEntity {
        return sessionRepository.save(SessionEntity(
            user = user,
            token = token,
            refreshToken = refreshToken,
            userAgent = "Test Browser",
            ipAddress = "127.0.0.1",
            expiresAt = expiresAt
        ))
    }

    @Test
    fun `findByToken should return session when exists`() {
        val session = createSession(token = "unique-token-${UUID.randomUUID()}")

        val found = sessionRepository.findByToken(session.token!!)

        assertNotNull(found)
        assertEquals(session.token, found?.token)
    }

    @Test
    fun `findByToken should return null when not exists`() {
        val found = sessionRepository.findByToken("nonexistent-token")
        assertNull(found)
    }

    @Test
    fun `findByRefreshToken should return session when exists`() {
        val session = createSession(refreshToken = "unique-refresh-${UUID.randomUUID()}")

        val found = sessionRepository.findByRefreshToken(session.refreshToken)

        assertNotNull(found)
        assertEquals(session.refreshToken, found?.refreshToken)
    }

    @Test
    fun `findByRefreshToken should return null when not exists`() {
        val found = sessionRepository.findByRefreshToken("nonexistent-refresh")
        assertNull(found)
    }

    @Test
    fun `findByUserId should return all sessions for user`() {
        createSession(refreshToken = "refresh-1-${UUID.randomUUID()}")
        createSession(refreshToken = "refresh-2-${UUID.randomUUID()}")

        val result = sessionRepository.findByUserId(testUser.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findExpiredSessions should return only expired sessions`() {
        val expired = createSession(
            refreshToken = "expired-${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().minusDays(1)
        )
        createSession(
            refreshToken = "active-${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().plusDays(1)
        )

        val result = sessionRepository.findExpiredSessions(OffsetDateTime.now())

        assertTrue(result.any { it.id == expired.id })
        assertFalse(result.any { it.expiresAt.isAfter(OffsetDateTime.now()) })
    }

    @Test
    fun `deleteExpiredSessions should delete and return count`() {
        createSession(refreshToken = "expired-1-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().minusDays(1))
        createSession(refreshToken = "expired-2-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().minusDays(2))
        createSession(refreshToken = "active-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().plusDays(1))

        val deleted = sessionRepository.deleteExpiredSessions(OffsetDateTime.now())

        assertEquals(2, deleted)
    }

    @Test
    fun `deleteByUserId should delete all sessions for user and return count`() {
        createSession(refreshToken = "user1-${UUID.randomUUID()}")
        createSession(refreshToken = "user2-${UUID.randomUUID()}")

        val deleted = sessionRepository.deleteByUserId(testUser.id!!)

        assertEquals(2, deleted)
    }
}
