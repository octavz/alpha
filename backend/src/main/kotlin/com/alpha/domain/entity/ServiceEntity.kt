package com.alpha.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(
    name = "services",
    indexes = [
        Index(name = "services_business_id_idx", columnList = "business_id")
    ]
)
class ServiceEntity : BaseEntity() {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false, foreignKey = jakarta.persistence.ForeignKey(name = "fk_services_business"))
    var business: BusinessEntity? = null
    
    @Column(name = "name", nullable = false)
    var name: String = ""
    
    @Column(name = "description")
    var description: String? = null
    
    @Column(name = "duration_minutes", nullable = false)
    var durationMinutes: Int = 0
    
    @Column(name = "price", precision = 10, scale = 2)
    var price: BigDecimal? = null
    
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
    
    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
    
    // Relationships
    // Note: Appointment relationship removed as AppointmentEntity doesn't have service field
    // var appointments: MutableSet<AppointmentEntity> = mutableSetOf()
}