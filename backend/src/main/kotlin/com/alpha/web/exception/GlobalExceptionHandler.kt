package com.alpha.web.exception

import com.alpha.service.exception.*
import com.alpha.web.dto.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(ex: ServiceException, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Service exception: ${ex.message}", ex)
        return ResponseEntity
            .status(ex.status)
            .body(ApiResponse.error(
                code = ex.javaClass.simpleName,
                message = ex.message ?: "Service error",
                statusCode = ex.status.value()
            ))
    }
    
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Authentication exception: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(
                code = "AUTHENTICATION_ERROR",
                message = ex.message ?: "Authentication failed",
                statusCode = HttpStatus.UNAUTHORIZED.value()
            ))
    }
    
    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Validation exception: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                code = "VALIDATION_ERROR",
                message = ex.message ?: "Validation failed",
                statusCode = HttpStatus.BAD_REQUEST.value()
            ))
    }
    
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Not found exception: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(
                code = "NOT_FOUND",
                message = ex.message ?: "Resource not found",
                statusCode = HttpStatus.NOT_FOUND.value()
            ))
    }
    
    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(ex: ConflictException, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Conflict exception: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(
                code = "CONFLICT",
                message = ex.message ?: "Conflict occurred",
                statusCode = HttpStatus.CONFLICT.value()
            ))
    }
    
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Forbidden exception: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(
                code = "FORBIDDEN",
                message = ex.message ?: "Access forbidden",
                statusCode = HttpStatus.FORBIDDEN.value()
            ))
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Any>> {
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            "$fieldName: $errorMessage"
        }
        
        val errorMessage = if (errors.size == 1) errors[0] else "Multiple validation errors: ${errors.joinToString(", ")}"
        
        logger.warn("Validation errors: $errorMessage")
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                code = "VALIDATION_ERROR",
                message = errorMessage,
                statusCode = HttpStatus.BAD_REQUEST.value()
            ))
    }
    
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Any>> {
        val errors = ex.constraintViolations.map { violation ->
            "${violation.propertyPath}: ${violation.message}"
        }
        
        val errorMessage = if (errors.size == 1) errors[0] else "Multiple constraint violations: ${errors.joinToString(", ")}"
        
        logger.warn("Constraint violations: $errorMessage")
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                code = "CONSTRAINT_VIOLATION",
                message = errorMessage,
                statusCode = HttpStatus.BAD_REQUEST.value()
            ))
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.error("Unhandled exception: ${ex.message}", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred",
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            ))
    }
}