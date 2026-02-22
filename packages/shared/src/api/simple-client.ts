// Simple API client that doesn't require external dependencies
const DEFAULT_API_URL = 'http://localhost:3000'
export const API_URL: string = (typeof process !== 'undefined' && process.env?.API_URL) || DEFAULT_API_URL

export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  error?: {
    code: string
    message: string
    statusCode: number
  }
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: {
    id: string
    email: string
    name: string
  }
}

export interface User {
  id: string
  email: string
  name: string
}

export class SimpleApiClient {
  private baseURL: string
  private token: string | null = null
  private refreshToken: string | null = null

  constructor(baseURL: string = API_URL) {
    this.baseURL = baseURL
    this.loadTokensFromStorage()
  }

  private loadTokensFromStorage() {
    if (typeof window !== 'undefined' && window.localStorage) {
      this.token = localStorage.getItem('accessToken')
      this.refreshToken = localStorage.getItem('refreshToken')
    }
  }

  private saveTokensToStorage() {
    if (typeof window !== 'undefined' && window.localStorage) {
      if (this.token) {
        localStorage.setItem('accessToken', this.token)
      } else {
        localStorage.removeItem('accessToken')
      }
      
      if (this.refreshToken) {
        localStorage.setItem('refreshToken', this.refreshToken)
      } else {
        localStorage.removeItem('refreshToken')
      }
    }
  }

  setTokens(accessToken: string, refreshToken: string) {
    this.token = accessToken
    this.refreshToken = refreshToken
    this.saveTokensToStorage()
  }

  clearTokens() {
    this.token = null
    this.refreshToken = null
    this.saveTokensToStorage()
  }

  private async fetchWithAuth<T>(endpoint: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    }

    // Merge headers from options
    if (options.headers) {
      Object.assign(headers, options.headers)
    }

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`
    }

    try {
      const response = await fetch(`${this.baseURL}${endpoint}`, {
        ...options,
        headers,
        credentials: 'include',
      })

      const data = await response.json()

      if (!response.ok) {
        if (response.status === 401 && this.refreshToken) {
          // Try to refresh token
          const refreshResult = await this.refreshAuthToken()
          if (refreshResult.success) {
            // Retry the original request
            return this.fetchWithAuth<T>(endpoint, options)
          }
        }
        
        return {
          success: false,
          error: {
            code: 'HTTP_ERROR',
            message: data.error?.message || `HTTP ${response.status}`,
            statusCode: response.status,
          },
        }
      }

      return {
        success: true,
        data,
      }
    } catch (error: any) {
      return {
        success: false,
        error: {
          code: 'NETWORK_ERROR',
          message: error.message || 'Network error',
          statusCode: 0,
        },
      }
    }
  }

  async login(email: string, password: string): Promise<ApiResponse<AuthResponse>> {
    const result = await this.fetchWithAuth<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })

    if (result.success && result.data) {
      this.setTokens(result.data.accessToken, result.data.refreshToken)
    }

    return result
  }

  async register(email: string, password: string, name: string): Promise<ApiResponse<AuthResponse>> {
    const result = await this.fetchWithAuth<AuthResponse>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ email, password, name }),
    })

    if (result.success && result.data) {
      this.setTokens(result.data.accessToken, result.data.refreshToken)
    }

    return result
  }

  async refreshAuthToken(): Promise<ApiResponse<{ accessToken: string; refreshToken: string }>> {
    if (!this.refreshToken) {
      return {
        success: false,
        error: {
          code: 'NO_REFRESH_TOKEN',
          message: 'No refresh token available',
          statusCode: 401,
        },
      }
    }

    const result = await this.fetchWithAuth<{ accessToken: string; refreshToken: string }>('/api/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({ refreshToken: this.refreshToken }),
    })

    if (result.success && result.data) {
      this.setTokens(result.data.accessToken, result.data.refreshToken)
    } else {
      this.clearTokens()
    }

    return result
  }

  async logout(): Promise<ApiResponse<{ message: string }>> {
    const result = await this.fetchWithAuth<{ message: string }>('/api/auth/logout', {
      method: 'POST',
    })

    this.clearTokens()
    return result
  }

  async getCurrentUser(): Promise<ApiResponse<{ user: User }>> {
    return this.fetchWithAuth<{ user: User }>('/api/auth/me')
  }

  async healthCheck(): Promise<ApiResponse<{ status: string; timestamp: string; message: string }>> {
    return this.fetchWithAuth<{ status: string; timestamp: string; message: string }>('/api/health')
  }
}

export const apiClient = new SimpleApiClient()