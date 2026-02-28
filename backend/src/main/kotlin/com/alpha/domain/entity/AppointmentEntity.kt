package com.alpha.domain.entity

import com.alpha.domain.enums.AppointmentStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "appointments")
class AppointmentEntity : BaseEntity() {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    var business: BusinessEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: UserEntity? = null

    @Column(name = "appointment_date", nullable = false)
    var appointmentDate: LocalDate? = null

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime? = null

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime? = null

    @Column(name = "customer_name", nullable = false)
    var customerName: String? = null

    @Column(name = "customer_email", nullable = false)
    var customerEmail: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: AppointmentStatus = AppointmentStatus.PENDING
}