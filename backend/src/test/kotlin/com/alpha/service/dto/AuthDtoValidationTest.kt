package com.alpha.service.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class AuthDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `RegisterRequest should fail for blank email`() {
        val request = RegisterRequest(
            email = "",
            password = "password123",
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `RegisterRequest should fail for invalid email format`() {
        val request = RegisterRequest(
            email = "not-an-email",
            password = "password123",
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `RegisterRequest should fail for email too long`() {
        val request = RegisterRequest(
            email = "a".repeat(256) + "@test.com",
            password = "password123",
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `RegisterRequest should fail for missing password`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "",
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    fun `RegisterRequest should fail for password too short`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "short",
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    fun `RegisterRequest should fail for password too long`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "a".repeat(101),
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    fun `RegisterRequest should fail for missing name`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "password123",
            name = ""
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `RegisterRequest should fail for name too short`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "password123",
            name = "A"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `RegisterRequest should fail for name too long`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "password123",
            name = "a".repeat(256)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `RegisterRequest should fail for phone too long`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "password123",
            name = "Test User",
            phone = "1".repeat(21)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "phone" })
    }

    @Test
    fun `RegisterRequest should pass for valid input`() {
        val request = RegisterRequest(
            email = "test@test.com",
            password = "password123",
            name = "Test User"
        )
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `LoginRequest should fail for blank email`() {
        val request = LoginRequest(
            email = "",
            password = "password123"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `LoginRequest should fail for invalid email format`() {
        val request = LoginRequest(
            email = "invalid-email",
            password = "password123"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `LoginRequest should fail for missing password`() {
        val request = LoginRequest(
            email = "test@test.com",
            password = ""
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }

    @Test
    fun `RefreshTokenRequest should fail for blank token`() {
        val request = RefreshTokenRequest(refreshToken = "")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "refreshToken" })
    }

    @Test
    fun `LogoutRequest should fail for blank session ID`() {
        val request = LogoutRequest(sessionId = "")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "sessionId" })
    }

    @Test
    fun `VerifyEmailRequest should fail for blank token`() {
        val request = VerifyEmailRequest(token = "")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "token" })
    }

    @Test
    fun `ForgotPasswordRequest should fail for blank email`() {
        val request = ForgotPasswordRequest(email = "")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `ForgotPasswordRequest should fail for invalid email format`() {
        val request = ForgotPasswordRequest(email = "invalid-email")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `ResetPasswordRequest should fail for blank token`() {
        val request = ResetPasswordRequest(token = "", newPassword = "newpassword123")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "token" })
    }

    @Test
    fun `ResetPasswordRequest should fail for token too short`() {
        val request = ResetPasswordRequest(token = "short", newPassword = "newpassword123")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "token" })
    }

    @Test
    fun `ResetPasswordRequest should fail for missing new password`() {
        val request = ResetPasswordRequest(token = "a".repeat(32), newPassword = "")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "newPassword" })
    }

    @Test
    fun `ResetPasswordRequest should fail for new password too short`() {
        val request = ResetPasswordRequest(token = "a".repeat(32), newPassword = "short")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "newPassword" })
    }

    @Test
    fun `ResetPasswordRequest should fail for new password too long`() {
        val request = ResetPasswordRequest(token = "a".repeat(32), newPassword = "a".repeat(101))
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "newPassword" })
    }
}
