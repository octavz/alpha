package com.alpha.domain.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    name = "password_resets",
    indexes = [
        Index(name = "password_resets_user_id_idx", columnList = "user_id"),
        Index(name = "password_resets_token_idx", columnList = "token")
    ]
)
data class PasswordResetEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "token", nullable = false)
    val token: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: OffsetDateTime
) : BaseEntity()