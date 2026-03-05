import { useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity } from 'react-native';
import { useNavigate } from 'react-router-dom';
import { useBusinessStore, useAuthStore } from '@alpha/shared';

interface BookingPageProps {
  businessId: string;
}

export const BookingPage = ({ businessId }: BookingPageProps) => {
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
        <TouchableOpacity 
          style={styles.ctaButton} 
          onPress={() => navigate('/login')}
          data-testid="booking-login-button"
        >
          <Text style={styles.ctaButtonText}>Login</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.pageContainer}>
      <TouchableOpacity onPress={() => navigate(-1)} data-testid="booking-back-button">
        <Text style={styles.backButton}>← Back</Text>
      </TouchableOpacity>
      
      <Text style={styles.pageTitle}>Book Appointment</Text>
      {currentBusiness && (
        <Text style={styles.bookingBusinessName}>{currentBusiness.name}</Text>
      )}
      
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

      <TouchableOpacity 
        style={styles.bookButton}
        data-testid="booking-confirm-button"
      >
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
  ctaButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 20,
  },
  ctaButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
  },
});