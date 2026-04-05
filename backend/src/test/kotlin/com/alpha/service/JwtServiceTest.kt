package com.alpha.service

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class JwtServiceTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var jwtService: JwtService

    private lateinit var testUser: UserEntity

    @BeforeEach
    fun setUp() {
        testUser = UserEntity().apply {
            id = java.util.UUID.randomUUID()
            email = "test@example.com"
            name = "Test User"
            role = UserRole.CUSTOMER
            emailVerified = true
            createdAt = java.time.OffsetDateTime.now()
        }
    }

    @Test
    fun `generateAccessToken should create valid token`() {
        val token = jwtService.generateAccessToken(testUser)
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `generateRefreshToken should create valid token`() {
        val token = jwtService.generateRefreshToken(testUser)
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `validateToken should return decoded JWT for valid token`() {
        val token = jwtService.generateAccessToken(testUser)
        val decoded = jwtService.validateToken(token)
        assertNotNull(decoded)
    }

    @Test
    fun `extractUserId should return correct user ID`() {
        val token = jwtService.generateAccessToken(testUser)
        val userId = jwtService.extractUserId(token)
        assertEquals(testUser.id, userId)
    }

    @Test
    fun `extractUserRole should return correct role`() {
        val token = jwtService.generateAccessToken(testUser)
        val role = jwtService.extractUserRole(token)
        assertEquals(UserRole.CUSTOMER, role)
    }

    @Test
    fun `extractUserRole should return ADMIN for admin user`() {
        val adminUser = UserEntity().apply {
            id = java.util.UUID.randomUUID()
            email = "admin@example.com"
            role = UserRole.ADMIN
        }
        val token = jwtService.generateAccessToken(adminUser)
        val role = jwtService.extractUserRole(token)
        assertEquals(UserRole.ADMIN, role)
    }

    @Test
    fun `isTokenExpired should return false for fresh token`() {
        val token = jwtService.generateAccessToken(testUser)
        assertFalse(jwtService.isTokenExpired(token))
    }

    @Test
    fun `isTokenExpired should return true for invalid token`() {
        assertTrue(jwtService.isTokenExpired("invalid-token"))
    }

    @Test
    fun `validateToken should throw for invalid token`() {
        assertThrows(com.alpha.service.exception.AuthenticationException::class.java) {
            jwtService.validateToken("invalid-token")
        }
    }

    @Test
    fun `extractUserId should throw for invalid token`() {
        assertThrows(com.alpha.service.exception.AuthenticationException::class.java) {
            jwtService.extractUserId("invalid-token")
        }
    }

    @Test
    fun `extractUserRole should throw for invalid token`() {
        assertThrows(com.alpha.service.exception.AuthenticationException::class.java) {
            jwtService.extractUserRole("invalid-token")
        }
    }
}
