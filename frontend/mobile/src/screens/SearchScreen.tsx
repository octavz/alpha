import React from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity } from 'react-native';

export const SearchScreen = () => {
  const [searchQuery, setSearchQuery] = React.useState('');

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
        />
        <TouchableOpacity style={styles.searchButton}>
          <Text style={styles.searchButtonText}>Search</Text>
        </TouchableOpacity>
      </View>

      <ScrollView style={styles.resultsList}>
        {[1, 2, 3, 4, 5].map((i) => (
          <View key={i} style={styles.resultCard}>
            <Text style={styles.resultName}>Business {i}</Text>
            <Text style={styles.resultDescription}>Quality services with excellent customer satisfaction</Text>
            <Text style={styles.resultRating}>★ 4.5 (120 reviews)</Text>
          </View>
        ))}
      </ScrollView>
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
  resultRating: {
    fontSize: 14,
    color: '#6c757d',
  },
});