package com.alpha.service

import com.alpha.domain.entity.AppointmentEntity
import com.alpha.domain.entity.BusinessEntity
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.AppointmentStatus
import com.alpha.domain.enums.UserRole
import com.alpha.domain.repository.*
import com.alpha.service.dto.*
import com.alpha.service.exception.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

@Service
@Transactional
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository,
    private val securityContextService: SecurityContextService
) {
    
    fun createAppointment(request: CreateAppointmentRequest): AppointmentDto {
        val business = businessRepository.findById(request.businessId)
            .orElseThrow { NotFoundException("Business not found") }
        
        val currentUserId = securityContextService.getCurrentUserId()
        val customer = userRepository.findById(currentUserId)
            .orElseThrow { NotFoundException("User not found") }
        
        // Check if business is active
        if (!business.isActive) {
            throw BusinessException("Business is not active")
        }
        
        // Check for conflicting appointments
        val conflictingAppointment = appointmentRepository.findByBusinessIdAndAppointmentDateAndStatus(
            businessId = business.id!!,
            appointmentDate = request.appointmentDate,
            status = AppointmentStatus.PENDING
        ).firstOrNull { it.startTime == request.startTime }
        
        if (conflictingAppointment != null) {
            throw BusinessException("Time slot is already booked")
        }
        
        val appointment = AppointmentEntity().apply {
            this.business = business
            this.customer = customer
            appointmentDate = request.appointmentDate
            startTime = request.startTime
            endTime = request.endTime
            customerName = request.customerName
            customerEmail = request.customerEmail
            status = AppointmentStatus.PENDING
            createdAt = OffsetDateTime.now()
            updatedAt = OffsetDateTime.now()
        }
        
        val savedAppointment = appointmentRepository.save(appointment)
        return AppointmentDto.fromEntity(savedAppointment)
    }
    
    fun updateAppointment(appointmentId: UUID, request: UpdateAppointmentRequest): AppointmentDto {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { NotFoundException("Appointment not found") }
        
        // Check permissions
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        val isOwner = appointment.customer?.id == currentUserId
        val isBusinessOwner = appointment.business?.user?.id == currentUserId
        val isAdmin = currentUserRole == UserRole.ADMIN
        
        if (!isOwner && !isBusinessOwner && !isAdmin) {
            throw AuthorizationException("You don't have permission to update this appointment")
        }
        
        request.appointmentDate?.let { appointment.appointmentDate = it }
        request.startTime?.let { appointment.startTime = it }
        request.endTime?.let { appointment.endTime = it }
        request.customerName?.let { appointment.customerName = it }
        request.customerEmail?.let { appointment.customerEmail = it }
        request.status?.let { appointment.status = it }
        
        appointment.updatedAt = OffsetDateTime.now()
        val updatedAppointment = appointmentRepository.save(appointment)
        return AppointmentDto.fromEntity(updatedAppointment)
    }
    
    fun getAppointment(appointmentId: UUID): AppointmentDto {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { NotFoundException("Appointment not found") }
        
        // Check permissions
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        val isOwner = appointment.customer?.id == currentUserId
        val isBusinessOwner = appointment.business?.user?.id == currentUserId
        val isAdmin = currentUserRole == UserRole.ADMIN
        
        if (!isOwner && !isBusinessOwner && !isAdmin) {
            throw AuthorizationException("You don't have permission to view this appointment")
        }
        
        return AppointmentDto.fromEntity(appointment)
    }
    
    fun searchAppointments(
        businessId: UUID?,
        customerId: UUID?,
        status: AppointmentStatus?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<AppointmentDto> {
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        // If no filters specified, return user's own appointments
        if (businessId == null && customerId == null && status == null && startDate == null && endDate == null) {
            if (currentUserRole == UserRole.ADMIN) {
                // Admin can see all appointments
                val appointments = appointmentRepository.findAll(pageable)
                return appointments.map { AppointmentDto.fromEntity(it) }
            } else {
                // Regular users see their own appointments
                val appointments = appointmentRepository.findCustomerAppointments(currentUserId, pageable)
                return appointments.map { AppointmentDto.fromEntity(it) }
            }
        }
        
        // Apply filters
        var filteredAppointments: List<AppointmentEntity> = emptyList()
        
        if (businessId != null) {
            filteredAppointments = appointmentRepository.findByBusinessId(businessId)
        } else if (customerId != null) {
            filteredAppointments = appointmentRepository.findByCustomerId(customerId)
        } else {
            filteredAppointments = appointmentRepository.findAll()
        }
        
        // Apply additional filters
        if (status != null) {
            filteredAppointments = filteredAppointments.filter { it.status == status }
        }
        
        if (startDate != null && endDate != null) {
            filteredAppointments = filteredAppointments.filter {
                it.appointmentDate != null && 
                it.appointmentDate!! >= startDate && 
                it.appointmentDate!! <= endDate
            }
        }
        
        // Check permissions for each appointment
        val authorizedAppointments = filteredAppointments.filter { appointment ->
            val isOwner = appointment.customer?.id == currentUserId
            val isBusinessOwner = appointment.business?.user?.id == currentUserId
            val isAdmin = currentUserRole == UserRole.ADMIN
            isOwner || isBusinessOwner || isAdmin
        }
        
        // Convert to page (simplified - in real implementation, this would be done at database level)
        val start = pageable.pageNumber * pageable.pageSize
        val end = minOf(start + pageable.pageSize, authorizedAppointments.size)
        val pageContent = authorizedAppointments.subList(start, end)
        
        return org.springframework.data.domain.PageImpl(
            pageContent.map { AppointmentDto.fromEntity(it) },
            pageable,
            authorizedAppointments.size.toLong()
        )
    }
    
    fun getBusinessAppointments(businessId: UUID, pageable: Pageable): Page<AppointmentDto> {
        val business = businessRepository.findById(businessId)
            .orElseThrow { NotFoundException("Business not found") }
        
        // Check if user owns the business or is admin
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        if (business.user?.id != currentUserId && currentUserRole != UserRole.ADMIN) {
            throw AuthorizationException("You don't have permission to view these appointments")
        }
        
        val appointments = appointmentRepository.findBusinessAppointments(businessId, pageable)
        return appointments.map { AppointmentDto.fromEntity(it) }
    }
    
    fun cancelAppointment(appointmentId: UUID): AppointmentDto {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { NotFoundException("Appointment not found") }
        
        // Check permissions
        val currentUserId = securityContextService.getCurrentUserId()
        val currentUserRole = securityContextService.getCurrentUserRole()
        
        val isOwner = appointment.customer?.id == currentUserId
        val isBusinessOwner = appointment.business?.user?.id == currentUserId
        val isAdmin = currentUserRole == UserRole.ADMIN
        
        if (!isOwner && !isBusinessOwner && !isAdmin) {
            throw AuthorizationException("You don't have permission to cancel this appointment")
        }
        
        appointment.status = AppointmentStatus.CANCELLED
        appointment.updatedAt = OffsetDateTime.now()
        val cancelledAppointment = appointmentRepository.save(appointment)
        return AppointmentDto.fromEntity(cancelledAppointment)
    }
    
    fun getBusinessAvailability(businessId: UUID, date: LocalDate): List<LocalTime> {
        val business = businessRepository.findById(businessId)
            .orElseThrow { NotFoundException("Business not found") }
        
        // Get existing appointments for the date
        val existingAppointments = appointmentRepository.findByBusinessIdAndAppointmentDate(
            businessId = businessId,
            appointmentDate = date
        ).filter { it.status == AppointmentStatus.PENDING || it.status == AppointmentStatus.CONFIRMED }
        
        // Generate available time slots (simplified - 9 AM to 5 PM, 30-minute slots)
        val availableSlots = mutableListOf<LocalTime>()
        var currentTime = LocalTime.of(9, 0)
        val endTime = LocalTime.of(17, 0)
        
        while (currentTime.isBefore(endTime)) {
            val isBooked = existingAppointments.any { 
                it.startTime == currentTime || 
                (it.startTime!!.isBefore(currentTime) && it.endTime!!.isAfter(currentTime))
            }
            
            if (!isBooked) {
                availableSlots.add(currentTime)
            }
            
            currentTime = currentTime.plusMinutes(30)
        }
        
        return availableSlots
    }
}