package com.alpha.service.exception

import org.springframework.http.HttpStatus

open class ServiceException(
    message: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
) : RuntimeException(message)

class AuthenticationException(message: String) : ServiceException(message, HttpStatus.UNAUTHORIZED)
class AuthorizationException(message: String) : ServiceException(message, HttpStatus.FORBIDDEN)
class ValidationException(message: String) : ServiceException(message, HttpStatus.BAD_REQUEST)
class NotFoundException(message: String) : ServiceException(message, HttpStatus.NOT_FOUND)
class ConflictException(message: String) : ServiceException(message, HttpStatus.CONFLICT)
class ForbiddenException(message: String) : ServiceException(message, HttpStatus.FORBIDDEN)
class BusinessException(message: String) : ServiceException(message, HttpStatus.BAD_REQUEST)