package com.alpha.service

import com.alpha.domain.entity.SessionEntity
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import com.alpha.domain.repository.*
import com.alpha.service.dto.*
import com.alpha.service.exception.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*
import java.util.regex.Pattern

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val emailVerificationRepository: EmailVerificationRepository,
    private val passwordResetRepository: PasswordResetRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val emailService: EmailService
) {
    
    private val EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
    
    fun register(request: RegisterRequest, userAgent: String?, ipAddress: String?): AuthResponse {
        // Validate email
        validateEmail(request.email)
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("User with this email already exists")
        }
        
        // Create user
        val user = UserEntity().apply {
            email = request.email
            passwordHash = passwordEncoder.encode(request.password)
            name = request.name
            phone = request.phone
            role = request.requiredRole
            emailVerified = false
            isBanned = false
        }
        
        val savedUser = userRepository.save(user)
        
        // Create session
        val session = createSession(savedUser, userAgent, ipAddress)
        
        // Create email verification token
        createEmailVerificationToken(savedUser)
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser.email, savedUser.name ?: "User")
        
        return AuthResponse(
            user = mapToUserResponse(savedUser),
            accessToken = jwtService.generateAccessToken(savedUser),
            refreshToken = session.refreshToken,
            sessionId = session.id.toString()
        )
    }
    
    fun login(request: LoginRequest, userAgent: String?, ipAddress: String?): AuthResponse {
        // Find user by email
        val user = userRepository.findByEmail(request.email)
            ?: throw AuthenticationException("Invalid email or password")
        
        // Check if user is banned
        if (user.isBanned) {
            throw AuthenticationException("Account is banned")
        }
        
        // Verify password
        if (user.passwordHash == null || !passwordEncoder.matches(request.password, user.passwordHash)) {
            throw AuthenticationException("Invalid email or password")
        }
        
        // Create session
        val session = createSession(user, userAgent, ipAddress)
        
        return AuthResponse(
            user = mapToUserResponse(user),
            accessToken = jwtService.generateAccessToken(user),
            refreshToken = session.refreshToken,
            sessionId = session.id.toString()
        )
    }
    
    fun refreshToken(request: RefreshTokenRequest): TokenRefreshResponse {
        // Find session by refresh token
        val session = sessionRepository.findByRefreshToken(request.refreshToken)
            ?: throw AuthenticationException("Invalid refresh token")
        
        // Check if session is expired
        if (session.expiresAt.isBefore(OffsetDateTime.now())) {
            sessionRepository.delete(session)
            throw AuthenticationException("Session expired")
        }
        
        // Find user
        val user = session.user
        
        // Generate new tokens
        val newAccessToken = jwtService.generateAccessToken(user)
        val newRefreshToken = UUID.randomUUID().toString()
        
        // Create new session (instead of updating)
        val newSession = createSession(user, session.userAgent, session.ipAddress)
        
        // Delete old session
        sessionRepository.delete(session)
        
        return TokenRefreshResponse(
            accessToken = newAccessToken,
            refreshToken = newSession.refreshToken
        )
    }
    
    fun logout(sessionId: String, userId: UUID) {
        val session = sessionRepository.findById(UUID.fromString(sessionId))
            .orElseThrow { NotFoundException("Session not found") }
        
        // Verify session belongs to user
        if (session.user.requiredId != userId) {
            throw ForbiddenException("Cannot logout other user's session")
        }
        
        sessionRepository.delete(session)
    }
    
    fun verifyEmail(token: String) {
        val verification = emailVerificationRepository.findByToken(token)
            ?: throw ValidationException("Invalid verification token")
        
        // Check if token is expired
        if (verification.expiresAt.isBefore(OffsetDateTime.now())) {
            emailVerificationRepository.delete(verification)
            throw ValidationException("Verification token expired")
        }
        
        // Update user
        val user = verification.user
        user.emailVerified = true
        userRepository.save(user)
        
        // Delete verification token
        emailVerificationRepository.delete(verification)
    }
    
    fun forgotPassword(email: String) {
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException("User not found")
        
        // Create password reset token
        val token = UUID.randomUUID().toString()
        val expiresAt = OffsetDateTime.now().plusHours(24)
        
        val passwordReset = passwordResetRepository.save(
            com.alpha.domain.entity.PasswordResetEntity(
                user = user,
                token = token,
                expiresAt = expiresAt
            )
        )
        
        // Send password reset email
        emailService.sendPasswordResetEmail(user.email, user.name ?: "User", token)
    }
    
    fun resetPassword(token: String, newPassword: String) {
        val passwordReset = passwordResetRepository.findByToken(token)
            ?: throw ValidationException("Invalid reset token")
        
        // Check if token is expired
        if (passwordReset.expiresAt.isBefore(OffsetDateTime.now())) {
            passwordResetRepository.delete(passwordReset)
            throw ValidationException("Reset token expired")
        }
        
        // Update user password
        val user = passwordReset.user
        user.passwordHash = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        
        // Delete all user sessions
        sessionRepository.deleteByUserId(user.requiredId)
        
        // Delete password reset token
        passwordResetRepository.delete(passwordReset)
    }
    
    fun getCurrentUser(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }
        
        return mapToUserResponse(user)
    }
    
    fun updateProfile(userId: UUID, name: String?, phone: String?, avatarUrl: String?) {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }
        
        name?.let { user.name = it }
        phone?.let { user.phone = it }
        avatarUrl?.let { user.avatarUrl = it }
        
        userRepository.save(user)
    }
    
    fun changePassword(userId: UUID, currentPassword: String, newPassword: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }
        
        // Verify current password
        if (user.passwordHash == null || !passwordEncoder.matches(currentPassword, user.passwordHash)) {
            throw ValidationException("Current password is incorrect")
        }
        
        // Update password
        user.passwordHash = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        
        // Delete all user sessions except current one
        // (This forces re-login on all devices for security)
        // Implementation depends on how we track current session
    }
    
    private fun validateEmail(email: String) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ValidationException("Invalid email format")
        }
    }
    
    private fun createSession(user: UserEntity, userAgent: String?, ipAddress: String?): SessionEntity {
        val refreshToken = UUID.randomUUID().toString()
        val expiresAt = OffsetDateTime.now().plusDays(7)
        
        return sessionRepository.save(
            SessionEntity(
                user = user,
                token = "", // Not storing access token anymore
                refreshToken = refreshToken,
                userAgent = userAgent,
                ipAddress = ipAddress,
                expiresAt = expiresAt
            )
        )
    }
    
    private fun createEmailVerificationToken(user: UserEntity) {
        val token = UUID.randomUUID().toString()
        val expiresAt = OffsetDateTime.now().plusHours(24)
        
        emailVerificationRepository.save(
            com.alpha.domain.entity.EmailVerificationEntity(
                user = user,
                token = token,
                expiresAt = expiresAt
            )
        )
    }
    
    private fun mapToUserResponse(user: UserEntity): UserResponse {
        return UserResponse(
            id = user.requiredId,
            email = user.email,
            name = user.name,
            phone = user.phone,
            role = user.role,
            emailVerified = user.emailVerified,
            avatarUrl = user.avatarUrl,
            regionId = user.region?.id,
            isBanned = user.isBanned,
            createdAt = user.requiredCreatedAt
        )
    }
}