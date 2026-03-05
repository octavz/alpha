export interface User {
  id: string;
  email: string;
  name: string;
  role: string;
  emailVerified: boolean;
  createdAt: string;
  banned: boolean;
  avatarUrl?: string;
  phone?: string;
}

export interface Region {
  id: string;
  name: string;
  code: string;
  country: string;
  timezone: string;
  createdAt: string;
  active: boolean;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  description?: string;
  icon?: string;
  parentId?: string;
  sortOrder: number;
  isActive: boolean;
  createdAt: string;
  children: Category[];
}

export interface Business {
  id: string;
  name: string;
  description: string;
  slug: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  phone?: string;
  email?: string;
  website?: string;
  latitude?: number;
  longitude?: number;
  regionId: string;
  categoryId: string;
  userId: string;
  verificationStatus: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
  featured: boolean;
  rating?: number;
  reviewCount?: number;
  logoUrl?: string;
  coverImageUrl?: string;
  isVerified?: boolean;
}

export interface Appointment {
  id: string;
  userId: string;
  businessId: string;
  serviceId: string;
  dateTime: string;
  status: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  sessionId: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
