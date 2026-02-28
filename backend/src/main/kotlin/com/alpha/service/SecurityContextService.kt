package com.alpha.service

import com.alpha.domain.enums.UserRole
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityContextService {
    
    fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw com.alpha.service.exception.AuthenticationException("User not authenticated")
        }
        
        val principal = authentication.principal
        return when (principal) {
            is String -> UUID.fromString(principal)
            else -> throw com.alpha.service.exception.AuthenticationException("Invalid authentication principal")
        }
    }
    
    fun getCurrentUserRole(): UserRole {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw com.alpha.service.exception.AuthenticationException("User not authenticated")
        }
        
        val authorities = authentication.authorities
        val roleAuthority = authorities.firstOrNull { it.authority?.startsWith("ROLE_") == true }
            ?: throw com.alpha.service.exception.AuthenticationException("User role not found")
        
        val roleName = roleAuthority.authority?.substring(5) // Remove "ROLE_" prefix
            ?: throw com.alpha.service.exception.AuthenticationException("Invalid role authority")
        return UserRole.valueOf(roleName)
    }
    
    fun isCurrentUserAdmin(): Boolean {
        return try {
            getCurrentUserRole() == UserRole.ADMIN
        } catch (e: Exception) {
            false
        }
    }
    
    fun isCurrentUserBusinessAdmin(): Boolean {
        return try {
            val role = getCurrentUserRole()
            role == UserRole.BUSINESS_ADMIN || role == UserRole.ADMIN
        } catch (e: Exception) {
            false
        }
    }
    
    fun isCurrentUserBusinessStaff(): Boolean {
        return try {
            val role = getCurrentUserRole()
            role == UserRole.BUSINESS_STAFF || role == UserRole.BUSINESS_ADMIN || role == UserRole.ADMIN
        } catch (e: Exception) {
            false
        }
    }
    
    fun getCurrentUserEmail(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication?.details is Map<*, *>) {
            val details = authentication.details as Map<*, *>
            return details["email"] as? String
        }
        return null
    }
}