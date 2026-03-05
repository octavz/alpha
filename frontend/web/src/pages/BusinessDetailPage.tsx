import { useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { useNavigate } from 'react-router-dom';
import { useBusinessStore } from '@alpha/shared';

interface BusinessDetailPageProps {
  id: string;
}

export const BusinessDetailPage = ({ id }: BusinessDetailPageProps) => {
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
      <TouchableOpacity onPress={() => navigate(-1)} data-testid="business-back-button">
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

      <TouchableOpacity 
        style={styles.bookButton} 
        onPress={() => navigate(`/booking/${currentBusiness.id}`)}
        data-testid="business-book-button"
      >
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