package com.alpha.domain.converter

import com.alpha.domain.enums.UserRole
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class UserRoleConverter : AttributeConverter<UserRole, String> {
    
    override fun convertToDatabaseColumn(attribute: UserRole?): String {
        return attribute?.name?.lowercase() ?: "customer"
    }
    
    override fun convertToEntityAttribute(dbData: String?): UserRole {
        return when (dbData?.lowercase()) {
            "customer" -> UserRole.CUSTOMER
            "business_admin" -> UserRole.BUSINESS_ADMIN
            "business_staff" -> UserRole.BUSINESS_STAFF
            "admin" -> UserRole.ADMIN
            else -> UserRole.CUSTOMER
        }
    }
}