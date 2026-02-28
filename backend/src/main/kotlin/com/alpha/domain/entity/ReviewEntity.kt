package com.alpha.domain.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    name = "reviews",
    indexes = [
        Index(name = "reviews_business_id_idx", columnList = "business_id"),
        Index(name = "reviews_customer_id_idx", columnList = "customer_id"),
        Index(name = "reviews_rating_idx", columnList = "rating"),
        Index(name = "reviews_is_approved_idx", columnList = "is_approved")
    ]
)
data class ReviewEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    val business: BusinessEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: UserEntity,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    val appointment: AppointmentEntity? = null,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @Column(name = "title")
    val title: String? = null,

    @Column(name = "comment")
    val comment: String? = null,

    @Column(name = "is_approved", nullable = false)
    val isApproved: Boolean = false,

    @Column(name = "is_featured", nullable = false)
    val isFeatured: Boolean = false
) : BaseEntity() {
    @PreUpdate
    fun preUpdate() {
        updatedAt = OffsetDateTime.now()
    }
}