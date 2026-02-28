package com.alpha.web.controller

import com.alpha.web.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Health check endpoints")
class HealthController {
    
    @GetMapping
    @Operation(summary = "Health check", description = "Check if the API is running")
    fun healthCheck(): ResponseEntity<ApiResponse<HealthResponse>> {
        val response = HealthResponse(
            status = "healthy",
            timestamp = Instant.now().toString(),
            message = "Alpha API is running"
        )
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }
    
    data class HealthResponse(
        val status: String,
        val timestamp: String,
        val message: String
    )
}