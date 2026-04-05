package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.PasswordResetEntity
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
class PasswordResetRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var passwordResetRepository: PasswordResetRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: UserEntity

    @BeforeEach
    fun setUp() {
        testUser = userRepository.save(UserEntity().apply {
            email = "user-${UUID.randomUUID()}@example.com"
            name = "Test User"
            role = UserRole.CUSTOMER
        })
    }

    private fun createPasswordReset(
        user: UserEntity = testUser,
        token: String = "token-${UUID.randomUUID()}",
        expiresAt: OffsetDateTime = OffsetDateTime.now().plusHours(1)
    ): PasswordResetEntity {
        return passwordResetRepository.save(PasswordResetEntity(
            user = user,
            token = token,
            expiresAt = expiresAt
        ))
    }

    @Test
    fun `findByToken should return reset when exists`() {
        val reset = createPasswordReset(token = "unique-token-${UUID.randomUUID()}")

        val found = passwordResetRepository.findByToken(reset.token)

        assertNotNull(found)
        assertEquals(reset.token, found?.token)
    }

    @Test
    fun `findByToken should return null when not exists`() {
        val found = passwordResetRepository.findByToken("nonexistent-token")
        assertNull(found)
    }

    @Test
    fun `findByUserId should return all resets for user`() {
        createPasswordReset(token = "token-1-${UUID.randomUUID()}")
        createPasswordReset(token = "token-2-${UUID.randomUUID()}")

        val result = passwordResetRepository.findByUserId(testUser.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findExpiredResets should return only expired resets`() {
        val expired = createPasswordReset(
            token = "expired-${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().minusHours(1)
        )
        val active = createPasswordReset(
            token = "active-${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().plusHours(1)
        )

        val result = passwordResetRepository.findExpiredResets(OffsetDateTime.now())

        assertTrue(result.any { it.id == expired.id })
        assertFalse(result.any { it.id == active.id })
    }

    @Test
    fun `deleteExpiredResets should delete and return count`() {
        createPasswordReset(token = "expired-1-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().minusHours(1))
        createPasswordReset(token = "expired-2-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().minusHours(2))
        createPasswordReset(token = "active-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().plusHours(1))

        val deleted = passwordResetRepository.deleteExpiredResets(OffsetDateTime.now())

        assertEquals(2, deleted)
    }

    @Test
    fun `deleteByUserId should delete all resets for user and return count`() {
        createPasswordReset(token = "user1-${UUID.randomUUID()}")
        createPasswordReset(token = "user2-${UUID.randomUUID()}")

        val deleted = passwordResetRepository.deleteByUserId(testUser.id!!)

        assertEquals(2, deleted)
    }
}
