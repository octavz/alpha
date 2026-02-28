package com.alpha.domain.repository

import com.alpha.domain.entity.AppointmentEntity
import com.alpha.domain.enums.AppointmentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

interface AppointmentRepository : JpaRepository<AppointmentEntity, UUID> {
    @Query("SELECT a FROM AppointmentEntity a WHERE a.business.id = :businessId")
    fun findByBusinessId(@Param("businessId") businessId: UUID): List<AppointmentEntity>
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.customer.id = :customerId")
    fun findByCustomerId(@Param("customerId") customerId: UUID): List<AppointmentEntity>
    
    fun findByStatus(status: AppointmentStatus): List<AppointmentEntity>
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.business.id = :businessId AND a.appointmentDate = :appointmentDate")
    fun findByBusinessIdAndAppointmentDate(
        @Param("businessId") businessId: UUID,
        @Param("appointmentDate") appointmentDate: LocalDate
    ): List<AppointmentEntity>
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.business.id = :businessId AND a.appointmentDate = :appointmentDate AND a.status = :status")
    fun findByBusinessIdAndAppointmentDateAndStatus(
        @Param("businessId") businessId: UUID,
        @Param("appointmentDate") appointmentDate: LocalDate,
        @Param("status") status: AppointmentStatus
    ): List<AppointmentEntity>
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.business.id = :businessId AND a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate")
    fun findByBusinessIdAndDateRange(
        @Param("businessId") businessId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<AppointmentEntity>
    
    @Query("""
        SELECT a FROM AppointmentEntity a 
        WHERE a.business.id = :businessId 
        AND a.appointmentDate = :date
        AND a.status IN :statuses
        ORDER BY a.startTime
    """)
    fun findBusinessAppointmentsByDateAndStatus(
        @Param("businessId") businessId: UUID,
        @Param("date") date: LocalDate,
        @Param("statuses") statuses: List<AppointmentStatus>
    ): List<AppointmentEntity>
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.customer.id = :customerId ORDER BY a.appointmentDate DESC, a.startTime DESC")
    fun findCustomerAppointments(@Param("customerId") customerId: UUID, pageable: Pageable): Page<AppointmentEntity>
    
    @Query("SELECT a FROM AppointmentEntity a WHERE a.business.id = :businessId ORDER BY a.appointmentDate DESC, a.startTime DESC")
    fun findBusinessAppointments(@Param("businessId") businessId: UUID, pageable: Pageable): Page<AppointmentEntity>
}