import { Elysia } from 'elysia'

export class AppError extends Error {
  constructor(
    public statusCode: number,
    public message: string,
    public isOperational = true
  ) {
    super(message)
    this.name = this.constructor.name
    Error.captureStackTrace(this, this.constructor)
  }
}

export class ValidationError extends AppError {
  constructor(message: string) {
    super(400, message)
  }
}

export class AuthenticationError extends AppError {
  constructor(message = 'Authentication required') {
    super(401, message)
  }
}

export class AuthorizationError extends AppError {
  constructor(message = 'Insufficient permissions') {
    super(403, message)
  }
}

export class NotFoundError extends AppError {
  constructor(message = 'Resource not found') {
    super(404, message)
  }
}

export class ConflictError extends AppError {
  constructor(message = 'Resource already exists') {
    super(409, message)
  }
}

export const errorHandler = new Elysia()
  .error({
    AppError,
    ValidationError,
    AuthenticationError,
    AuthorizationError,
    NotFoundError,
    ConflictError
  })
  .onError(({ code, error, set }) => {
    console.error('Error:', error)

    // Handle known errors
    if (error instanceof AppError) {
      set.status = error.statusCode
      return {
        success: false,
        error: {
          code: error.name,
          message: error.message,
          statusCode: error.statusCode
        }
      }
    }

    // Handle validation errors
    if (code === 'VALIDATION') {
      set.status = 400
      return {
        success: false,
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Validation failed',
          details: error.all
        }
      }
    }

    // Handle unknown errors
    set.status = 500
    return {
      success: false,
      error: {
        code: 'INTERNAL_SERVER_ERROR',
        message: process.env.NODE_ENV === 'production' 
          ? 'Internal server error' 
          : error.message,
        statusCode: 500
      }
    }
  })