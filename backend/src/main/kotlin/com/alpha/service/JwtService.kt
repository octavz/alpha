package com.alpha.service

import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService {
    
    @Value("\${app.jwt.secret}")
    private lateinit var jwtSecret: String
    
    @Value("\${app.jwt.access-expiration:900}") // 15 minutes default
    private var accessExpiration: Long = 900
    
    @Value("\${app.jwt.refresh-expiration:604800}") // 7 days default
    private var refreshExpiration: Long = 604800
    
    fun generateAccessToken(user: UserEntity): String {
        return JWT.create()
            .withSubject(user.id.toString())
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withClaim("emailVerified", user.emailVerified)
            .withIssuedAt(Date.from(Instant.now()))
            .withExpiresAt(Date.from(Instant.now().plusSeconds(accessExpiration)))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
    
    fun generateRefreshToken(user: UserEntity): String {
        return JWT.create()
            .withSubject(user.id.toString())
            .withClaim("type", "refresh")
            .withIssuedAt(Date.from(Instant.now()))
            .withExpiresAt(Date.from(Instant.now().plusSeconds(refreshExpiration)))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
    
    fun validateToken(token: String): DecodedJWT {
        return try {
            JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token)
        } catch (e: JWTVerificationException) {
            throw com.alpha.service.exception.AuthenticationException("Invalid token")
        }
    }
    
    fun extractUserId(token: String): UUID {
        val decodedJWT = validateToken(token)
        return UUID.fromString(decodedJWT.subject)
    }
    
    fun extractUserRole(token: String): UserRole {
        val decodedJWT = validateToken(token)
        val role = decodedJWT.getClaim("role").asString()
        return UserRole.valueOf(role)
    }
    
    fun isTokenExpired(token: String): Boolean {
        return try {
            val decodedJWT = validateToken(token)
            decodedJWT.expiresAt.before(Date())
        } catch (e: Exception) {
            true
        }
    }
}