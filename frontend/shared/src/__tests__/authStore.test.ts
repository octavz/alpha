import { act } from '@testing-library/react';
import { api } from '../api/client';
import { useAuthStore } from '../stores/authStore';
import type { User } from '../types';

const originalGetCurrentUser = api.getCurrentUser;
const originalLogin = api.login;
const originalRegister = api.register;
const originalLogout = api.logout;
const originalUpdateProfile = api.updateProfile;
const originalChangePassword = api.changePassword;
const originalLoadTokens = api.loadTokens;
const originalClearTokens = api.clearTokens;
const originalIsAuthenticated = api.isAuthenticated;

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

beforeEach(() => {
  jest.clearAllMocks();
  useAuthStore.setState({
    user: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
  });

  api.getCurrentUser = jest.fn().mockResolvedValue(mockUser);
  api.login = jest.fn().mockResolvedValue(mockUser);
  api.register = jest.fn().mockResolvedValue(mockUser);
  api.logout = jest.fn().mockResolvedValue(undefined);
  api.updateProfile = jest.fn().mockResolvedValue(mockUser);
  api.changePassword = jest.fn().mockResolvedValue(undefined);
  api.loadTokens = jest.fn().mockResolvedValue(undefined);
  api.clearTokens = jest.fn();
  api.isAuthenticated = jest.fn().mockReturnValue(false);
});

afterEach(() => {
  api.getCurrentUser = originalGetCurrentUser;
  api.login = originalLogin;
  api.register = originalRegister;
  api.logout = originalLogout;
  api.updateProfile = originalUpdateProfile;
  api.changePassword = originalChangePassword;
  api.loadTokens = originalLoadTokens;
  api.clearTokens = originalClearTokens;
  api.isAuthenticated = originalIsAuthenticated;
});

describe('AuthStore', () => {
  describe('checkAuth', () => {
    test('should set authenticated state when tokens exist and user data loads', async () => {
      (api.isAuthenticated as jest.Mock).mockReturnValue(true);

      const store = useAuthStore.getState();
      await act(async () => {
        await store.checkAuth();
      });

      expect(api.loadTokens).toHaveBeenCalled();
      expect(api.getCurrentUser).toHaveBeenCalled();
      expect(useAuthStore.getState().user).toEqual(mockUser);
      expect(useAuthStore.getState().isAuthenticated).toBe(true);
    });

    test('should clear auth when not authenticated', async () => {
      (api.isAuthenticated as jest.Mock).mockReturnValue(false);

      const store = useAuthStore.getState();
      await act(async () => {
        await store.checkAuth();
      });

      expect(useAuthStore.getState().user).toBeNull();
      expect(useAuthStore.getState().isAuthenticated).toBe(false);
    });

    test('should clear auth when getCurrentUser fails', async () => {
      (api.isAuthenticated as jest.Mock).mockReturnValue(true);
      (api.getCurrentUser as jest.Mock).mockRejectedValue(new Error('Unauthorized'));

      const store = useAuthStore.getState();
      await act(async () => {
        await store.checkAuth();
      });

      expect(api.clearTokens).toHaveBeenCalled();
      expect(useAuthStore.getState().user).toBeNull();
      expect(useAuthStore.getState().isAuthenticated).toBe(false);
    });
  });

  describe('login', () => {
    test('should login successfully and set user', async () => {
      const store = useAuthStore.getState();
      await act(async () => {
        await store.login('test@example.com', 'password123');
      });

      expect(api.login).toHaveBeenCalledWith('test@example.com', 'password123');
      expect(useAuthStore.getState().user).toEqual(mockUser);
      expect(useAuthStore.getState().isAuthenticated).toBe(true);
      expect(useAuthStore.getState().isLoading).toBe(false);
    });

    test('should handle login error', async () => {
      (api.login as jest.Mock).mockRejectedValue(new Error('Invalid credentials'));

      const store = useAuthStore.getState();
      await act(async () => {
        try {
          await store.login('test@example.com', 'wrongpassword');
        } catch (e) {
          // Error expected
        }
      });

      expect(useAuthStore.getState().error).toBe('Login failed');
      expect(useAuthStore.getState().isLoading).toBe(false);
    });
  });

  describe('register', () => {
    test('should register successfully and set user', async () => {
      const store = useAuthStore.getState();
      await act(async () => {
        await store.register('test@example.com', 'password123', 'Test User');
      });

      expect(api.register).toHaveBeenCalledWith('test@example.com', 'password123', 'Test User');
      expect(useAuthStore.getState().user).toEqual(mockUser);
      expect(useAuthStore.getState().isAuthenticated).toBe(true);
    });

    test('should handle register error', async () => {
      (api.register as jest.Mock).mockRejectedValue(new Error('Email already exists'));

      const store = useAuthStore.getState();
      await act(async () => {
        try {
          await store.register('test@example.com', 'password123', 'Test User');
        } catch (e) {
          // Error expected
        }
      });

      expect(useAuthStore.getState().error).toBe('Registration failed');
    });
  });

  describe('logout', () => {
    test('should logout successfully', async () => {
      useAuthStore.setState({ user: mockUser, isAuthenticated: true });

      const store = useAuthStore.getState();
      await act(async () => {
        await store.logout();
      });

      expect(api.logout).toHaveBeenCalled();
      expect(useAuthStore.getState().user).toBeNull();
      expect(useAuthStore.getState().isAuthenticated).toBe(false);
    });
  });

  describe('updateProfile', () => {
    test('should update profile successfully', async () => {
      useAuthStore.setState({ user: mockUser, isAuthenticated: true });

      const updatedUser = { ...mockUser, name: 'Updated Name' };
      (api.updateProfile as jest.Mock).mockResolvedValue(updatedUser);

      const store = useAuthStore.getState();
      await act(async () => {
        await store.updateProfile({ name: 'Updated Name' });
      });

      expect(api.updateProfile).toHaveBeenCalledWith({ name: 'Updated Name' });
      expect(useAuthStore.getState().user).toEqual(updatedUser);
    });

    test('should handle update error', async () => {
      useAuthStore.setState({ user: mockUser, isAuthenticated: true });
      (api.updateProfile as jest.Mock).mockRejectedValue(new Error('Update failed'));

      const store = useAuthStore.getState();
      await act(async () => {
        try {
          await store.updateProfile({ name: 'Updated Name' });
        } catch (e) {
          // Error expected
        }
      });

      expect(useAuthStore.getState().error).toBe('Update failed');
    });
  });

  describe('changePassword', () => {
    test('should change password successfully', async () => {
      const store = useAuthStore.getState();
      await act(async () => {
        await store.changePassword('oldPass', 'newPass');
      });

      expect(api.changePassword).toHaveBeenCalledWith('oldPass', 'newPass');
    });

    test('should handle change password error', async () => {
      (api.changePassword as jest.Mock).mockRejectedValue(new Error('Password change failed'));

      const store = useAuthStore.getState();
      await act(async () => {
        try {
          await store.changePassword('wrongOldPass', 'newPass');
        } catch (e) {
          // Error expected
        }
      });

      expect(useAuthStore.getState().error).toBe('Password change failed');
    });
  });

  describe('initial state', () => {
    test('should have correct default values', () => {
      const state = useAuthStore.getState();
      expect(state.user).toBeNull();
      expect(state.isAuthenticated).toBe(false);
      expect(state.isLoading).toBe(false);
      expect(state.error).toBeNull();
    });
  });
});
