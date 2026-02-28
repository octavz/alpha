package com.alpha.domain.entity

import com.alpha.domain.enums.UserRole
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "users_email_idx", columnList = "email"),
        Index(name = "users_google_id_idx", columnList = "google_id"),
        Index(name = "users_role_idx", columnList = "role"),
        Index(name = "users_region_id_idx", columnList = "region_id")
    ]
)
class UserEntity : BaseEntity() {
    
    @Column(name = "email", unique = true, nullable = false)
    var email: String = ""
    
    @Column(name = "password_hash")
    var passwordHash: String? = null
    
    @Column(name = "google_id", unique = true)
    var googleId: String? = null
    
    @Column(name = "name")
    var name: String? = null
    
    @Column(name = "phone")
    var phone: String? = null
    
    @Column(name = "role", nullable = false)
    var role: UserRole = UserRole.CUSTOMER
    
    @Column(name = "email_verified", nullable = false)
    var emailVerified: Boolean = false
    
    @Column(name = "avatar_url")
    var avatarUrl: String? = null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", foreignKey = jakarta.persistence.ForeignKey(name = "fk_users_region"))
    var region: RegionEntity? = null
    
    @Column(name = "is_banned", nullable = false)
    var isBanned: Boolean = false
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sessions: MutableSet<SessionEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var emailVerifications: MutableSet<EmailVerificationEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var passwordResets: MutableSet<PasswordResetEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var businesses: MutableSet<BusinessEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var appointments: MutableSet<AppointmentEntity> = mutableSetOf()
    
    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var reviews: MutableSet<ReviewEntity> = mutableSetOf()
}