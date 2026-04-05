package com.alpha.service.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.math.BigDecimal

@TestInstance(Lifecycle.PER_CLASS)
class AppointmentDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `CreateAppointmentRequest should fail for past appointmentDate`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().minusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = "test@email.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "appointmentDate" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for servicePointNumber negative`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            servicePointNumber = -1,
            customerName = "Test Customer",
            customerEmail = "test@email.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "servicePointNumber" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for servicePointNumber too high`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            servicePointNumber = 1000,
            customerName = "Test Customer",
            customerEmail = "test@email.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "servicePointNumber" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for blank customerName`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "",
            customerEmail = "test@email.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerName" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for customerName too short`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "A",
            customerEmail = "test@email.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerName" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for customerName too long`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "a".repeat(256),
            customerEmail = "test@email.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerName" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for blank customerEmail`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = ""
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerEmail" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for invalid customerEmail format`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = "invalid-email"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerEmail" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for customerEmail too long`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = "a".repeat(256) + "@test.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerEmail" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for customerPhone too long`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = "test@email.com",
            customerPhone = "1".repeat(21)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerPhone" })
    }

    @Test
    fun `CreateAppointmentRequest should fail for customerNotes too long`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = "test@email.com",
            customerNotes = "a".repeat(1001)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "customerNotes" })
    }

    @Test
    fun `CreateAppointmentRequest should pass for valid input`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            customerName = "Test Customer",
            customerEmail = "test@email.com",
            customerPhone = "1234567890",
            customerNotes = "Some notes"
        )
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `UpdateAppointmentRequest should fail for past appointmentDate`() {
        val request = UpdateAppointmentRequest(
            appointmentDate = LocalDate.now().minusDays(1)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "appointmentDate" })
    }

    @Test
    fun `UpdateAppointmentRequest should pass for null values`() {
        val request = UpdateAppointmentRequest()
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }
}
