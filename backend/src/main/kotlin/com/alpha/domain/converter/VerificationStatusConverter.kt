package com.alpha.domain.converter

import com.alpha.domain.enums.VerificationStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class VerificationStatusConverter : AttributeConverter<VerificationStatus, String> {
    
    override fun convertToDatabaseColumn(attribute: VerificationStatus?): String? {
        return attribute?.name?.lowercase()
    }
    
    override fun convertToEntityAttribute(dbData: String?): VerificationStatus? {
        if (dbData == null) return null
        return try {
            VerificationStatus.valueOf(dbData.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
