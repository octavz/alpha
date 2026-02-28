package com.alpha.web.controller

import com.alpha.domain.enums.AppointmentStatus
import com.alpha.service.AppointmentService
import com.alpha.service.dto.*
import com.alpha.web.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Appointment management endpoints")
class AppointmentController(
    private val appointmentService: AppointmentService
) {
    
    @PostMapping
    @Operation(summary = "Create a new appointment")
    fun createAppointment(
        @Valid @RequestBody request: CreateAppointmentRequest
    ): ResponseEntity<ApiResponse<AppointmentDto>> {
        val appointment = appointmentService.createAppointment(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(appointment))
    }
    
    @PutMapping("/{appointmentId}")
    @Operation(summary = "Update appointment")
    fun updateAppointment(
        @PathVariable appointmentId: UUID,
        @Valid @RequestBody request: UpdateAppointmentRequest
    ): ResponseEntity<ApiResponse<AppointmentDto>> {
        val appointment = appointmentService.updateAppointment(appointmentId, request)
        return ResponseEntity.ok(ApiResponse.success(appointment))
    }
    
    @GetMapping("/{appointmentId}")
    @Operation(summary = "Get appointment by ID")
    fun getAppointment(
        @PathVariable appointmentId: UUID
    ): ResponseEntity<ApiResponse<AppointmentDto>> {
        val appointment = appointmentService.getAppointment(appointmentId)
        return ResponseEntity.ok(ApiResponse.success(appointment))
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search appointments")
    fun searchAppointments(
        @RequestParam(required = false) businessId: UUID?,
        @RequestParam(required = false) customerId: UUID?,
        @RequestParam(required = false) status: AppointmentStatus?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<AppointmentDto>>> {
        val result = appointmentService.searchAppointments(
            businessId, customerId, status, startDate, endDate, pageable
        )
        return ResponseEntity.ok(ApiResponse.success(result))
    }
    
    @GetMapping("/business/{businessId}")
    @Operation(summary = "Get business appointments")
    fun getBusinessAppointments(
        @PathVariable businessId: UUID,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<AppointmentDto>>> {
        val appointments = appointmentService.getBusinessAppointments(businessId, pageable)
        return ResponseEntity.ok(ApiResponse.success(appointments))
    }
    
    @PostMapping("/{appointmentId}/cancel")
    @Operation(summary = "Cancel appointment")
    fun cancelAppointment(
        @PathVariable appointmentId: UUID
    ): ResponseEntity<ApiResponse<AppointmentDto>> {
        val appointment = appointmentService.cancelAppointment(appointmentId)
        return ResponseEntity.ok(ApiResponse.success(appointment))
    }
    
    @GetMapping("/business/{businessId}/availability")
    @Operation(summary = "Get business availability")
    fun getBusinessAvailability(
        @PathVariable businessId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<ApiResponse<List<String>>> {
        val availability = appointmentService.getBusinessAvailability(businessId, date)
        val timeStrings = availability.map { it.toString() }
        return ResponseEntity.ok(ApiResponse.success(timeStrings))
    }
}