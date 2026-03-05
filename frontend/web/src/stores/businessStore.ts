import { create } from 'zustand';
import api from '../api/client';

interface Business {
  id: string;
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
  imageUrl?: string;
  isVerified: boolean;
  isFeatured: boolean;
  rating: number;
  reviewCount: number;
  createdAt: string;
}

interface BusinessState {
  businesses: Business[];
  featuredBusinesses: Business[];
  currentBusiness: Business | null;
  isLoading: boolean;
  error: string | null;
  totalPages: number;
  currentPage: number;

  // Actions
  fetchBusinesses: (params?: { query?: string; regionId?: string; categoryId?: string; page?: number; size?: number }) => Promise<void>;
  fetchFeaturedBusinesses: (page?: number, size?: number) => Promise<void>;
  fetchBusiness: (id: string) => Promise<void>;
  fetchBusinessBySlug: (slug: string) => Promise<void>;
  createBusiness: (data: Partial<Business>) => Promise<Business>;
  updateBusiness: (id: string, data: Partial<Business>) => Promise<Business>;
  deleteBusiness: (id: string) => Promise<void>;
  clearError: () => void;
}

export const useBusinessStore = create<BusinessState>((set, get) => ({
  businesses: [],
  featuredBusinesses: [],
  currentBusiness: null,
  isLoading: false,
  error: null,
  totalPages: 0,
  currentPage: 0,

  fetchBusinesses: async (params) => {
    set({ isLoading: true, error: null });
    try {
      const response = await api.getBusinesses(params);
      const data = response.content || response;
      set({ 
        businesses: data, 
        isLoading: false,
        totalPages: response.totalPages || 1,
        currentPage: response.number || 0,
      });
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
    }
  },

  fetchFeaturedBusinesses: async (page = 0, size = 10) => {
    set({ isLoading: true, error: null });
    try {
      const response = await api.getFeaturedBusinesses(page, size);
      set({ 
        featuredBusinesses: response.content || response, 
        isLoading: false 
      });
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
    }
  },

  fetchBusiness: async (id: string) => {
    set({ isLoading: true, error: null });
    try {
      const business = await api.getBusiness(id);
      set({ currentBusiness: business, isLoading: false });
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
    }
  },

  fetchBusinessBySlug: async (slug: string) => {
    set({ isLoading: true, error: null });
    try {
      const business = await api.getBusinessBySlug(slug);
      set({ currentBusiness: business, isLoading: false });
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
    }
  },

  createBusiness: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const business = await api.createBusiness(data as any);
      const businesses = [...get().businesses, business];
      set({ businesses, isLoading: false });
      return business;
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
      throw err;
    }
  },

  updateBusiness: async (id, data) => {
    set({ isLoading: true, error: null });
    try {
      const business = await api.updateBusiness(id, data);
      const businesses = get().businesses.map(b => b.id === id ? business : b);
      set({ businesses, currentBusiness: business, isLoading: false });
      return business;
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
      throw err;
    }
  },

  deleteBusiness: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await api.deleteBusiness(id);
      const businesses = get().businesses.filter(b => b.id !== id);
      set({ businesses, isLoading: false });
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
      throw err;
    }
  },

  clearError: () => set({ error: null }),
}));
