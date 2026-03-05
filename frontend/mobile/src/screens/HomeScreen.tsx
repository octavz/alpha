import React from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity } from 'react-native';

export const HomeScreen = () => {
  const [searchQuery, setSearchQuery] = React.useState('');

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
          />
          <TouchableOpacity style={styles.searchButton}>
            <Text style={styles.searchButtonText}>Search</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Popular Categories</Text>
        <View style={styles.categoryGrid}>
          {['Beauty & Salon', 'Health & Wellness', 'Home Services', 'Automotive', 'Food & Dining', 'Retail'].map((cat) => (
            <TouchableOpacity key={cat} style={styles.categoryCard}>
              <Text style={styles.categoryTitle}>{cat}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Featured Businesses</Text>
        <Text style={styles.sectionSubtitle}>Highly rated businesses in your area</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          {[1, 2, 3].map((i) => (
            <TouchableOpacity key={i} style={styles.businessCard}>
              <View style={styles.businessImagePlaceholder}>
                <Text style={styles.businessImageText}>B</Text>
              </View>
              <Text style={styles.businessName}>Business {i}</Text>
              <Text style={styles.businessCategory}>★ 4.5 (120 reviews)</Text>
              <Text style={styles.businessDescription}>Quality services with excellent customer satisfaction</Text>
              <TouchableOpacity style={styles.bookButton}>
                <Text style={styles.bookButtonText}>Book Now</Text>
              </TouchableOpacity>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>
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
});