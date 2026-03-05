import { ApiClient } from '../api/client';
import type { User, Business, Region, Category, Appointment, Page } from '../types';

// Mock environment variable
process.env.VITE_API_URL = 'http://localhost:3000';

describe('ApiClient', () => {
  let api: ApiClient;
  let postSpy: jest.SpyInstance;
  let getSpy: jest.SpyInstance;
  let putSpy: jest.SpyInstance;
  let deleteSpy: jest.SpyInstance;

  beforeEach(() => {
    api = new ApiClient();
    postSpy = jest.spyOn(api['client'], 'post');
    getSpy = jest.spyOn(api['client'], 'get');
    putSpy = jest.spyOn(api['client'], 'put');
    deleteSpy = jest.spyOn(api['client'], 'delete');
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Authentication', () => {
    const mockUser: User = {
      id: '1',
      email: 'test@example.com',
      name: 'Test User',
      role: 'user',
      emailVerified: true,
      createdAt: '2024-01-01T00:00:00Z',
      banned: false,
      phone: '123-456-7890',
      avatarUrl: undefined,
    };

    const mockAuthResponse = {
      data: {
        data: {
          user: mockUser,
          accessToken: 'mock-access-token',
          refreshToken: 'mock-refresh-token',
          sessionId: 'mock-session-id',
        },
      },
    };

    test('register should create user and return user data', async () => {
      postSpy.mockResolvedValue(mockAuthResponse);

      const user = await api.register('test@example.com', 'password123', 'Test User');

      expect(user).toEqual(mockUser);
      expect(postSpy).toHaveBeenCalledWith('/auth/register', { email: 'test@example.com', password: 'password123', name: 'Test User' });
    });

    test('login should authenticate user and return user data', async () => {
      postSpy.mockResolvedValue(mockAuthResponse);

      const user = await api.login('test@example.com', 'password123');

      expect(user).toEqual(mockUser);
      expect(postSpy).toHaveBeenCalledWith('/auth/login', { email: 'test@example.com', password: 'password123' });
    });

    test('logout should call logout endpoint', async () => {
      // Set tokens first so sessionId exists
      await api.setTokens('access-token', 'refresh-token', 'session-id');
      postSpy.mockResolvedValue({ data: { success: true } });

      await expect(api.logout()).resolves.toBeUndefined();
      expect(postSpy).toHaveBeenCalledWith('/auth/logout', { sessionId: 'session-id' });
    });

    test('getCurrentUser should return current user', async () => {
      getSpy.mockResolvedValue({ data: { data: mockUser } });

      const user = await api.getCurrentUser();

      expect(user).toEqual(mockUser);
      expect(getSpy).toHaveBeenCalledWith('/auth/me');
    });

    test('updateProfile should update user and return updated user', async () => {
      const updatedUser = { ...mockUser, name: 'Updated Name' };
      putSpy.mockResolvedValue({ data: { data: updatedUser } });

      const user = await api.updateProfile({ name: 'Updated Name' });

      expect(user).toEqual(updatedUser);
      expect(putSpy).toHaveBeenCalledWith('/auth/me', { name: 'Updated Name' });
    });

    test('changePassword should change password successfully', async () => {
      postSpy.mockResolvedValue({ data: { success: true } });

      await expect(api.changePassword('oldPass', 'newPass')).resolves.toBeUndefined();
      expect(postSpy).toHaveBeenCalledWith('/auth/change-password', null, expect.objectContaining({
        params: { currentPassword: 'oldPass', newPassword: 'newPass' },
      }));
    });
  });

  describe('Business', () => {
    const mockBusiness: Business = {
      id: '1',
      name: 'Test Business',
      slug: 'test-business',
      description: 'A test business',
      address: '123 Test St',
      city: 'Test City',
      regionId: '1',
      categoryId: '1',
      userId: '1',
      verificationStatus: 'verified',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
      active: true,
      featured: false,
      rating: 4.5,
      reviewCount: 100,
      phone: '123-456-7890',
      email: 'business@test.com',
      website: 'https://test.com',
      latitude: 40.7128,
      longitude: -74.006,
    };

    const mockPage: Page<Business> = {
      content: [mockBusiness],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0,
      first: true,
      last: true,
    };

    test('getBusinesses should return paginated businesses', async () => {
      getSpy.mockResolvedValue({ data: { data: mockPage } });

      const result = await api.getBusinesses();

      expect(result).toEqual(mockPage);
      expect(getSpy).toHaveBeenCalledWith('/businesses/search', { params: undefined });
    });

    test('getBusinesses with search parameters', async () => {
      getSpy.mockResolvedValue({ data: { data: mockPage } });

      const result = await api.getBusinesses({
        query: 'test',
        regionId: '1',
        categoryId: '2',
        page: 0,
        size: 10,
      });

      expect(result).toEqual(mockPage);
      expect(getSpy).toHaveBeenCalledWith('/businesses/search', { params: { query: 'test', regionId: '1', categoryId: '2', page: 0, size: 10 } });
    });

    test('getBusiness should return business by id', async () => {
      getSpy.mockResolvedValue({ data: { data: mockBusiness } });

      const business = await api.getBusiness('1');

      expect(business).toEqual(mockBusiness);
      expect(getSpy).toHaveBeenCalledWith('/businesses/1');
    });

    test('getBusinessBySlug should return business by slug', async () => {
      getSpy.mockResolvedValue({ data: { data: mockBusiness } });

      const business = await api.getBusinessBySlug('test-business');

      expect(business).toEqual(mockBusiness);
      expect(getSpy).toHaveBeenCalledWith('/businesses/slug/test-business');
    });

    test('getFeaturedBusinesses should return featured businesses', async () => {
      getSpy.mockResolvedValue({ data: { data: mockPage } });

      const result = await api.getFeaturedBusinesses();

      expect(result).toEqual(mockPage);
      expect(getSpy).toHaveBeenCalledWith('/businesses/featured', { params: { page: 0, size: 10 } });
    });

    test('getBusinessesByRegion should return businesses for region', async () => {
      getSpy.mockResolvedValue({ data: { data: mockPage } });

      const result = await api.getBusinessesByRegion('1');

      expect(result).toEqual(mockPage);
      expect(getSpy).toHaveBeenCalledWith('/businesses/region/1', { params: { page: 0, size: 20 } });
    });

    test('getBusinessesByCategory should return businesses for category', async () => {
      getSpy.mockResolvedValue({ data: { data: mockPage } });

      const result = await api.getBusinessesByCategory('1');

      expect(result).toEqual(mockPage);
      expect(getSpy).toHaveBeenCalledWith('/businesses/category/1', { params: { page: 0, size: 20 } });
    });

    test('createBusiness should create new business', async () => {
      postSpy.mockResolvedValue({ data: { data: mockBusiness } });

      const business = await api.createBusiness({
        name: 'Test Business',
        description: 'A test business',
        slug: 'test-business',
        address: '123 Test St',
        city: 'Test City',
        regionId: '1',
        categoryId: '1',
      });

      expect(business).toEqual(mockBusiness);
      expect(postSpy).toHaveBeenCalledWith('/businesses', {
        name: 'Test Business',
        description: 'A test business',
        slug: 'test-business',
        address: '123 Test St',
        city: 'Test City',
        regionId: '1',
        categoryId: '1',
      });
    });

    test('updateBusiness should update existing business', async () => {
      const updatedBusiness = { ...mockBusiness, name: 'Updated Business' };
      putSpy.mockResolvedValue({ data: { data: updatedBusiness } });

      const business = await api.updateBusiness('1', { name: 'Updated Business' });

      expect(business).toEqual(updatedBusiness);
      expect(putSpy).toHaveBeenCalledWith('/businesses/1', { name: 'Updated Business' });
    });

    test('deleteBusiness should delete business', async () => {
      deleteSpy.mockResolvedValue({ data: { success: true } });

      await expect(api.deleteBusiness('1')).resolves.toBeUndefined();
      expect(deleteSpy).toHaveBeenCalledWith('/businesses/1');
    });
  });

  describe('Appointment', () => {
    const mockAppointment: Appointment = {
      id: '1',
      businessId: '1',
      userId: '1',
      serviceId: '1',
      dateTime: '2024-01-01T10:00:00Z',
      status: 'confirmed',
      notes: 'Test appointment',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    };

    const mockAppointmentPage: Page<Appointment> = {
      content: [mockAppointment],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0,
      first: true,
      last: true,
    };

    test('getAppointments should return paginated appointments', async () => {
      getSpy.mockResolvedValue({ data: { data: mockAppointmentPage } });

      const result = await api.getAppointments();

      expect(result).toEqual(mockAppointmentPage);
      expect(getSpy).toHaveBeenCalledWith('/appointments', { params: undefined });
    });

    test('getAppointment should return appointment by id', async () => {
      getSpy.mockResolvedValue({ data: { data: mockAppointment } });

      const appointment = await api.getAppointment('1');

      expect(appointment).toEqual(mockAppointment);
      expect(getSpy).toHaveBeenCalledWith('/appointments/1');
    });

    test('createAppointment should create new appointment', async () => {
      postSpy.mockResolvedValue({ data: { data: mockAppointment } });

      const appointment = await api.createAppointment({
        businessId: '1',
        serviceId: '1',
        dateTime: '2024-01-01T10:00:00Z',
        notes: 'Test appointment',
      });

      expect(appointment).toEqual(mockAppointment);
      expect(postSpy).toHaveBeenCalledWith('/appointments', {
        businessId: '1',
        serviceId: '1',
        dateTime: '2024-01-01T10:00:00Z',
        notes: 'Test appointment',
      });
    });

    test('updateAppointment should update appointment', async () => {
      const updatedAppointment = { ...mockAppointment, status: 'completed' };
      putSpy.mockResolvedValue({ data: { data: updatedAppointment } });

      const appointment = await api.updateAppointment('1', { status: 'completed' });

      expect(appointment).toEqual(updatedAppointment);
      expect(putSpy).toHaveBeenCalledWith('/appointments/1', { status: 'completed' });
    });

    test('cancelAppointment should cancel appointment', async () => {
      deleteSpy.mockResolvedValue({ data: { success: true } });

      await expect(api.cancelAppointment('1')).resolves.toBeUndefined();
      expect(deleteSpy).toHaveBeenCalledWith('/appointments/1');
    });
  });

  describe('Region', () => {
    const mockRegion: Region = {
      id: '1',
      name: 'New York',
      code: 'NY',
      country: 'USA',
      timezone: 'America/New_York',
      createdAt: '2024-01-01T00:00:00Z',
      active: true,
    };

    const mockRegions: Region[] = [
      mockRegion,
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

    test('getRegions should return all regions', async () => {
      getSpy.mockResolvedValue({ data: { data: mockRegions } });

      const regions = await api.getRegions();

      expect(regions).toEqual(mockRegions);
      expect(getSpy).toHaveBeenCalledWith('/regions');
    });

    test('getRegion should return region by id', async () => {
      getSpy.mockResolvedValue({ data: { data: mockRegion } });

      const region = await api.getRegion('1');

      expect(region).toEqual(mockRegion);
      expect(getSpy).toHaveBeenCalledWith('/regions/1');
    });
  });

  describe('Category', () => {
    const mockCategory: Category = {
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
    };

    const mockCategories: Category[] = [
      mockCategory,
      {
        id: '2',
        name: 'Health',
        slug: 'health',
        description: 'Health services',
        icon: '🏥',
        parentId: undefined,
        sortOrder: 2,
        isActive: true,
        createdAt: '2024-01-01T00:00:00Z',
        children: [],
      },
    ];

    test('getCategories should return all categories', async () => {
      getSpy.mockResolvedValue({ data: { data: mockCategories } });

      const categories = await api.getCategories();

      expect(categories).toEqual(mockCategories);
      expect(getSpy).toHaveBeenCalledWith('/categories');
    });

    test('getCategory should return category by id', async () => {
      getSpy.mockResolvedValue({ data: { data: mockCategory } });

      const category = await api.getCategory('1');

      expect(category).toEqual(mockCategory);
      expect(getSpy).toHaveBeenCalledWith('/categories/1');
    });
  });

  describe('Token Management', () => {
    test('setTokens should store tokens in memory', async () => {
      api = new ApiClient();
      await api.setTokens('access-token', 'refresh-token', 'session-id');

      expect(api.getAccessToken()).toBe('access-token');
      expect(api.getSessionId()).toBe('session-id');
      expect(api.isAuthenticated()).toBe(true);
    });

    test('clearTokens should remove tokens', async () => {
      api = new ApiClient();
      await api.setTokens('access-token', 'refresh-token', 'session-id');
      api.clearTokens();

      expect(api.getAccessToken()).toBeNull();
      expect(api.getSessionId()).toBeNull();
      expect(api.isAuthenticated()).toBe(false);
    });
  });

  describe('getHealth', () => {
    test('should return health status', async () => {
      getSpy.mockResolvedValue({ data: { success: true, status: 'ok' } });

      const health = await api.getHealth();

      expect(health).toEqual({ success: true, status: 'ok' });
      expect(getSpy).toHaveBeenCalledWith('/health');
    });
  });
});
