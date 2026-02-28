package com.alpha.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.util.UUID

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: OffsetDateTime? = null
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime? = null
    
    // Helper property for non-null ID access (use after entity is saved)
    val requiredId: UUID
        get() = id ?: throw IllegalStateException("Entity ID is null")
    
    val requiredCreatedAt: OffsetDateTime
        get() = createdAt ?: OffsetDateTime.now()
    
    val requiredUpdatedAt: OffsetDateTime
        get() = updatedAt ?: OffsetDateTime.now()
}