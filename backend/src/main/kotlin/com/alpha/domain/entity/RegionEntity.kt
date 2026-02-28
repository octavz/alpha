package com.alpha.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "regions",
    indexes = [
        Index(name = "regions_code_idx", columnList = "code"),
        Index(name = "regions_country_idx", columnList = "country")
    ]
)
class RegionEntity : BaseEntity() {
    
    @Column(name = "name", nullable = false)
    var name: String = ""
    
    @Column(name = "code", unique = true, nullable = false)
    var code: String = ""
    
    @Column(name = "country", nullable = false)
    var country: String = ""
    
    @Column(name = "timezone", nullable = false)
    var timezone: String = ""
    
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
    
    // Relationships
    @OneToMany(mappedBy = "region", cascade = [CascadeType.ALL], orphanRemoval = true)
    var users: MutableSet<UserEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "region", cascade = [CascadeType.ALL], orphanRemoval = true)
    var businesses: MutableSet<BusinessEntity> = mutableSetOf()
}