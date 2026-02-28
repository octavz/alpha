package com.alpha.domain.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(
    name = "sessions",
    indexes = [
        Index(name = "sessions_user_id_idx", columnList = "user_id"),
        Index(name = "sessions_token_idx", columnList = "token"),
        Index(name = "sessions_refresh_token_idx", columnList = "refresh_token")
    ]
)
data class SessionEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @Column(name = "token")
    var token: String? = null,

    @Column(name = "refresh_token", nullable = false)
    var refreshToken: String,

    @Column(name = "user_agent")
    var userAgent: String? = null,

    @Column(name = "ip_address")
    var ipAddress: String? = null,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: OffsetDateTime
) : BaseEntity()