import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';

export const BusinessDetailScreen = () => {
  return (
    <ScrollView contentContainerStyle={styles.pageContainer}>
      <TouchableOpacity>
        <Text style={styles.backButton}>← Back</Text>
      </TouchableOpacity>
      
      <View style={styles.detailHeader}>
        <View style={styles.detailImagePlaceholder}>
          <Text style={styles.detailImageText}>B</Text>
        </View>
        <Text style={styles.detailName}>Business Name</Text>
        <Text style={styles.verifiedBadge}>✓ Verified</Text>
        <Text style={styles.detailRating}>★ 4.5 (120 reviews)</Text>
      </View>

      <View style={styles.detailSection}>
        <Text style={styles.detailLabel}>Description</Text>
        <Text style={styles.detailText}>Quality services with excellent customer satisfaction and professional staff.</Text>
      </View>

      <View style={styles.detailSection}>
        <Text style={styles.detailLabel}>Location</Text>
        <Text style={styles.detailText}>123 Main St, City, State 12345</Text>
      </View>

      <View style={styles.detailSection}>
        <Text style={styles.detailLabel}>Phone</Text>
        <Text style={styles.detailText}>(123) 456-7890</Text>
      </View>

      <View style={styles.detailSection}>
        <Text style={styles.detailLabel}>Email</Text>
        <Text style={styles.detailText}>contact@business.com</Text>
      </View>

      <TouchableOpacity style={styles.bookButton}>
        <Text style={styles.bookButtonText}>Book Appointment</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  pageContainer: {
    flex: 1,
    padding: 20,
  },
  backButton: {
    fontSize: 16,
    color: '#007bff',
    marginBottom: 16,
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
  bookButton: {
    backgroundColor: '#28a745',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 20,
  },
  bookButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
  },
});