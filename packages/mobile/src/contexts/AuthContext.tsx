import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import * as Keychain from 'react-native-keychain'
import { edenApiClient, User } from '@alpha/shared'

interface AuthContextType {
  user: User | null
  isLoading: boolean
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<void>
  register: (email: string, password: string, name: string) => Promise<void>
  logout: () => Promise<void>
  refreshToken: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

interface AuthProviderProps {
  children: ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  useEffect(() => {
    checkAuthStatus()
  }, [])

  const checkAuthStatus = async () => {
    try {
      setIsLoading(true)
      
      // Try to get tokens from secure storage
      const credentials = await Keychain.getGenericPassword()
      
      if (credentials) {
        // Load tokens into API client
        const { username: accessToken, password: refreshToken } = credentials
        edenApiClient.setTokens(accessToken, refreshToken)
        
        // Try to get current user
        try {
          const response = await edenApiClient.getCurrentUser()
          if (response.success && response.data) {
            setUser(response.data.user)
            setIsAuthenticated(true)
          } else {
            await clearAuth()
          }
        } catch (error) {
          await clearAuth()
        }
      }
    } catch (error) {
      console.error('Auth check error:', error)
      await clearAuth()
    } finally {
      setIsLoading(false)
    }
  }

  const saveTokens = async (accessToken: string, refreshToken: string) => {
    try {
      await Keychain.setGenericPassword(accessToken, refreshToken)
      edenApiClient.setTokens(accessToken, refreshToken)
    } catch (error) {
      console.error('Failed to save tokens:', error)
    }
  }

  const clearAuth = async () => {
    try {
      await Keychain.resetGenericPassword()
      edenApiClient.clearTokens()
      setUser(null)
      setIsAuthenticated(false)
    } catch (error) {
      console.error('Failed to clear auth:', error)
    }
  }

  const login = async (email: string, password: string) => {
    try {
      setIsLoading(true)
      const response = await edenApiClient.login(email, password)
      
      if (response.success && response.data) {
        const { user, accessToken, refreshToken } = response.data
        await saveTokens(accessToken, refreshToken)
        setUser(user)
        setIsAuthenticated(true)
      } else {
        throw new Error(response.error?.message || 'Login failed')
      }
    } catch (error) {
      console.error('Login error:', error)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const register = async (email: string, password: string, name: string) => {
    try {
      setIsLoading(true)
      const response = await edenApiClient.register(email, password, name)
      
      if (response.success && response.data) {
        const { user, accessToken, refreshToken } = response.data
        await saveTokens(accessToken, refreshToken)
        setUser(user)
        setIsAuthenticated(true)
      } else {
        throw new Error(response.error?.message || 'Registration failed')
      }
    } catch (error) {
      console.error('Registration error:', error)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const logout = async () => {
    try {
      setIsLoading(true)
      await edenApiClient.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      await clearAuth()
      setIsLoading(false)
    }
  }

  const refreshToken = async () => {
    try {
      const credentials = await Keychain.getGenericPassword()
      if (!credentials) {
        throw new Error('No refresh token available')
      }

      // Set the refresh token first
      edenApiClient.setTokens('', credentials.password)
      
      const response = await edenApiClient.refreshTokens()

      if (response.success && response.data) {
        const { accessToken, refreshToken: newRefreshToken } = response.data
        await saveTokens(accessToken, newRefreshToken)
      } else {
        throw new Error('Token refresh failed')
      }
    } catch (error) {
      console.error('Token refresh error:', error)
      await clearAuth()
      throw error
    }
  }

  const value = {
    user,
    isLoading,
    isAuthenticated,
    login,
    register,
    logout,
    refreshToken
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}