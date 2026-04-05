package com.alpha.service.dto

import com.alpha.domain.enums.VerificationStatus
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.math.BigDecimal
import java.util.*

@TestInstance(Lifecycle.PER_CLASS)
class BusinessDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `CreateBusinessRequest should fail for blank name`() {
        val request = CreateBusinessRequest(
            name = "",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID()
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateBusinessRequest should fail for name too short`() {
        val request = CreateBusinessRequest(
            name = "A",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID()
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateBusinessRequest should fail for name too long`() {
        val request = CreateBusinessRequest(
            name = "a".repeat(256),
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID()
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateBusinessRequest should fail for description too long`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            description = "a".repeat(1001)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "description" })
    }

    @Test
    fun `CreateBusinessRequest should fail for invalid email format`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            email = "invalid-email"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `CreateBusinessRequest should fail for email too long`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            email = "a".repeat(256) + "@test.com"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `CreateBusinessRequest should fail for phone too long`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            phone = "1".repeat(21)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "phone" })
    }

    @Test
    fun `CreateBusinessRequest should fail for website too long`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            website = "a".repeat(501)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "website" })
    }

    @Test
    fun `CreateBusinessRequest should fail for latitude too low`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            latitude = BigDecimal("-91")
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "latitude" })
    }

    @Test
    fun `CreateBusinessRequest should fail for latitude too high`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            latitude = BigDecimal("91")
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "latitude" })
    }

    @Test
    fun `CreateBusinessRequest should fail for longitude too low`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            longitude = BigDecimal("-181")
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "longitude" })
    }

    @Test
    fun `CreateBusinessRequest should fail for longitude too high`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            longitude = BigDecimal("181")
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "longitude" })
    }

    @Test
    fun `CreateBusinessRequest should fail for servicePointsCount negative`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            servicePointsCount = -1
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "servicePointsCount" })
    }

    @Test
    fun `CreateBusinessRequest should fail for servicePointsCount too high`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            servicePointsCount = 1001
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "servicePointsCount" })
    }

    @Test
    fun `CreateBusinessRequest should pass for valid input`() {
        val request = CreateBusinessRequest(
            name = "Test Business",
            categoryId = UUID.randomUUID(),
            regionId = UUID.randomUUID(),
            email = "test@business.com",
            phone = "1234567890"
        )
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `UpdateBusinessRequest should fail for name too short`() {
        val request = UpdateBusinessRequest(name = "A")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `UpdateBusinessRequest should fail for name too long`() {
        val request = UpdateBusinessRequest(name = "a".repeat(256))
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `UpdateBusinessRequest should pass for null values`() {
        val request = UpdateBusinessRequest()
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `NearbyBusinessRequest should fail for negative radius`() {
        val request = NearbyBusinessRequest(
            latitude = BigDecimal("0.0"),
            longitude = BigDecimal("0.0"),
            radius = -1.0
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "radius" })
    }
}
