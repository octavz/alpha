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
    @field:Size(max = 255, message = "Email cannot exceed 255 characters")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,
    
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    val name: String,
    
    @field:Size(max = 20, message = "Phone cannot exceed 20 characters")
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
    @field:Size(max = 255, message = "Email cannot exceed 255 characters")
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
    @field:Size(max = 255, message = "Email cannot exceed 255 characters")
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "Token is required")
    @field:Size(min = 32, max = 256, message = "Token must be between 32 and 256 characters")
    val token: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val newPassword: String
)

data class UpdateProfileRequest(
    @field:Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    val name: String? = null,
    
    @field:Size(max = 20, message = "Phone cannot exceed 20 characters")
    val phone: String? = null
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val newPassword: String
)