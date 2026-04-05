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
class RegionDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `CreateRegionRequest should fail for blank name`() {
        val request = CreateRegionRequest(
            name = "",
            code = "US-NY",
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateRegionRequest should fail for name too short`() {
        val request = CreateRegionRequest(
            name = "A",
            code = "US-NY",
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateRegionRequest should fail for name too long`() {
        val request = CreateRegionRequest(
            name = "a".repeat(256),
            code = "US-NY",
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateRegionRequest should fail for blank code`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "",
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "code" })
    }

    @Test
    fun `CreateRegionRequest should fail for code too short`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "A",
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "code" })
    }

    @Test
    fun `CreateRegionRequest should fail for code too long`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "a".repeat(11),
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "code" })
    }

    @Test
    fun `CreateRegionRequest should fail for blank country`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "US-NY",
            country = "",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "country" })
    }

    @Test
    fun `CreateRegionRequest should fail for country too long`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "US-NY",
            country = "a".repeat(101),
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "country" })
    }

    @Test
    fun `CreateRegionRequest should fail for blank timezone`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "US-NY",
            country = "USA",
            timezone = ""
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "timezone" })
    }

    @Test
    fun `CreateRegionRequest should fail for timezone too long`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "US-NY",
            country = "USA",
            timezone = "a".repeat(51)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "timezone" })
    }

    @Test
    fun `CreateRegionRequest should pass for valid input`() {
        val request = CreateRegionRequest(
            name = "New York",
            code = "US-NY",
            country = "USA",
            timezone = "America/New_York"
        )
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `UpdateRegionRequest should fail for name too short`() {
        val request = UpdateRegionRequest(name = "A")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `UpdateRegionRequest should fail for name too long`() {
        val request = UpdateRegionRequest(name = "a".repeat(256))
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `UpdateRegionRequest should pass for null values`() {
        val request = UpdateRegionRequest()
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }
}
