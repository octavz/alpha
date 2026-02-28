package com.alpha.service.dto

import com.alpha.domain.enums.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.*

data class RegisterRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, message = "Name must be at least 2 characters")
    val name: String,
    
    val phone: String? = null,
    val role: UserRole? = null,
    val regionId: UUID? = null
) {
    val requiredRole: UserRole
        get() = role ?: UserRole.CUSTOMER
}

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class AuthResponse(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String,
    val sessionId: String
)

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String?,
    val phone: String?,
    val role: UserRole,
    val emailVerified: Boolean,
    val avatarUrl: String?,
    val regionId: UUID?,
    val isBanned: Boolean,
    val createdAt: OffsetDateTime
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String
)

data class LogoutRequest(
    @field:NotBlank(message = "Session ID is required")
    val sessionId: String
)

data class VerifyEmailRequest(
    @field:NotBlank(message = "Token is required")
    val token: String
)

data class ForgotPasswordRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "Token is required")
    val token: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val newPassword: String
)