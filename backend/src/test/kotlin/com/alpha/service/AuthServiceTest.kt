package com.alpha.service

import com.alpha.domain.entity.*
import com.alpha.domain.enums.UserRole
import com.alpha.domain.enums.VerificationStatus
import com.alpha.domain.repository.*
import com.alpha.service.dto.*
import com.alpha.service.exception.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class AuthServiceIntegrationTest {

    private val userRepository = mockk<UserRepository>()
    private val sessionRepository = mockk<SessionRepository>()
    private val emailVerificationRepository = mockk<EmailVerificationRepository>()
    private val passwordResetRepository = mockk<PasswordResetRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtService = mockk<JwtService>()
    private val emailService = mockk<EmailService>()

    private val authService = AuthService(
        userRepository,
        sessionRepository,
        emailVerificationRepository,
        passwordResetRepository,
        passwordEncoder,
        jwtService,
        emailService
    )

    private lateinit var testUser: UserEntity

    @BeforeEach
    fun setUp() {
        testUser = UserEntity().apply {
            id = UUID.randomUUID()
            email = "test@example.com"
            passwordHash = "encodedPassword"
            name = "Test User"
            phone = "1234567890"
            role = UserRole.CUSTOMER
            emailVerified = false
            isBanned = false
        }

        clearAllMocks()
    }

@Test
    fun `register should create user and return auth response`() {
        val request = RegisterRequest(
            email = "new@example.com",
            password = "password123",
            name = "New User",
            phone = "0987654321",
            role = UserRole.CUSTOMER
        )

        every { userRepository.existsByEmail("new@example.com") } returns false
        every { userRepository.save(any()) } answers {
            val user = firstArg<UserEntity>()
            user.apply { id = UUID.randomUUID() }
        }
        every { passwordEncoder.encode("password123") } returns "encodedPassword"
        every { sessionRepository.save(any()) } answers {
            val session = firstArg<SessionEntity>()
            session.apply {
                id = UUID.randomUUID()
                token = "token-${UUID.randomUUID()}"
                refreshToken = "refresh-${UUID.randomUUID()}"
            }
        }
        every { emailVerificationRepository.save(any()) } answers { firstArg() }
        every { emailService.sendVerificationEmail(any(), any()) } just runs
        every { jwtService.generateAccessToken(any()) } returns "access-token"
        every { jwtService.generateRefreshToken(any()) } returns "refresh-token"

        val response = authService.register(request, "Mozilla", "127.0.0.1")

        assertEquals("new@example.com", response.user.email)
        assertEquals("New User", response.user.name)
        assertEquals("access-token", response.accessToken)
    }

    @Test
    fun `register should throw ConflictException when email exists`() {
        val request = RegisterRequest(
            email = "exists@example.com",
            password = "password123",
            name = "Existing User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        every { userRepository.existsByEmail("exists@example.com") } returns true

        assertThrows(ConflictException::class.java) {
            authService.register(request, null, null)
        }
    }

    @Test
    fun `register should throw ValidationException for invalid email`() {
        val request = RegisterRequest(
            email = "invalid-email",
            password = "password123",
            name = "User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        assertThrows(ValidationException::class.java) {
            authService.register(request, null, null)
        }
    }

    @Test
    fun `login should return auth response for valid credentials`() {
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        every { userRepository.findByEmail("test@example.com") } returns testUser
        every { passwordEncoder.matches("password123", "encodedPassword") } returns true
        every { sessionRepository.save(any()) } answers {
            val session = firstArg<SessionEntity>()
            session.apply { id = UUID.randomUUID() }
        }
        every { jwtService.generateAccessToken(testUser) } returns "access-token"

        val response = authService.login(request, "Mozilla", "127.0.0.1")

        assertEquals("test@example.com", response.user.email)
        assertEquals("access-token", response.accessToken)
    }

    @Test
    fun `login should throw AuthenticationException for invalid email`() {
        val request = LoginRequest(
            email = "nonexistent@example.com",
            password = "password123"
        )

        every { userRepository.findByEmail("nonexistent@example.com") } returns null

        assertThrows(AuthenticationException::class.java) {
            authService.login(request, null, null)
        }
    }

    @Test
    fun `login should throw AuthenticationException for wrong password`() {
        val request = LoginRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        every { userRepository.findByEmail("test@example.com") } returns testUser
        every { passwordEncoder.matches("wrongpassword", "encodedPassword") } returns false

        assertThrows(AuthenticationException::class.java) {
            authService.login(request, null, null)
        }
    }

    @Test
    fun `login should throw AuthenticationException for banned user`() {
        val bannedUser = testUser.apply { isBanned = true }
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        every { userRepository.findByEmail("test@example.com") } returns bannedUser

        assertThrows(AuthenticationException::class.java) {
            authService.login(request, null, null)
        }
    }

    @Test
    fun `login should throw for user with null passwordHash`() {
        val googleUser = testUser.apply { passwordHash = null; googleId = "google-123" }
        val request = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        every { userRepository.findByEmail("test@example.com") } returns googleUser

        assertThrows(AuthenticationException::class.java) {
            authService.login(request, null, null)
        }
    }

    @Test
    fun `refreshToken should return new tokens for valid session`() {
        val session = SessionEntity(
            user = testUser,
            token = "",
            refreshToken = "valid-refresh-token",
            userAgent = "Mozilla",
            ipAddress = "127.0.0.1",
            expiresAt = java.time.OffsetDateTime.now().plusDays(7)
        ).apply { id = UUID.randomUUID() }

        val request = RefreshTokenRequest("valid-refresh-token")

        every { sessionRepository.findByRefreshToken("valid-refresh-token") } returns session
        every { sessionRepository.save(any()) } answers {
            val s = firstArg<SessionEntity>()
            s.apply { id = UUID.randomUUID() }
        }
        every { sessionRepository.delete(session) } just runs
        every { jwtService.generateAccessToken(testUser) } returns "new-access-token"

        val response = authService.refreshToken(request)

        assertEquals("new-access-token", response.accessToken)
    }

    @Test
    fun `refreshToken should throw for invalid refresh token`() {
        val request = RefreshTokenRequest("invalid-token")

        every { sessionRepository.findByRefreshToken("invalid-token") } returns null

        assertThrows(AuthenticationException::class.java) {
            authService.refreshToken(request)
        }
    }

    @Test
    fun `refreshToken should throw for expired session`() {
        val session = SessionEntity(
            user = testUser,
            token = "",
            refreshToken = "expired-token",
            userAgent = "Mozilla",
            ipAddress = "127.0.0.1",
            expiresAt = java.time.OffsetDateTime.now().minusDays(1)
        ).apply { id = UUID.randomUUID() }

        val request = RefreshTokenRequest("expired-token")

        every { sessionRepository.findByRefreshToken("expired-token") } returns session
        every { sessionRepository.delete(session) } just runs

        assertThrows(AuthenticationException::class.java) {
            authService.refreshToken(request)
        }
    }

    @Test
    fun `logout should delete session`() {
        val sessionId = UUID.randomUUID()
        val session = SessionEntity(
            user = testUser,
            token = "",
            refreshToken = "refresh",
            userAgent = "Mozilla",
            ipAddress = "127.0.0.1",
            expiresAt = java.time.OffsetDateTime.now().plusDays(7)
        ).apply { id = sessionId }

        every { sessionRepository.findById(sessionId) } returns java.util.Optional.of(session)
        every { sessionRepository.delete(session) } just runs

        authService.logout(sessionId.toString(), testUser.id!!)

        verify { sessionRepository.delete(session) }
    }

    @Test
    fun `logout should throw ForbiddenException for other user session`() {
        val sessionId = UUID.randomUUID()
        val otherUser = UserEntity().apply {
            id = UUID.randomUUID()
            email = "other@example.com"
            role = UserRole.CUSTOMER
        }
        val session = SessionEntity(
            user = otherUser,
            token = "",
            refreshToken = "refresh",
            userAgent = "Mozilla",
            ipAddress = "127.0.0.1",
            expiresAt = java.time.OffsetDateTime.now().plusDays(7)
        ).apply { id = sessionId }

        every { sessionRepository.findById(sessionId) } returns java.util.Optional.of(session)

        assertThrows(ForbiddenException::class.java) {
            authService.logout(sessionId.toString(), testUser.id!!)
        }
    }

    @Test
    fun `logout should throw NotFoundException for invalid session`() {
        val sessionId = UUID.randomUUID()
        every { sessionRepository.findById(sessionId) } returns java.util.Optional.empty()

        assertThrows(NotFoundException::class.java) {
            authService.logout(sessionId.toString(), testUser.id!!)
        }
    }

    @Test
    fun `verifyEmail should mark user as verified`() {
        val verification = EmailVerificationEntity(
            user = testUser,
            token = "verify-token",
            expiresAt = java.time.OffsetDateTime.now().plusHours(24)
        )

        every { emailVerificationRepository.findByToken("verify-token") } returns verification
        every { userRepository.save(testUser) } returns testUser
        every { emailVerificationRepository.delete(verification) } just runs

        authService.verifyEmail("verify-token")

        assertTrue(testUser.emailVerified)
    }

    @Test
    fun `verifyEmail should throw for invalid token`() {
        every { emailVerificationRepository.findByToken("invalid") } returns null

        assertThrows(ValidationException::class.java) {
            authService.verifyEmail("invalid")
        }
    }

    @Test
    fun `verifyEmail should throw for expired token`() {
        val verification = EmailVerificationEntity(
            user = testUser,
            token = "expired-token",
            expiresAt = java.time.OffsetDateTime.now().minusHours(1)
        )

        every { emailVerificationRepository.findByToken("expired-token") } returns verification
        every { emailVerificationRepository.delete(verification) } just runs

        assertThrows(ValidationException::class.java) {
            authService.verifyEmail("expired-token")
        }
    }

    @Test
    fun `forgotPassword should create reset token and send email`() {
        every { userRepository.findByEmail("test@example.com") } returns testUser
        every { passwordResetRepository.save(any()) } answers { firstArg() }
        every { emailService.sendPasswordResetEmail(any(), any(), any()) } just runs

        authService.forgotPassword("test@example.com")

        verify { emailService.sendPasswordResetEmail(any(), any(), any()) }
    }

    @Test
    fun `forgotPassword should throw for non-existent user`() {
        every { userRepository.findByEmail("nonexistent@example.com") } returns null

        assertThrows(NotFoundException::class.java) {
            authService.forgotPassword("nonexistent@example.com")
        }
    }

    @Test
    fun `resetPassword should update password and delete sessions`() {
        val passwordReset = PasswordResetEntity(
            user = testUser,
            token = "reset-token",
            expiresAt = java.time.OffsetDateTime.now().plusHours(24)
        )

        every { passwordResetRepository.findByToken("reset-token") } returns passwordReset
        every { passwordEncoder.encode("newPassword") } returns "newEncodedPassword"
        every { userRepository.save(testUser) } returns testUser
        every { sessionRepository.deleteByUserId(any()) } returns 0
        every { passwordResetRepository.delete(passwordReset) } just runs

        authService.resetPassword("reset-token", "newPassword")

        assertEquals("newEncodedPassword", testUser.passwordHash)
    }

    @Test
    fun `resetPassword should throw for invalid token`() {
        every { passwordResetRepository.findByToken("invalid") } returns null

        assertThrows(ValidationException::class.java) {
            authService.resetPassword("invalid", "newPassword")
        }
    }

    @Test
    fun `resetPassword should throw for expired token`() {
        val passwordReset = PasswordResetEntity(
            user = testUser,
            token = "expired-token",
            expiresAt = java.time.OffsetDateTime.now().minusHours(1)
        )

        every { passwordResetRepository.findByToken("expired-token") } returns passwordReset
        every { passwordResetRepository.delete(passwordReset) } just runs

        assertThrows(ValidationException::class.java) {
            authService.resetPassword("expired-token", "newPassword")
        }
    }

    @Test
    fun `getCurrentUser should return user response`() {
        every { userRepository.findById(testUser.id!!) } returns java.util.Optional.of(testUser)

        val response = authService.getCurrentUser(testUser.id!!)

        assertEquals("test@example.com", response.email)
        assertEquals("Test User", response.name)
    }

    @Test
    fun `getCurrentUser should throw for non-existent user`() {
        every { userRepository.findById(any()) } returns java.util.Optional.empty()

        assertThrows(NotFoundException::class.java) {
            authService.getCurrentUser(UUID.randomUUID())
        }
    }

    @Test
    fun `updateProfile should update user fields`() {
        every { userRepository.findById(testUser.id!!) } returns java.util.Optional.of(testUser)
        every { userRepository.save(testUser) } returns testUser

        authService.updateProfile(testUser.id!!, "New Name", "9876543210", "https://avatar.com/img.jpg")

        assertEquals("New Name", testUser.name)
        assertEquals("9876543210", testUser.phone)
        assertEquals("https://avatar.com/img.jpg", testUser.avatarUrl)
    }

    @Test
    fun `updateProfile should only update non-null fields`() {
        every { userRepository.findById(testUser.id!!) } returns java.util.Optional.of(testUser)
        every { userRepository.save(testUser) } returns testUser

        authService.updateProfile(testUser.id!!, "New Name", null, null)

        assertEquals("New Name", testUser.name)
        assertEquals("1234567890", testUser.phone)
    }

    @Test
    fun `changePassword should update password`() {
        every { userRepository.findById(testUser.id!!) } returns java.util.Optional.of(testUser)
        every { passwordEncoder.matches("password123", "encodedPassword") } returns true
        every { passwordEncoder.encode("newPassword") } returns "newEncodedPassword"
        every { userRepository.save(testUser) } returns testUser

        authService.changePassword(testUser.id!!, "password123", "newPassword")

        assertEquals("newEncodedPassword", testUser.passwordHash)
    }

    @Test
    fun `changePassword should throw for wrong current password`() {
        every { userRepository.findById(testUser.id!!) } returns java.util.Optional.of(testUser)
        every { passwordEncoder.matches("wrong", "encodedPassword") } returns false

        assertThrows(ValidationException::class.java) {
            authService.changePassword(testUser.id!!, "wrong", "newPassword")
        }
    }

    @Test
    fun `changePassword should throw for user with null passwordHash`() {
        val googleUser = testUser.apply { passwordHash = null }
        every { userRepository.findById(testUser.id!!) } returns java.util.Optional.of(googleUser)

        assertThrows(ValidationException::class.java) {
            authService.changePassword(testUser.id!!, "password123", "newPassword")
        }
    }
}
