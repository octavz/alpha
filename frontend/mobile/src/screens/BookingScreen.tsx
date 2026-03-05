import React from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity } from 'react-native';

export const BookingScreen = () => {
  return (
    <ScrollView contentContainerStyle={styles.pageContainer}>
      <TouchableOpacity>
        <Text style={styles.backButton}>← Back</Text>
      </TouchableOpacity>
      
      <Text style={styles.pageTitle}>Book Appointment</Text>
      <Text style={styles.bookingBusinessName}>Business Name</Text>
      
      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Date & Time</Text>
        <TextInput 
          style={styles.formInput} 
          placeholder="Select date and time" 
          placeholderTextColor="#adb5bd" 
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Notes (optional)</Text>
        <TextInput 
          style={styles.formInput} 
          placeholder="Any special requests?" 
          placeholderTextColor="#adb5bd" 
          multiline 
          numberOfLines={3} 
        />
      </View>

      <TouchableOpacity style={styles.bookButton}>
        <Text style={styles.bookButtonText}>Confirm Booking</Text>
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
  pageTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
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