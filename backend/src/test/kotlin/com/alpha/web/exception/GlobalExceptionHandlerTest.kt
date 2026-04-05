package com.alpha.web.exception

import com.alpha.service.exception.*
import com.alpha.web.dto.ApiResponse
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Path
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest
import java.util.Collections

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()
    private val request = mockk<WebRequest>()

    @Test
    fun `handleNotFoundException should return 404`() {
        val ex = NotFoundException("Resource not found")

        val response = handler.handleNotFoundException(ex, request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.success)
        assertEquals("NOT_FOUND", response.body!!.error?.code)
        assertEquals("Resource not found", response.body!!.error?.message)
    }

    @Test
    fun `handleConflictException should return 409`() {
        val ex = ConflictException("Already exists")

        val response = handler.handleConflictException(ex, request)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("CONFLICT", response.body!!.error?.code)
        assertEquals("Already exists", response.body!!.error?.message)
    }

    @Test
    fun `handleValidationException should return 400`() {
        val ex = ValidationException("Invalid input")

        val response = handler.handleValidationException(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("VALIDATION_ERROR", response.body!!.error?.code)
        assertEquals("Invalid input", response.body!!.error?.message)
    }

    @Test
    fun `handleAuthenticationException should return 401`() {
        val ex = AuthenticationException("Invalid credentials")

        val response = handler.handleAuthenticationException(ex, request)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("AUTHENTICATION_ERROR", response.body!!.error?.code)
        assertEquals("Invalid credentials", response.body!!.error?.message)
    }

    @Test
    fun `handleForbiddenException should return 403`() {
        val ex = ForbiddenException("Access denied")

        val response = handler.handleForbiddenException(ex, request)

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals("FORBIDDEN", response.body!!.error?.code)
        assertEquals("Access denied", response.body!!.error?.message)
    }

    @Test
    fun `handleServiceException should return exception status`() {
        val ex = BusinessException("Business error")

        val response = handler.handleServiceException(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("BusinessException", response.body!!.error?.code)
        assertEquals("Business error", response.body!!.error?.message)
    }

    @Test
    fun `handleValidationExceptions should return field errors`() {
        val bindingResult = mockk<BindingResult>()
        val fieldError = FieldError("object", "fieldName", "must not be null")
        every { bindingResult.allErrors } returns listOf(fieldError)

        val ex = mockk<MethodArgumentNotValidException>()
        every { ex.bindingResult } returns bindingResult

        val response = handler.handleValidationExceptions(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body!!.error?.message?.contains("fieldName") == true)
    }

    @Test
    fun `handleValidationExceptions should return multiple errors`() {
        val bindingResult = mockk<BindingResult>()
        val error1 = FieldError("object", "field1", "error1")
        val error2 = FieldError("object", "field2", "error2")
        every { bindingResult.allErrors } returns listOf(error1, error2)

        val ex = mockk<MethodArgumentNotValidException>()
        every { ex.bindingResult } returns bindingResult

        val response = handler.handleValidationExceptions(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body!!.error?.message?.contains("Multiple validation errors") == true)
    }

    @Test
    fun `handleConstraintViolationException should return constraint errors`() {
        val violation = mockk<ConstraintViolation<Any>>()
        val path = mockk<Path>()
        every { violation.propertyPath } returns path
        every { path.toString() } returns "fieldName"
        every { violation.message } returns "must not be null"

        val ex = ConstraintViolationException(setOf(violation))

        val response = handler.handleConstraintViolationException(ex, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("CONSTRAINT_VIOLATION", response.body!!.error?.code)
    }

    @Test
    fun `handleGenericException should return 500`() {
        val ex = RuntimeException("Unexpected error")

        val response = handler.handleGenericException(ex, request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("INTERNAL_SERVER_ERROR", response.body!!.error?.code)
        assertEquals("An unexpected error occurred", response.body!!.error?.message)
    }

    @Test
    fun `handleNotFoundException with default message`() {
        val ex = NotFoundException("Resource not found")

        val response = handler.handleNotFoundException(ex, request)

        assertEquals("Resource not found", response.body!!.error?.message)
    }

    @Test
    fun `handleConflictException with default message`() {
        val ex = ConflictException("Conflict occurred")

        val response = handler.handleConflictException(ex, request)

        assertEquals("Conflict occurred", response.body!!.error?.message)
    }

    @Test
    fun `handleValidationException with default message`() {
        val ex = ValidationException("Validation failed")

        val response = handler.handleValidationException(ex, request)

        assertEquals("Validation failed", response.body!!.error?.message)
    }

    @Test
    fun `handleAuthenticationException with default message`() {
        val ex = AuthenticationException("Authentication failed")

        val response = handler.handleAuthenticationException(ex, request)

        assertEquals("Authentication failed", response.body!!.error?.message)
    }

    @Test
    fun `handleForbiddenException with default message`() {
        val ex = ForbiddenException("Access forbidden")

        val response = handler.handleForbiddenException(ex, request)

        assertEquals("Access forbidden", response.body!!.error?.message)
    }
}
