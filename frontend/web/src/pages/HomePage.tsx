import { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity, ActivityIndicator } from 'react-native';
import { useBusinessStore, useDataStore, useAuthStore } from '@alpha/shared';
import { useNavigate } from 'react-router-dom';

export const HomePage = () => {
  const { featuredBusinesses, fetchFeaturedBusinesses, isLoading } = useBusinessStore();
  const { categories, fetchCategories } = useDataStore();
  const { isAuthenticated } = useAuthStore();
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
            data-testid="home-search-input"
            style={styles.searchInput}
            placeholder="Search for businesses, services, or categories..."
            placeholderTextColor="#adb5bd"
            value={searchQuery}
            onChangeText={setSearchQuery}
            onSubmitEditing={handleSearch}
          />
          <TouchableOpacity 
            data-testid="home-search-button"
            style={styles.searchButton} 
            onPress={handleSearch}
          >
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
          <TouchableOpacity 
            data-testid="home-cta-button"
            style={styles.ctaButton} 
            onPress={() => navigate('/register')}
          >
            <Text style={styles.ctaButtonText}>Get Started</Text>
          </TouchableOpacity>
        </View>
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  pageContainer: {
    flex: 1,
    padding: 20,
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
  emptyState: {
    padding: 40,
    alignItems: 'center',
  },
  emptyStateText: {
    fontSize: 16,
    color: '#6c757d',
  },
});

export default HomePage;
