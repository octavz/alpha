import axios, { AxiosInstance, AxiosError } from 'axios';
import type { User, Business, Region, Category, Appointment, Page } from '../types';

// Dynamic import for AsyncStorage to avoid issues in web environment
let AsyncStorage: any = null;
const loadAsyncStorage = async () => {
  if (AsyncStorage) return AsyncStorage;
  try {
    AsyncStorage = (await import('@react-native-async-storage/async-storage')).default;
  } catch {
    AsyncStorage = null;
  }
  return AsyncStorage;
};

const isReactNative = typeof document === 'undefined' && typeof window === 'undefined';

const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || process.env.VITE_API_URL || 'http://localhost:3000';

class ApiClient {
  private client: AxiosInstance;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private sessionId: string | null = null;

  constructor() {
    this.client = axios.create({
      baseURL: `${API_BASE_URL}/api/v1`,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.client.interceptors.request.use((config) => {
      if (this.accessToken) {
        config.headers.Authorization = `Bearer ${this.accessToken}`;
      }
      return config;
    });

    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        if (error.response?.status === 401) {
          const refreshed = await this.refreshAccessToken();
          if (refreshed && error.config) {
            error.config.headers.Authorization = `Bearer ${this.accessToken}`;
            return this.client.request(error.config);
          }
        }
        return Promise.reject(error);
      }
    );
   }

   private async getStorage() {
     if (isReactNative) {
       const storage = await loadAsyncStorage();
       return storage;
     }
     return typeof window !== 'undefined' ? window.localStorage : null;
   }

   async setTokens(accessToken: string, refreshToken: string, sessionId: string) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.sessionId = sessionId;
    
    const storage = await this.getStorage();
    if (storage) {
      await storage.setItem('accessToken', accessToken);
      await storage.setItem('refreshToken', refreshToken);
      await storage.setItem('sessionId', sessionId);
    }
  }

  async loadTokens() {
    const storage = await this.getStorage();
    if (storage) {
      this.accessToken = await storage.getItem('accessToken');
      this.refreshToken = await storage.getItem('refreshToken');
      this.sessionId = await storage.getItem('sessionId');
    }
  }

  async clearTokens() {
    this.accessToken = null;
    this.refreshToken = null;
    this.sessionId = null;
    
    const storage = await this.getStorage();
    if (storage) {
      await storage.removeItem('accessToken');
      await storage.removeItem('refreshToken');
      await storage.removeItem('sessionId');
    }
  }

  getAccessToken() {
    return this.accessToken;
  }

  getSessionId() {
    return this.sessionId;
  }

  isAuthenticated() {
    return !!this.accessToken;
  }

  async refreshAccessToken(): Promise<boolean> {
    if (!this.refreshToken || !this.sessionId) return false;
    try {
      const response = await this.client.post('/auth/refresh', {
        refreshToken: this.refreshToken,
        sessionId: this.sessionId,
      });
      const { accessToken, refreshToken, sessionId } = response.data.data;
      this.setTokens(accessToken, refreshToken, sessionId);
      return true;
    } catch {
      this.clearTokens();
      return false;
    }
  }

  // Auth endpoints
  async register(email: string, password: string, name: string): Promise<User> {
    const response = await this.client.post('/auth/register', { email, password, name });
    const { accessToken, refreshToken, sessionId, user } = response.data.data;
    this.setTokens(accessToken, refreshToken, sessionId);
    return user;
  }

  async login(email: string, password: string): Promise<User> {
    const response = await this.client.post('/auth/login', { email, password });
    const { accessToken, refreshToken, sessionId, user } = response.data.data;
    this.setTokens(accessToken, refreshToken, sessionId);
    return user;
  }

  async logout(): Promise<void> {
    if (!this.sessionId) return;
    try {
      await this.client.post('/auth/logout', { sessionId: this.sessionId });
    } finally {
      this.clearTokens();
    }
  }

  async getCurrentUser(): Promise<User> {
    const response = await this.client.get('/auth/me');
    return response.data.data;
  }

  async updateProfile(data: { name?: string; phone?: string; avatarUrl?: string }): Promise<User> {
    const response = await this.client.put('/auth/me', data);
    return response.data.data;
  }

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await this.client.post('/auth/change-password', null, {
      params: { currentPassword, newPassword },
    });
  }

  // Business endpoints
  async getBusinesses(params?: {
    query?: string;
    regionId?: string;
    categoryId?: string;
    page?: number;
    size?: number;
  }): Promise<Page<Business>> {
    const response = await this.client.get('/businesses/search', { params });
    return response.data.data;
  }

  async getBusiness(id: string): Promise<Business> {
    const response = await this.client.get(`/businesses/${id}`);
    return response.data.data;
  }

  async getBusinessBySlug(slug: string): Promise<Business> {
    const response = await this.client.get(`/businesses/slug/${slug}`);
    return response.data.data;
  }

  async getFeaturedBusinesses(page = 0, size = 10): Promise<Page<Business>> {
    const response = await this.client.get('/businesses/featured', { params: { page, size } });
    return response.data.data;
  }

  async getBusinessesByRegion(regionId: string, page = 0, size = 20): Promise<Page<Business>> {
    const response = await this.client.get(`/businesses/region/${regionId}`, { params: { page, size } });
    return response.data.data;
  }

  async getBusinessesByCategory(categoryId: string, page = 0, size = 20): Promise<Page<Business>> {
    const response = await this.client.get(`/businesses/category/${categoryId}`, { params: { page, size } });
    return response.data.data;
  }

  async getMyBusinesses(page = 0, size = 20): Promise<Page<Business>> {
    const response = await this.client.get('/businesses/my-businesses', { params: { page, size } });
    return response.data.data;
  }

  async createBusiness(data: {
    name: string;
    description: string;
    slug: string;
    address: string;
    city: string;
    regionId: string;
    categoryId: string;
    phone?: string;
    email?: string;
    website?: string;
    latitude?: number;
    longitude?: number;
  }): Promise<Business> {
    const response = await this.client.post('/businesses', data);
    return response.data.data;
  }

  async updateBusiness(id: string, data: Partial<{
    name: string;
    description: string;
    address: string;
    city: string;
    phone: string;
    email: string;
    website: string;
    latitude: number;
    longitude: number;
    isVerified: boolean;
    isFeatured: boolean;
  }>): Promise<Business> {
    const response = await this.client.put(`/businesses/${id}`, data);
    return response.data.data;
  }

  async deleteBusiness(id: string): Promise<void> {
    await this.client.delete(`/businesses/${id}`);
  }

  // Appointment endpoints
  async getAppointments(params?: {
    businessId?: string;
    status?: string;
    page?: number;
    size?: number;
  }): Promise<Page<Appointment>> {
    const response = await this.client.get('/appointments', { params });
    return response.data.data;
  }

  async getAppointment(id: string): Promise<Appointment> {
    const response = await this.client.get(`/appointments/${id}`);
    return response.data.data;
  }

  async createAppointment(data: {
    businessId: string;
    serviceId: string;
    dateTime: string;
    notes?: string;
  }): Promise<Appointment> {
    const response = await this.client.post('/appointments', data);
    return response.data.data;
  }

  async updateAppointment(id: string, data: {
    status?: string;
    dateTime?: string;
    notes?: string;
  }): Promise<Appointment> {
    const response = await this.client.put(`/appointments/${id}`, data);
    return response.data.data;
  }

  async cancelAppointment(id: string): Promise<void> {
    await this.client.delete(`/appointments/${id}`);
  }

  // Region endpoints
  async getRegions(): Promise<Region[]> {
    const response = await this.client.get('/regions');
    return response.data.data;
  }

  async getRegion(id: string): Promise<Region> {
    const response = await this.client.get(`/regions/${id}`);
    return response.data.data;
  }

  // Category endpoints
  async getCategories(): Promise<Category[]> {
    const response = await this.client.get('/categories');
    return response.data.data;
  }

  async getCategory(id: string): Promise<Category> {
    const response = await this.client.get(`/categories/${id}`);
    return response.data.data;
  }
}

export const api = new ApiClient();
export default api;
