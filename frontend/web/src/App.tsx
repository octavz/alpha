import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useSearchParams, useParams, Link } from 'react-router-dom';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity, ActivityIndicator, Alert } from 'react-native';
import { useAuthStore } from './stores/authStore';
import { useBusinessStore } from './stores/businessStore';
import { useDataStore } from './stores/dataStore';
import api from './api/client';

const API_URL = 'http://localhost:3000';

const HomePage = () => {
  const { featuredBusinesses, fetchFeaturedBusinesses, isLoading } = useBusinessStore();
  const { categories, fetchCategories } = useDataStore();
  const { isAuthenticated, user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchFeaturedBusinesses();
    fetchCategories();
  }, []);

  const handleSearch = () => {
    navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
  };

  return (
    <ScrollView contentContainerStyle={styles.pageContainer}>
      <View style={styles.hero}>
        <Text style={styles.heroTitle}>Find Local Businesses Near You</Text>
        <Text style={styles.heroSubtitle}>
          Discover and book appointments with businesses in your region
        </Text>
        <View style={styles.searchBox}>
          <TextInput
            style={styles.searchInput}
            placeholder="Search for businesses, services, or categories..."
            placeholderTextColor="#adb5bd"
            value={searchQuery}
            onChangeText={setSearchQuery}
            onSubmitEditing={handleSearch}
          />
          <TouchableOpacity style={styles.searchButton} onPress={handleSearch}>
            <Text style={styles.searchButtonText}>Search</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Popular Categories</Text>
        <View style={styles.categoryGrid}>
          {categories.length > 0 ? categories.map((cat) => (
            <TouchableOpacity
              key={cat.id}
              style={styles.categoryCard}
              onPress={() => navigate(`/search?category=${cat.id}`)}
            >
              <Text style={styles.categoryTitle}>{cat.name}</Text>
            </TouchableOpacity>
          )) : (
            ['Beauty & Salon', 'Health & Wellness', 'Home Services', 'Automotive', 'Food & Dining', 'Retail'].map((cat) => (
              <TouchableOpacity key={cat} style={styles.categoryCard} onPress={() => navigate('/search')}>
                <Text style={styles.categoryTitle}>{cat}</Text>
              </TouchableOpacity>
            ))
          )}
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Featured Businesses</Text>
        <Text style={styles.sectionSubtitle}>Highly rated businesses in your area</Text>
        {isLoading ? (
          <ActivityIndicator size="large" color="#007bff" />
        ) : (
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            {featuredBusinesses.length > 0 ? featuredBusinesses.map((business) => (
              <TouchableOpacity
                key={business.id}
                style={styles.businessCard}
                onPress={() => navigate(`/business/${business.id}`)}
              >
                <View style={styles.businessImagePlaceholder}>
                  <Text style={styles.businessImageText}>{business.name.charAt(0)}</Text>
                </View>
                <Text style={styles.businessName}>{business.name}</Text>
                <Text style={styles.businessCategory}>★ {business.rating?.toFixed(1) || '4.5'} ({business.reviewCount || 0} reviews)</Text>
                <Text style={styles.businessDescription} numberOfLines={2}>{business.description}</Text>
                <TouchableOpacity style={styles.bookButton} onPress={() => navigate(`/booking/${business.id}`)}>
                  <Text style={styles.bookButtonText}>Book Now</Text>
                </TouchableOpacity>
              </TouchableOpacity>
            )) : (
              <View style={styles.emptyState}>
                <Text style={styles.emptyStateText}>No featured businesses yet</Text>
              </View>
            )}
          </ScrollView>
        )}
      </View>

      {!isAuthenticated && (
        <View style={styles.ctaSection}>
          <Text style={styles.ctaTitle}>Own a business?</Text>
          <Text style={styles.ctaText}>List your business on Alpha and reach more customers</Text>
          <TouchableOpacity style={styles.ctaButton} onPress={() => navigate('/register')}>
            <Text style={styles.ctaButtonText}>Get Started</Text>
          </TouchableOpacity>
        </View>
      )}
    </ScrollView>
  );
};

const SearchPage = () => {
  const { businesses, fetchBusinesses, isLoading } = useBusinessStore();
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const categoryId = searchParams.get('category') || '';
  const [searchQuery, setSearchQuery] = useState(query);

  useEffect(() => {
    fetchBusinesses({ query: searchQuery, categoryId });
  }, [searchQuery, categoryId]);

  const handleSearch = () => {
    fetchBusinesses({ query: searchQuery, categoryId });
  };

  return (
    <View style={styles.pageContainer}>
      <Text style={styles.pageTitle}>Search Businesses</Text>
      <View style={styles.searchBox}>
        <TextInput
          style={styles.searchInput}
          placeholder="Search..."
          placeholderTextColor="#adb5bd"
          value={searchQuery}
          onChangeText={setSearchQuery}
          onSubmitEditing={handleSearch}
        />
        <TouchableOpacity style={styles.searchButton} onPress={handleSearch}>
          <Text style={styles.searchButtonText}>Search</Text>
        </TouchableOpacity>
      </View>

      {isLoading ? (
        <ActivityIndicator size="large" color="#007bff" />
      ) : (
        <ScrollView style={styles.resultsList}>
          {businesses.length > 0 ? businesses.map((business) => (
            <TouchableOpacity key={business.id} style={styles.resultCard}>
              <Text style={styles.resultName}>{business.name}</Text>
              <Text style={styles.resultDescription}>{business.description}</Text>
              <Text style={styles.resultMeta}>★ {business.rating?.toFixed(1) || '0.0'} • {business.city}</Text>
            </TouchableOpacity>
          )) : (
            <Text style={styles.emptyText}>No businesses found</Text>
          )}
        </ScrollView>
      )}
    </View>
  );
};

const BusinessDetailPage = ({ id }: { id: string }) => {
  const { currentBusiness, fetchBusiness, isLoading } = useBusinessStore();
  const navigate = useNavigate();

  useEffect(() => {
    fetchBusiness(id);
  }, [id]);

  if (isLoading) {
    return <ActivityIndicator size="large" color="#007bff" />;
  }

  if (!currentBusiness) {
    return <Text>Business not found</Text>;
  }

  return (
    <ScrollView contentContainerStyle={styles.pageContainer}>
      <TouchableOpacity onPress={() => navigate(-1)}>
        <Text style={styles.backButton}>← Back</Text>
      </TouchableOpacity>
      
      <View style={styles.detailHeader}>
        <View style={styles.detailImagePlaceholder}>
          <Text style={styles.detailImageText}>{currentBusiness.name.charAt(0)}</Text>
        </View>
        <Text style={styles.detailName}>{currentBusiness.name}</Text>
        {currentBusiness.isVerified && (
          <Text style={styles.verifiedBadge}>✓ Verified</Text>
        )}
        <Text style={styles.detailRating}>★ {currentBusiness.rating?.toFixed(1) || '0.0'} ({currentBusiness.reviewCount || 0} reviews)</Text>
      </View>

      <View style={styles.detailSection}>
        <Text style={styles.detailLabel}>Description</Text>
        <Text style={styles.detailText}>{currentBusiness.description}</Text>
      </View>

      <View style={styles.detailSection}>
        <Text style={styles.detailLabel}>Location</Text>
        <Text style={styles.detailText}>{currentBusiness.address}, {currentBusiness.city}</Text>
      </View>

      {currentBusiness.phone && (
        <View style={styles.detailSection}>
          <Text style={styles.detailLabel}>Phone</Text>
          <Text style={styles.detailText}>{currentBusiness.phone}</Text>
        </View>
      )}

      {currentBusiness.email && (
        <View style={styles.detailSection}>
          <Text style={styles.detailLabel}>Email</Text>
          <Text style={styles.detailText}>{currentBusiness.email}</Text>
        </View>
      )}

      {currentBusiness.website && (
        <View style={styles.detailSection}>
          <Text style={styles.detailLabel}>Website</Text>
          <Text style={styles.detailText}>{currentBusiness.website}</Text>
        </View>
      )}

      <TouchableOpacity style={styles.bookButton} onPress={() => navigate(`/booking/${currentBusiness.id}`)}>
        <Text style={styles.bookButtonText}>Book Appointment</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const BookingPage = ({ businessId }: { businessId: string }) => {
  const { currentBusiness, fetchBusiness } = useBusinessStore();
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (businessId) {
      fetchBusiness(businessId);
    }
  }, [businessId]);

  if (!isAuthenticated) {
    return (
      <View style={styles.pageContainer}>
        <Text style={styles.pageTitle}>Please login to book</Text>
        <TouchableOpacity style={styles.ctaButton} onPress={() => navigate('/login')}>
          <Text style={styles.ctaButtonText}>Login</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.pageContainer}>
      <TouchableOpacity onPress={() => navigate(-1)}>
        <Text style={styles.backButton}>← Back</Text>
      </TouchableOpacity>
      
      <Text style={styles.pageTitle}>Book Appointment</Text>
      {currentBusiness && (
        <Text style={styles.bookingBusinessName}>{currentBusiness.name}</Text>
      )}
      
      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Date & Time</Text>
        <TextInput style={styles.formInput} placeholder="Select date and time" placeholderTextColor="#adb5bd" />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Notes (optional)</Text>
        <TextInput style={styles.formInput} placeholder="Any special requests?" placeholderTextColor="#adb5bd" multiline numberOfLines={3} />
      </View>

      <TouchableOpacity style={styles.bookButton}>
        <Text style={styles.bookButtonText}>Confirm Booking</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading, error, clearError } = useAuthStore();
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      Alert.alert('Login Failed', error || 'Invalid credentials');
    }
  };

  return (
    <View style={styles.pageContainer}>
      <Text style={styles.pageTitle}>Login</Text>
      <Text style={styles.pageSubtitle}>Welcome back!</Text>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Email</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Enter your email"
          placeholderTextColor="#adb5bd"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Password</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Enter your password"
          placeholderTextColor="#adb5bd"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />
      </View>

      {error && <Text style={styles.errorText}>{error}</Text>}

      <TouchableOpacity style={styles.primaryButton} onPress={handleLogin} disabled={isLoading}>
        {isLoading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.primaryButtonText}>Login</Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigate('/register')}>
        <Text style={styles.linkText}>Don't have an account? Register</Text>
      </TouchableOpacity>
    </View>
  );
};

const RegisterPage = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { register, isLoading, error } = useAuthStore();
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      await register(email, password, name);
      navigate('/');
    } catch (err) {
      Alert.alert('Registration Failed', error || 'Please try again');
    }
  };

  return (
    <View style={styles.pageContainer}>
      <Text style={styles.pageTitle}>Create Account</Text>
      <Text style={styles.pageSubtitle}>Join Alpha today!</Text>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Name</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Enter your name"
          placeholderTextColor="#adb5bd"
          value={name}
          onChangeText={setName}
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Email</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Enter your email"
          placeholderTextColor="#adb5bd"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Password</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Create a password"
          placeholderTextColor="#adb5bd"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />
      </View>

      {error && <Text style={styles.errorText}>{error}</Text>}

      <TouchableOpacity style={styles.primaryButton} onPress={handleRegister} disabled={isLoading}>
        {isLoading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.primaryButtonText}>Register</Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigate('/login')}>
        <Text style={styles.linkText}>Already have an account? Login</Text>
      </TouchableOpacity>
    </View>
  );
};

const ProfilePage = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <View style={styles.pageContainer}>
      <Text style={styles.pageTitle}>Profile</Text>
      {user && (
        <>
          <View style={styles.profileCard}>
            <Text style={styles.profileName}>{user.name}</Text>
            <Text style={styles.profileEmail}>{user.email}</Text>
            <Text style={styles.profileRole}>{user.role}</Text>
          </View>
          <TouchableOpacity style={styles.dangerButton} onPress={handleLogout}>
            <Text style={styles.dangerButtonText}>Logout</Text>
          </TouchableOpacity>
        </>
      )}
    </View>
  );
};

function App() {
  const { isAuthenticated, checkAuth, user } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, []);

  return (
    <Router>
      <View style={styles.container}>
        <View style={styles.header}>
          <Link to="/">
            <Text style={styles.headerTitle}>Alpha Directory</Text>
          </Link>
          <View style={styles.headerActions}>
            {isAuthenticated ? (
              <Link to="/profile">
                <Text style={styles.headerButton}>{user?.name || 'Profile'}</Text>
              </Link>
            ) : (
              <Link to="/login">
                <Text style={styles.headerButton}>Login</Text>
              </Link>
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
  },
  headerButton: {
    fontSize: 16,
    color: '#007bff',
    fontWeight: '600',
    marginLeft: 16,
  },
  pageContainer: {
    flex: 1,
    padding: 20,
  },
  pageTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
  },
  pageSubtitle: {
    fontSize: 16,
    color: '#6c757d',
    marginBottom: 24,
  },
  hero: {
    alignItems: 'center',
    marginBottom: 40,
    paddingTop: 20,
  },
  heroTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#212529',
    textAlign: 'center',
    marginBottom: 12,
  },
  heroSubtitle: {
    fontSize: 18,
    color: '#6c757d',
    textAlign: 'center',
    marginBottom: 30,
    maxWidth: 600,
  },
  searchBox: {
    flexDirection: 'row',
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
    alignItems: 'center',
    width: '100%',
    maxWidth: 800,
  },
  searchInput: {
    flex: 1,
    fontSize: 16,
    paddingHorizontal: 12,
    paddingVertical: 12,
    color: '#212529',
  },
  searchButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingHorizontal: 24,
    paddingVertical: 12,
  },
  searchButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
  },
  section: {
    marginBottom: 40,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
  },
  sectionSubtitle: {
    fontSize: 16,
    color: '#6c757d',
    marginBottom: 20,
  },
  categoryGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  categoryCard: {
    width: '48%',
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  categoryTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#212529',
  },
  businessCard: {
    width: 280,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 16,
    marginRight: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  businessImagePlaceholder: {
    width: '100%',
    height: 160,
    backgroundColor: '#e9ecef',
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  businessImageText: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#adb5bd',
  },
  businessName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 4,
  },
  businessCategory: {
    fontSize: 14,
    color: '#6c757d',
    marginBottom: 8,
  },
  businessDescription: {
    fontSize: 14,
    color: '#495057',
    lineHeight: 20,
    marginBottom: 16,
  },
  bookButton: {
    backgroundColor: '#28a745',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
  },
  bookButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
  },
  ctaSection: {
    backgroundColor: '#007bff',
    borderRadius: 12,
    padding: 24,
    alignItems: 'center',
    marginBottom: 40,
  },
  ctaTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#ffffff',
    marginBottom: 8,
  },
  ctaText: {
    fontSize: 16,
    color: 'rgba(255,255,255,0.9)',
    marginBottom: 16,
    textAlign: 'center',
  },
  ctaButton: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    paddingHorizontal: 32,
    paddingVertical: 12,
  },
  ctaButtonText: {
    color: '#007bff',
    fontWeight: '600',
    fontSize: 16,
  },
  resultsList: {
    flex: 1,
    marginTop: 20,
  },
  resultCard: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  resultName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 4,
  },
  resultDescription: {
    fontSize: 14,
    color: '#495057',
    marginBottom: 8,
  },
  resultMeta: {
    fontSize: 14,
    color: '#6c757d',
  },
  emptyState: {
    padding: 40,
    alignItems: 'center',
  },
  emptyStateText: {
    fontSize: 16,
    color: '#6c757d',
  },
  emptyText: {
    fontSize: 16,
    color: '#6c757d',
    textAlign: 'center',
    marginTop: 40,
  },
  detailHeader: {
    alignItems: 'center',
    marginBottom: 24,
  },
  detailImagePlaceholder: {
    width: 120,
    height: 120,
    backgroundColor: '#e9ecef',
    borderRadius: 60,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
  },
  detailImageText: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#adb5bd',
  },
  detailName: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
  },
  verifiedBadge: {
    fontSize: 14,
    color: '#28a745',
    fontWeight: '600',
    marginBottom: 8,
  },
  detailRating: {
    fontSize: 16,
    color: '#6c757d',
  },
  detailSection: {
    marginBottom: 20,
  },
  detailLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#6c757d',
    marginBottom: 4,
    textTransform: 'uppercase',
  },
  detailText: {
    fontSize: 16,
    color: '#212529',
  },
  backButton: {
    fontSize: 16,
    color: '#007bff',
    marginBottom: 16,
  },
  bookingBusinessName: {
    fontSize: 18,
    color: '#495057',
    marginBottom: 24,
  },
  formGroup: {
    marginBottom: 20,
  },
  formLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#495057',
    marginBottom: 8,
  },
  formInput: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#dee2e6',
    color: '#212529',
  },
  primaryButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
    marginBottom: 16,
  },
  primaryButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
  },
  linkText: {
    fontSize: 16,
    color: '#007bff',
    textAlign: 'center',
  },
  errorText: {
    fontSize: 14,
    color: '#dc3545',
    marginBottom: 16,
  },
  profileCard: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 24,
    marginBottom: 24,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  profileName: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 4,
  },
  profileEmail: {
    fontSize: 16,
    color: '#6c757d',
    marginBottom: 8,
  },
  profileRole: {
    fontSize: 14,
    color: '#007bff',
    fontWeight: '600',
  },
  dangerButton: {
    backgroundColor: '#dc3545',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  dangerButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
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

const BusinessDetailPageWrapper = () => {
  const { id } = useParams<{ id: string }>();
  return <BusinessDetailPage id={id || ''} />;
};

const BookingPageWrapper = () => {
  const { businessId } = useParams<{ businessId: string }>();
  return <BookingPage businessId={businessId || ''} />;
};

export default App;
