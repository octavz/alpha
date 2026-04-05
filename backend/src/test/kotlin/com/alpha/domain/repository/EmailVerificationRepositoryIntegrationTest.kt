package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.EmailVerificationEntity
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
class EmailVerificationRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var emailVerificationRepository: EmailVerificationRepository

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

    private fun createEmailVerification(
        user: UserEntity = testUser,
        token: String = "token-${UUID.randomUUID()}",
        expiresAt: OffsetDateTime = OffsetDateTime.now().plusHours(24)
    ): EmailVerificationEntity {
        return emailVerificationRepository.save(EmailVerificationEntity(
            user = user,
            token = token,
            expiresAt = expiresAt
        ))
    }

    @Test
    fun `findByToken should return verification when exists`() {
        val verification = createEmailVerification(token = "unique-token-${UUID.randomUUID()}")

        val found = emailVerificationRepository.findByToken(verification.token)

        assertNotNull(found)
        assertEquals(verification.token, found?.token)
    }

    @Test
    fun `findByToken should return null when not exists`() {
        val found = emailVerificationRepository.findByToken("nonexistent-token")
        assertNull(found)
    }

    @Test
    fun `findByUserId should return all verifications for user`() {
        createEmailVerification(token = "token-1-${UUID.randomUUID()}")
        createEmailVerification(token = "token-2-${UUID.randomUUID()}")

        val result = emailVerificationRepository.findByUserId(testUser.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findExpiredVerifications should return only expired verifications`() {
        val expired = createEmailVerification(
            token = "expired-${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().minusHours(1)
        )
        val active = createEmailVerification(
            token = "active-${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().plusHours(1)
        )

        val result = emailVerificationRepository.findExpiredVerifications(OffsetDateTime.now())

        assertTrue(result.any { it.id == expired.id })
        assertFalse(result.any { it.id == active.id })
    }

    @Test
    fun `deleteExpiredVerifications should delete and return count`() {
        createEmailVerification(token = "expired-1-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().minusHours(1))
        createEmailVerification(token = "expired-2-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().minusHours(2))
        createEmailVerification(token = "active-${UUID.randomUUID()}", expiresAt = OffsetDateTime.now().plusHours(1))

        val deleted = emailVerificationRepository.deleteExpiredVerifications(OffsetDateTime.now())

        assertEquals(2, deleted)
    }

    @Test
    fun `deleteByUserId should delete all verifications for user and return count`() {
        createEmailVerification(token = "user1-${UUID.randomUUID()}")
        createEmailVerification(token = "user2-${UUID.randomUUID()}")

        val deleted = emailVerificationRepository.deleteByUserId(testUser.id!!)

        assertEquals(2, deleted)
    }
}
