package com.alpha.web.controller

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.enums.UserRole
import com.alpha.service.dto.LoginRequest
import com.alpha.service.dto.RegisterRequest
import tools.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@AutoConfigureMockMvc
class AuthControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @Test
    fun `register should return success response`() {
        val uniqueEmail = "test-${UUID.randomUUID()}@example.com"
        val request = RegisterRequest(
            email = uniqueEmail,
            password = "password123",
            name = "Test User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.user.email").value(uniqueEmail))
            .andExpect(jsonPath("$.data.user.name").value("Test User"))
            .andExpect(jsonPath("$.data.user.phone").value("1234567890"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.refreshToken").exists())
            .andExpect(jsonPath("$.data.sessionId").exists())
    }

    @Test
    fun `register should return bad request for invalid data`() {
        val request = RegisterRequest(
            email = "",
            password = "123",
            name = "",
            phone = "",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `register should fail when email already exists`() {
        val email = "duplicate-${UUID.randomUUID()}@example.com"
        val firstRequest = RegisterRequest(
            email = email,
            password = "password123",
            name = "First User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(firstRequest))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )

        val secondRequest = RegisterRequest(
            email = email,
            password = "password456",
            name = "Second User",
            phone = "0987654321",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(secondRequest))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `login should return unauthorized for invalid credentials`() {
        val request = LoginRequest(
            email = "nonexistent-${UUID.randomUUID()}@example.com",
            password = "wrongPassword"
        )

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `health endpoint should return success`() {
        mockMvc.perform(
            get("/api/v1/health")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.status").value("healthy"))
            .andExpect(jsonPath("$.data.timestamp").exists())
    }

    @Test
    fun `login should return success with valid credentials`() {
        val email = "login-test-${UUID.randomUUID()}@example.com"
        val password = "password123"
        
        val registerRequest = RegisterRequest(
            email = email,
            password = password,
            name = "Login Test User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )

        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.refreshToken").exists())
    }

    @Test
    fun `login should return unauthorized for wrong password`() {
        val email = "wrong-pass-${UUID.randomUUID()}@example.com"
        
        val registerRequest = RegisterRequest(
            email = email,
            password = "correctpassword",
            name = "Wrong Pass User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )

        val loginRequest = LoginRequest(
            email = email,
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `register should return bad request for invalid email`() {
        val request = RegisterRequest(
            email = "not-an-email",
            password = "password123",
            name = "Test User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `register should return bad request for short password`() {
        val request = RegisterRequest(
            email = "test-${UUID.randomUUID()}@example.com",
            password = "123",
            name = "Test User",
            phone = "1234567890",
            role = UserRole.CUSTOMER
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .header("X-Forwarded-For", "127.0.0.1")
                .header("User-Agent", "Test Browser")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `forgot-password should return not found for non-existent email`() {
        val request = mapOf("email" to "nonexistent-${UUID.randomUUID()}@example.com")

        mockMvc.perform(
            post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `verify-email should return bad request for invalid token`() {
        val request = mapOf("token" to "invalid-token")

        mockMvc.perform(
            post("/api/v1/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `reset-password should return bad request for invalid token`() {
        val request = mapOf("token" to "invalid-token", "newPassword" to "newpass123")

        mockMvc.perform(
            post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `refresh should return unauthorized for invalid token`() {
        val request = mapOf("refreshToken" to "invalid-token")

        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }
}
