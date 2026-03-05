import { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity, ActivityIndicator } from 'react-native';
import { useSearchParams } from 'react-router-dom';
import { useBusinessStore } from '@alpha/shared';
import { ResultCard } from '../components/ResultCard';

export const SearchPage = () => {
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
          data-testid="home-search-input"
        />
        <TouchableOpacity style={styles.searchButton} onPress={handleSearch} data-testid="home-search-button">
          <Text style={styles.searchButtonText}>Search</Text>
        </TouchableOpacity>
      </View>

      {isLoading ? (
        <ActivityIndicator size="large" color="#007bff" />
      ) : (
        <ScrollView style={styles.resultsList}>
          {businesses.length > 0 ? businesses.map((business) => (
            <ResultCard
              key={business.id}
              name={business.name}
              description={business.description}
              rating={business.rating}
              city={business.city}
              onPress={() => {}}
            />
          )) : (
            <Text style={styles.emptyText}>No businesses found</Text>
          )}
        </ScrollView>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
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
  resultsList: {
    flex: 1,
    marginTop: 20,
  },
  emptyText: {
    fontSize: 16,
    color: '#6c757d',
    textAlign: 'center',
    marginTop: 40,
  },
});