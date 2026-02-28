package com.alpha.domain.entity

import jakarta.persistence.*
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    name = "business_hours",
    indexes = [
        Index(name = "business_hours_business_id_idx", columnList = "business_id")
    ]
)
data class BusinessHoursEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    val business: BusinessEntity,

    @Column(name = "day_of_week", nullable = false)
    val dayOfWeek: Int, // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

    @Column(name = "open_time")
    val openTime: LocalTime? = null,

    @Column(name = "close_time")
    val closeTime: LocalTime? = null,

    @Column(name = "is_closed", nullable = false)
    val isClosed: Boolean = false
) : BaseEntity()