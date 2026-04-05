package com.alpha.service.dto

import com.alpha.domain.enums.AppointmentStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class CreateAppointmentRequest(
    @field:NotNull(message = "Business ID is required")
    val businessId: UUID,
    
    val serviceId: UUID? = null,
    
    @field:NotNull(message = "Appointment date is required")
    @field:FutureOrPresent(message = "Appointment date must be today or in the future")
    val appointmentDate: LocalDate,
    
    @field:NotNull(message = "Start time is required")
    val startTime: LocalTime,
    
    @field:NotNull(message = "End time is required")
    val endTime: LocalTime,
    
    @field:PositiveOrZero(message = "Service point number must be zero or positive")
    @field:Max(value = 999, message = "Service point number cannot exceed 999")
    val servicePointNumber: Int? = null,
    
    @field:NotBlank(message = "Customer name is required")
    @field:Size(min = 2, max = 255, message = "Customer name must be between 2 and 255 characters")
    val customerName: String,
    
    @field:NotBlank(message = "Customer email is required")
    @field:Email(message = "Invalid email format")
    @field:Size(max = 255, message = "Customer email cannot exceed 255 characters")
    val customerEmail: String,
    
    @field:Size(max = 20, message = "Customer phone cannot exceed 20 characters")
    val customerPhone: String? = null,
    
    @field:Size(max = 1000, message = "Customer notes cannot exceed 1000 characters")
    val customerNotes: String? = null
)

data class UpdateAppointmentRequest(
    @field:FutureOrPresent(message = "Appointment date must be today or in the future")
    val appointmentDate: LocalDate? = null,
    
    val startTime: LocalTime? = null,
    
    val endTime: LocalTime? = null,
    
    @field:PositiveOrZero(message = "Service point number must be zero or positive")
    @field:Max(value = 999, message = "Service point number cannot exceed 999")
    val servicePointNumber: Int? = null,
    
    @field:Size(min = 2, max = 255, message = "Customer name must be between 2 and 255 characters")
    val customerName: String? = null,
    
    @field:Email(message = "Invalid email format")
    @field:Size(max = 255, message = "Customer email cannot exceed 255 characters")
    val customerEmail: String? = null,
    
    @field:Size(max = 20, message = "Customer phone cannot exceed 20 characters")
    val customerPhone: String? = null,
    
    @field:Size(max = 1000, message = "Customer notes cannot exceed 1000 characters")
    val customerNotes: String? = null,
    
    val status: AppointmentStatus? = null,
    
    @field:Size(max = 500, message = "Cancelled reason cannot exceed 500 characters")
    val cancelledReason: String? = null
)

data class AppointmentResponse(
    val id: UUID,
    val businessId: UUID,
    val customerId: UUID,
    val serviceId: UUID?,
    
    // Appointment Details
    val appointmentDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val servicePointNumber: Int?,
    
    // Customer Information
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String?,
    val customerNotes: String?,
    
    // Status
    val status: AppointmentStatus,
    val cancelledAt: java.time.OffsetDateTime?,
    val cancelledReason: String?,
    
    // Metadata
    val createdAt: java.time.OffsetDateTime,
    val updatedAt: java.time.OffsetDateTime
)

data class AppointmentSearchRequest(
    val businessId: UUID? = null,
    val customerId: UUID? = null,
    val serviceId: UUID? = null,
    val status: AppointmentStatus? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "appointmentDate",
    val sortDirection: String = "desc"
)

data class AppointmentSearchResponse(
    val appointments: List<AppointmentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)

data class BusinessAvailabilityRequest(
    @field:NotNull(message = "Business ID is required")
    val businessId: UUID,
    
    @field:NotNull(message = "Date is required")
    val date: LocalDate,
    
    val serviceId: UUID? = null
)

data class TimeSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val available: Boolean,
    val servicePointNumber: Int? = null
)

data class BusinessAvailabilityResponse(
    val businessId: UUID,
    val date: LocalDate,
    val timeSlots: List<TimeSlot>,
    val servicePointsCount: Int
)