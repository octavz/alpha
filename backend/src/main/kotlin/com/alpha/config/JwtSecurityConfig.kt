package com.alpha.config

import com.alpha.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class JwtSecurityConfig(
    private val jwtService: JwtService,
    private val logoutHandler: LogoutHandler
) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/health/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/health"
                    ).permitAll()
                    // Business endpoints - partially public
                    .requestMatchers(
                        "/api/v1/businesses/search",
                        "/api/v1/businesses/{id}",
                        "/api/v1/businesses/slug/{slug}",
                        "/api/v1/businesses/region/{regionId}",
                        "/api/v1/businesses/category/{categoryId}",
                        "/api/v1/businesses/featured",
                        "/api/v1/regions/**",
                        "/api/v1/categories/**"
                    ).permitAll()
                    // All other endpoints require authentication
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .logout { logout ->
                logout.logoutUrl("/api/v1/auth/logout")
                logout.addLogoutHandler(logoutHandler)
                logout.logoutSuccessHandler { _, response, _ ->
                    response.status = HttpServletResponse.SC_OK
                }
            }
        
        return http.build()
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:5173",
                "http://localhost:5179",
                "http://localhost:3000"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            allowedHeaders = listOf("Content-Type", "Authorization", "X-Requested-With")
            allowCredentials = true
            maxAge = 3600L
        }
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/api/**", configuration)
        source.registerCorsConfiguration("/v3/api-docs/**", CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET")
        })
        source.registerCorsConfiguration("/swagger-ui/**", CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET")
        })
        
        return source
    }
    
    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtService)
    }
    
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        
        val token = authHeader.substring(7)
        
        try {
            val decodedJWT = jwtService.validateToken(token)
            val userId = jwtService.extractUserId(token)
            val role = jwtService.extractUserRole(token)
            
            // Create authentication token
            val authentication = JwtAuthenticationToken(userId.toString(), role.toString(), token)
            SecurityContextHolder.getContext().authentication = authentication
            
        } catch (e: Exception) {
            // Token validation failed - continue without authentication
            logger.debug("JWT validation failed: ${e.message}")
        }
        
        filterChain.doFilter(request, response)
    }
}

data class JwtAuthenticationToken(
    private val userId: String,
    private val role: String,
    private val token: String
) : org.springframework.security.core.Authentication {
    
    override fun getAuthorities(): MutableCollection<org.springframework.security.core.GrantedAuthority> {
        return mutableListOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_$role"))
    }
    
    override fun getCredentials(): Any = token
    
    override fun getDetails(): Any? = null
    
    override fun getPrincipal(): Any = userId
    
    override fun isAuthenticated(): Boolean = true
    
    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw UnsupportedOperationException("JWT tokens are always authenticated")
    }
    
    override fun getName(): String = userId
}

@Component
class LogoutService : LogoutHandler {
    
    private val logger = LoggerFactory.getLogger(LogoutService::class.java)
    
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: org.springframework.security.core.Authentication?
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            // In a real implementation, you would blacklist the token here
            logger.info("User logged out with token: ${token.take(10)}...")
        }
        
        SecurityContextHolder.clearContext()
    }
}