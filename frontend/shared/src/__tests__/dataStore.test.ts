import { act } from '@testing-library/react';
import { api } from '../api/client';
import { useDataStore } from '../stores/dataStore';
import type { Region, Category } from '../types';

// Store original methods to restore later
const originalGetRegions = api.getRegions;
const originalGetCategories = api.getCategories;

const mockRegions: Region[] = [
  {
    id: '1',
    name: 'New York',
    code: 'NY',
    country: 'USA',
    timezone: 'America/New_York',
    createdAt: '2024-01-01T00:00:00Z',
    active: true,
  },
  {
    id: '2',
    name: 'Los Angeles',
    code: 'LA',
    country: 'USA',
    timezone: 'America/Los_Angeles',
    createdAt: '2024-01-01T00:00:00Z',
    active: true,
  },
];

const mockCategories: Category[] = [
  {
    id: '1',
    name: 'Beauty & Salon',
    slug: 'beauty-salon',
    description: 'Beauty services',
    icon: '💇',
    parentId: undefined,
    sortOrder: 1,
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    children: [],
  },
  {
    id: '2',
    name: 'Health & Wellness',
    slug: 'health-wellness',
    description: 'Health services',
    icon: '🏥',
    parentId: undefined,
    sortOrder: 2,
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    children: [],
  },
];

beforeEach(() => {
  jest.clearAllMocks();
  // Mock the api instance methods directly
  api.getRegions = jest.fn().mockResolvedValue(mockRegions);
  api.getCategories = jest.fn().mockResolvedValue(mockCategories);
  
  useDataStore.setState({
    regions: [],
    categories: [],
    isLoading: false,
    error: null,
  });
});

afterEach(() => {
  // Restore original methods
  api.getRegions = originalGetRegions;
  api.getCategories = originalGetCategories;
});

describe('DataStore', () => {
  describe('fetchRegions', () => {
    test('should fetch regions successfully', async () => {
      await act(async () => {
        await useDataStore.getState().fetchRegions();
      });

      expect(api.getRegions).toHaveBeenCalled();
      expect(useDataStore.getState().regions).toEqual(mockRegions);
      expect(useDataStore.getState().isLoading).toBe(false);
    });

    test('should not fetch regions if already loaded', async () => {
      await act(async () => {
        await useDataStore.getState().fetchRegions();
      });

      // Clear the mock after first call
      (api.getRegions as jest.Mock).mockClear();

      await act(async () => {
        await useDataStore.getState().fetchRegions();
      });

      expect(api.getRegions).not.toHaveBeenCalled();
    });

    test('should handle fetch error', async () => {
      (api.getRegions as jest.Mock).mockRejectedValue(new Error('Failed to fetch regions'));

      await act(async () => {
        await useDataStore.getState().fetchRegions();
      });

      expect(useDataStore.getState().error).toBe('Failed to fetch regions');
    });
  });

  describe('fetchCategories', () => {
    test('should fetch categories successfully', async () => {
      await act(async () => {
        await useDataStore.getState().fetchCategories();
      });

      expect(api.getCategories).toHaveBeenCalled();
      expect(useDataStore.getState().categories).toEqual(mockCategories);
      expect(useDataStore.getState().isLoading).toBe(false);
    });

    test('should not fetch categories if already loaded', async () => {
      await act(async () => {
        await useDataStore.getState().fetchCategories();
      });

      (api.getCategories as jest.Mock).mockClear();

      await act(async () => {
        await useDataStore.getState().fetchCategories();
      });

      expect(api.getCategories).not.toHaveBeenCalled();
    });

    test('should handle fetch error', async () => {
      (api.getCategories as jest.Mock).mockRejectedValue(new Error('Failed to fetch categories'));

      await act(async () => {
        await useDataStore.getState().fetchCategories();
      });

      expect(useDataStore.getState().error).toBe('Failed to fetch categories');
    });
  });

  describe('clearRegions', () => {
    test('should clear regions', async () => {
      await act(async () => {
        await useDataStore.getState().fetchRegions();
      });

      expect(useDataStore.getState().regions).toHaveLength(2);

      act(() => {
        useDataStore.getState().clearRegions();
      });

      expect(useDataStore.getState().regions).toEqual([]);
    });
  });

  describe('clearCategories', () => {
    test('should clear categories', async () => {
      await act(async () => {
        await useDataStore.getState().fetchCategories();
      });

      expect(useDataStore.getState().categories).toHaveLength(2);

      act(() => {
        useDataStore.getState().clearCategories();
      });

      expect(useDataStore.getState().categories).toEqual([]);
    });
  });

  describe('initial state', () => {
    test('should have correct default values', () => {
      const state = useDataStore.getState();
      expect(state.regions).toEqual([]);
      expect(state.categories).toEqual([]);
      expect(state.isLoading).toBe(false);
      expect(state.error).toBeNull();
    });
  });
});
