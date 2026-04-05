package com.alpha.domain.converter

import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserRoleConverterTest {

    private val converter = UserRoleConverter()

    @Test
    fun `convertToEntityAttribute should return CUSTOMER for customer`() {
        assertEquals(UserRole.CUSTOMER, converter.convertToEntityAttribute("customer"))
    }

    @Test
    fun `convertToEntityAttribute should return BUSINESS_ADMIN for business_admin`() {
        assertEquals(UserRole.BUSINESS_ADMIN, converter.convertToEntityAttribute("business_admin"))
    }

    @Test
    fun `convertToEntityAttribute should return BUSINESS_STAFF for business_staff`() {
        assertEquals(UserRole.BUSINESS_STAFF, converter.convertToEntityAttribute("business_staff"))
    }

    @Test
    fun `convertToEntityAttribute should return ADMIN for admin`() {
        assertEquals(UserRole.ADMIN, converter.convertToEntityAttribute("admin"))
    }

    @Test
    fun `convertToEntityAttribute should return CUSTOMER for unknown string`() {
        assertEquals(UserRole.CUSTOMER, converter.convertToEntityAttribute("unknown"))
    }

    @Test
    fun `convertToEntityAttribute should return CUSTOMER for null`() {
        assertEquals(UserRole.CUSTOMER, converter.convertToEntityAttribute(null))
    }

    @Test
    fun `convertToDatabaseColumn should return lowercase name`() {
        assertEquals("customer", converter.convertToDatabaseColumn(UserRole.CUSTOMER))
        assertEquals("business_admin", converter.convertToDatabaseColumn(UserRole.BUSINESS_ADMIN))
        assertEquals("business_staff", converter.convertToDatabaseColumn(UserRole.BUSINESS_STAFF))
        assertEquals("admin", converter.convertToDatabaseColumn(UserRole.ADMIN))
    }

    @Test
    fun `convertToDatabaseColumn should return customer for null`() {
        assertEquals("customer", converter.convertToDatabaseColumn(null))
    }
}
