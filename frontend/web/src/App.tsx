import { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { View, Text, StyleSheet } from 'react-native';
import { useAuthStore } from '@alpha/shared';

// Import pages from modular structure
import { HomePage } from './pages/HomePage';
import { SearchPage } from './pages/SearchPage';
import { BusinessDetailPageWrapper } from './pages/BusinessDetailPageWrapper';
import { BookingPageWrapper } from './pages/BookingPageWrapper';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ProfilePage } from './pages/ProfilePage';

function App() {
  const { isAuthenticated, checkAuth, user } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, []);

  return (
    <Router>
      <View style={styles.container}>
        <View style={styles.header}>
          <Link to="/" data-testid="header-logo">
            <Text style={styles.headerTitle} data-testid="header-logo-text">Alpha Directory</Text>
          </Link>
          <View style={styles.headerActions}>
            {isAuthenticated ? (
              <Link to="/profile" data-testid="header-profile-link">
                <Text style={styles.headerButton} data-testid="header-profile-text">{user?.name || 'Profile'}</Text>
              </Link>
            ) : (
              <>
                <Link to="/login" data-testid="header-login-link">
                  <Text style={styles.headerButton} data-testid="header-login-text">Sign in</Text>
                </Link>
                <Link to="/register" data-testid="header-register-link">
                  <Text style={styles.headerButton} data-testid="header-register-text">Register</Text>
                </Link>
              </>
            )}
          </View>
        </View>

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/business/:id" element={<BusinessDetailPageWrapper />} />
          <Route path="/booking/:businessId" element={<BookingPageWrapper />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Routes>

        <View style={styles.footer}>
          <Text style={styles.footerText}>© 2026 Alpha Business Directory</Text>
        </View>
      </View>
    </Router>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderBottomWidth: 1,
    borderBottomColor: '#dee2e6',
    paddingHorizontal: 20,
    paddingVertical: 16,
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#212529',
  },
  headerActions: {
    flexDirection: 'row',
    gap: 16,
  },
  headerButton: {
    fontSize: 16,
    color: '#007bff',
    fontWeight: '600',
  },
  footer: {
    backgroundColor: '#343a40',
    padding: 24,
    alignItems: 'center',
  },
  footerText: {
    fontSize: 14,
    color: '#adb5bd',
  },
});

export default App;
