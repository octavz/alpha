export interface User {
  id: string
  email: string
  name: string | null
  emailVerified: boolean
  avatarUrl: string | null
  createdAt: Date
  updatedAt: Date
}

export interface AuthResponse {
  user: Omit<User, 'updatedAt'>
  accessToken: string
  refreshToken: string
  sessionId: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  name: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  accessToken: string
  refreshToken: string
}

export interface VerifyEmailRequest {
  token: string
}

export interface ResetPasswordRequest {
  email: string
}

export interface ResetPasswordConfirmRequest {
  token: string
  newPassword: string
}

export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  error?: {
    code: string
    message: string
    statusCode: number
    details?: any
  }
  message?: string
}

export type ApiError = {
  code: string
  message: string
  statusCode: number
  details?: any
}

// Environment types
export interface Environment {
  NODE_ENV: 'development' | 'production' | 'test'
  API_URL: string
  WEB_URL: string
}

// Pagination types
export interface PaginatedResponse<T> {
  data: T[]
  meta: {
    total: number
    page: number
    limit: number
    totalPages: number
    hasNext: boolean
    hasPrev: boolean
  }
}

export interface PaginationParams {
  page?: number
  limit?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}