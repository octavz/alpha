package com.alpha.service

import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import com.alpha.domain.repository.*
import com.alpha.service.dto.LoginRequest
import com.alpha.service.dto.RegisterRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class SimpleAuthServiceTest {

    private lateinit var authService: AuthService
    private lateinit var userRepository: UserRepository
    private lateinit var sessionRepository: SessionRepository
    private lateinit var emailVerificationRepository: EmailVerificationRepository
    private lateinit var passwordResetRepository: PasswordResetRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtService: JwtService
    private lateinit var emailService: EmailService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        sessionRepository = mockk()
        emailVerificationRepository = mockk()
        passwordResetRepository = mockk()
        passwordEncoder = mockk()
        jwtService = mockk()
        emailService = mockk()
        
        authService = AuthService(
            userRepository = userRepository,
            sessionRepository = sessionRepository,
            emailVerificationRepository = emailVerificationRepository,
            passwordResetRepository = passwordResetRepository,
            passwordEncoder = passwordEncoder,
            jwtService = jwtService,
            emailService = emailService
        )
    }

    @Test
    fun `register should create new user`() {
        // Given
        val request = RegisterRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns "encodedPassword"
        every { userRepository.save(any()) } answers {
            val user = it.invocation.args[0] as UserEntity
            user.id = UUID.randomUUID()
            user.createdAt = java.time.OffsetDateTime.now()
            user.updatedAt = java.time.OffsetDateTime.now()
            user
        }
        every { sessionRepository.save(any()) } returnsArgument 0
        every { emailVerificationRepository.save(any()) } returnsArgument 0
        every { emailService.sendVerificationEmail(any(), any()) } returns Unit
        every { jwtService.generateAccessToken(any()) } returns "access-token"

        // When
        val result = authService.register(request, "test-agent", "127.0.0.1")

        // Then
        assertEquals(request.email, result.user.email)
        assertEquals(request.name, result.user.name)
        assertEquals("access-token", result.accessToken)
    }

    @Test
    fun `login should succeed with valid credentials`() {
        // Given
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        val user = UserEntity().apply {
            id = UUID.randomUUID()
            email = request.email
            passwordHash = "encodedPassword"
            name = "Test User"
            role = UserRole.CUSTOMER
            emailVerified = true
            isBanned = false
            createdAt = java.time.OffsetDateTime.now()
            updatedAt = java.time.OffsetDateTime.now()
        }

        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.passwordHash) } returns true
        every { sessionRepository.save(any()) } returnsArgument 0
        every { jwtService.generateAccessToken(user) } returns "access-token"

        // When
        val result = authService.login(request, "test-agent", "127.0.0.1")

        // Then
        assertEquals(user.email, result.user.email)
        assertEquals(user.name, result.user.name)
        assertEquals("access-token", result.accessToken)
    }
}