package com.alpha.web.controller

import com.alpha.service.AuthService
import com.alpha.service.SecurityContextService
import com.alpha.service.dto.*
import com.alpha.web.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
class AuthController(
    private val authService: AuthService,
    private val securityContextService: SecurityContextService
) {
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
        requestHttp: HttpServletRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val userAgent = requestHttp.getHeader("User-Agent")
        val ipAddress = requestHttp.getHeader("X-Forwarded-For") ?: requestHttp.remoteAddr
        
        val result = authService.register(request, userAgent, ipAddress)
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(result))
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        requestHttp: HttpServletRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val userAgent = requestHttp.getHeader("User-Agent")
        val ipAddress = requestHttp.getHeader("X-Forwarded-For") ?: requestHttp.remoteAddr
        
        val result = authService.login(request, userAgent, ipAddress)
        
        return ResponseEntity.ok(ApiResponse.success(result))
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    fun refreshToken(
        @Valid @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<ApiResponse<TokenRefreshResponse>> {
        val result = authService.refreshToken(request)
        return ResponseEntity.ok(ApiResponse.success(result))
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    fun logout(
        @Valid @RequestBody request: LogoutRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = securityContextService.getCurrentUserId()
        authService.logout(request.sessionId, userId)
        return ResponseEntity.ok(ApiResponse.success())
    }
    
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address")
    fun verifyEmail(
        @Valid @RequestBody request: VerifyEmailRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.verifyEmail(request.token)
        return ResponseEntity.ok(ApiResponse.success())
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    fun forgotPassword(
        @Valid @RequestBody request: ForgotPasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.forgotPassword(request.email)
        return ResponseEntity.ok(ApiResponse.success())
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.resetPassword(request.token, request.newPassword)
        return ResponseEntity.ok(ApiResponse.success())
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    fun getCurrentUser(): ResponseEntity<ApiResponse<UserResponse>> {
        val userId = securityContextService.getCurrentUserId()
        val user = authService.getCurrentUser(userId)
        return ResponseEntity.ok(ApiResponse.success(user))
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update user profile")
    fun updateProfile(
        @RequestParam name: String?,
        @RequestParam phone: String?,
        @RequestParam avatarUrl: String?
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val userId = securityContextService.getCurrentUserId()
        authService.updateProfile(userId, name, phone, avatarUrl)
        val user = authService.getCurrentUser(userId)
        return ResponseEntity.ok(ApiResponse.success(user))
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Change password")
    fun changePassword(
        @RequestParam currentPassword: String,
        @RequestParam newPassword: String
    ): ResponseEntity<ApiResponse<Unit>> {
        val userId = securityContextService.getCurrentUserId()
        authService.changePassword(userId, currentPassword, newPassword)
        return ResponseEntity.ok(ApiResponse.success())
    }
}