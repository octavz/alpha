import { create } from 'zustand';
import api from '../api/client';
import type { Region, Category } from '../types';

interface DataState {
  regions: Region[];
  categories: Category[];
  isLoading: boolean;
  error: string | null;

  fetchRegions: () => Promise<void>;
  fetchCategories: () => Promise<void>;
  clearRegions: () => void;
  clearCategories: () => void;
}

export const useDataStore = create<DataState>((set) => ({
  regions: [],
  categories: [],
  isLoading: false,
  error: null,

  fetchRegions: async () => {
    if (useDataStore.getState().regions.length > 0) return;
    set({ isLoading: true, error: null });
    try {
      const regions = await api.getRegions();
      set({ regions, isLoading: false });
    } catch (err: any) {
      set({ error: err.message, isLoading: false });
    }
  },

   fetchCategories: async () => {
     if (useDataStore.getState().categories.length > 0) return;
     set({ isLoading: true, error: null });
     try {
       const categories = await api.getCategories();
       set({ categories, isLoading: false });
     } catch (err: any) {
       set({ error: err.message, isLoading: false });
     }
   },

   clearRegions: () => set({ regions: [] }),
   clearCategories: () => set({ categories: [] }),
 }));
