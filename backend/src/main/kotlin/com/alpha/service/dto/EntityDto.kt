package com.alpha.service.dto

import com.alpha.domain.entity.*
import com.alpha.domain.enums.AppointmentStatus
import com.alpha.domain.enums.VerificationStatus
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class BusinessDto(
    val id: UUID?,
    val name: String,
    val description: String?,
    val slug: String,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?,
    val phone: String?,
    val email: String?,
    val website: String?,
    val latitude: java.math.BigDecimal?,
    val longitude: java.math.BigDecimal?,
    val regionId: UUID?,
    val categoryId: UUID?,
    val userId: UUID?,
    val verificationStatus: VerificationStatus,
    val isActive: Boolean,
    val isFeatured: Boolean,
    val createdAt: java.time.OffsetDateTime?,
    val updatedAt: java.time.OffsetDateTime?
) {
    companion object {
        fun fromEntity(entity: BusinessEntity): BusinessDto {
            return BusinessDto(
                id = entity.id,
                name = entity.name ?: "",
                description = entity.description,
                slug = entity.slug ?: "",
                addressLine1 = entity.addressLine1,
                addressLine2 = entity.addressLine2,
                city = entity.city,
                state = entity.state,
                zipCode = entity.zipCode,
                country = entity.country,
                phone = entity.phone,
                email = entity.email,
                website = entity.website,
                latitude = entity.latitude,
                longitude = entity.longitude,
                regionId = entity.region?.id,
                categoryId = entity.category?.id,
                userId = entity.user?.id,
                verificationStatus = entity.verificationStatus,
                isActive = entity.isActive,
                isFeatured = entity.isFeatured,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}

data class AppointmentDto(
    val id: UUID?,
    val businessId: UUID?,
    val customerId: UUID?,
    val appointmentDate: LocalDate?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val customerName: String?,
    val customerEmail: String?,
    val status: AppointmentStatus,
    val createdAt: java.time.OffsetDateTime?,
    val updatedAt: java.time.OffsetDateTime?
) {
    companion object {
        fun fromEntity(entity: AppointmentEntity): AppointmentDto {
            return AppointmentDto(
                id = entity.id,
                businessId = entity.business?.id,
                customerId = entity.customer?.id,
                appointmentDate = entity.appointmentDate,
                startTime = entity.startTime,
                endTime = entity.endTime,
                customerName = entity.customerName,
                customerEmail = entity.customerEmail,
                status = entity.status,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}