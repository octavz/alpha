export interface User {
  id: string
  email: string
  name: string | null
  phone: string | null
  role: 'customer' | 'business_admin' | 'business_staff' | 'admin'
  emailVerified: boolean
  avatarUrl: string | null
  regionId: string | null
  isBanned: boolean
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

// ========== BUSINESS DIRECTORY TYPES ==========

// Region types
export interface Region {
  id: string
  name: string
  code: string
  country: string
  timezone: string
  isActive: boolean
  createdAt: Date
}

export interface NewRegion {
  name: string
  code: string
  country: string
  timezone: string
  isActive?: boolean
}

// Category types
export interface Category {
  id: string
  name: string
  slug: string
  description: string | null
  icon: string | null
  parentId: string | null
  sortOrder: number
  isActive: boolean
  createdAt: Date
}

export interface NewCategory {
  name: string
  slug: string
  description?: string
  icon?: string
  parentId?: string | null
  sortOrder?: number
  isActive?: boolean
}

// Business types
export interface Business {
  id: string
  userId: string
  name: string
  slug: string
  description: string | null
  categoryId: string | null
  regionId: string
  
  // Contact Information
  email: string | null
  phone: string | null
  website: string | null
  
  // Address
  addressLine1: string | null
  addressLine2: string | null
  city: string | null
  state: string | null
  zipCode: string | null
  country: string | null
  latitude: string | null
  longitude: string | null
  
  // Business Details
  logoUrl: string | null
  coverImageUrl: string | null
  isVerified: boolean
  isActive: boolean
  isFeatured: boolean
  verificationStatus: 'pending' | 'approved' | 'rejected'
  
  // Service Points
  servicePointsCount: number
  
  // Metadata
  createdAt: Date
  updatedAt: Date
}

export interface NewBusiness {
  userId: string
  name: string
  slug: string
  description?: string
  categoryId?: string | null
  regionId: string
  email?: string
  phone?: string
  website?: string
  addressLine1?: string
  addressLine2?: string
  city?: string
  state?: string
  zipCode?: string
  country?: string
  latitude?: string
  longitude?: string
  logoUrl?: string
  coverImageUrl?: string
  servicePointsCount?: number
}

// Service types
export interface Service {
  id: string
  businessId: string
  name: string
  description: string | null
  durationMinutes: number
  price: string | null
  isActive: boolean
  sortOrder: number
  createdAt: Date
}

export interface NewService {
  businessId: string
  name: string
  description?: string
  durationMinutes: number
  price?: string
  isActive?: boolean
  sortOrder?: number
}

// Appointment types
export interface Appointment {
  id: string
  businessId: string
  customerId: string
  serviceId: string | null
  
  // Appointment Details
  appointmentDate: string
  startTime: string
  endTime: string
  servicePointNumber: number | null
  
  // Customer Information
  customerName: string
  customerEmail: string
  customerPhone: string | null
  customerNotes: string | null
  
  // Status
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled' | 'no_show'
  
  // Metadata
  createdAt: Date
  updatedAt: Date
  cancelledAt: Date | null
  cancelledReason: string | null
}

export interface NewAppointment {
  businessId: string
  customerId: string
  serviceId?: string | null
  appointmentDate: string
  startTime: string
  endTime: string
  servicePointNumber?: number | null
  customerName: string
  customerEmail: string
  customerPhone?: string
  customerNotes?: string
}

// Review types
export interface Review {
  id: string
  businessId: string
  customerId: string
  appointmentId: string | null
  
  // Review Details
  rating: number
  title: string | null
  comment: string | null
  isApproved: boolean
  isFeatured: boolean
  
  // Metadata
  createdAt: Date
  updatedAt: Date
}

export interface NewReview {
  businessId: string
  customerId: string
  appointmentId?: string | null
  rating: number
  title?: string
  comment?: string
}

// Business hours types
export interface BusinessHour {
  id: string
  businessId: string
  dayOfWeek: number
  openTime: string | null
  closeTime: string | null
  isClosed: boolean
  createdAt: Date
}

export interface NewBusinessHour {
  businessId: string
  dayOfWeek: number
  openTime?: string
  closeTime?: string
  isClosed?: boolean
}

// Search types
export interface BusinessSearchParams {
  q?: string
  region: string
  category?: string
  lat?: number
  lng?: number
  ratingMin?: number
  page?: number
  limit?: number
  sort?: 'relevance' | 'rating' | 'distance' | 'name'
}

export interface BusinessSearchResult {
  data: Business[]
  meta: {
    total: number
    page: number
    limit: number
    totalPages: number
    hasNext: boolean
    hasPrev: boolean
  }
}

// Availability types
export interface TimeSlot {
  date: string
  startTime: string
  endTime: string
  servicePointNumber: number
  isAvailable: boolean
}

export interface AvailabilityParams {
  businessId: string
  date: string
  serviceId?: string
}

// Dashboard types
export interface BusinessDashboardStats {
  totalAppointments: number
  pendingAppointments: number
  completedAppointments: number
  revenue: number
  averageRating: number
  totalReviews: number
}

export interface AdminDashboardStats {
  totalUsers: number
  totalBusinesses: number
  pendingBusinesses: number
  totalAppointments: number
  revenue: number
  activeRegions: number
}