package com.alpha.service.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.util.*

@TestInstance(Lifecycle.PER_CLASS)
class CategoryDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setup() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `CreateCategoryRequest should fail for blank name`() {
        val request = CreateCategoryRequest(
            name = ""
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateCategoryRequest should fail for name too short`() {
        val request = CreateCategoryRequest(
            name = "A"
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateCategoryRequest should fail for name too long`() {
        val request = CreateCategoryRequest(
            name = "a".repeat(256)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `CreateCategoryRequest should fail for description too long`() {
        val request = CreateCategoryRequest(
            name = "Test Category",
            description = "a".repeat(501)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "description" })
    }

    @Test
    fun `CreateCategoryRequest should fail for icon too long`() {
        val request = CreateCategoryRequest(
            name = "Test Category",
            icon = "a".repeat(501)
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "icon" })
    }

    @Test
    fun `CreateCategoryRequest should fail for sortOrder negative`() {
        val request = CreateCategoryRequest(
            name = "Test Category",
            sortOrder = -1
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "sortOrder" })
    }

    @Test
    fun `CreateCategoryRequest should fail for sortOrder too high`() {
        val request = CreateCategoryRequest(
            name = "Test Category",
            sortOrder = 10000
        )
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "sortOrder" })
    }

    @Test
    fun `CreateCategoryRequest should pass for valid input`() {
        val request = CreateCategoryRequest(
            name = "Test Category",
            description = "A test category",
            icon = "icon.png"
        )
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }

    @Test
    fun `UpdateCategoryRequest should fail for name too short`() {
        val request = UpdateCategoryRequest(name = "A")
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `UpdateCategoryRequest should fail for name too long`() {
        val request = UpdateCategoryRequest(name = "a".repeat(256))
        val violations = validator.validate(request)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `UpdateCategoryRequest should pass for null values`() {
        val request = UpdateCategoryRequest()
        val violations = validator.validate(request)
        assertEquals(0, violations.size)
    }
}
