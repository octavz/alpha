import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

interface BusinessCardProps {
  name: string;
  rating?: number;
  reviewCount?: number;
  description: string;
  onPress: () => void;
  onBookPress: () => void;
}

export const BusinessCard = ({
  name,
  rating = 4.5,
  reviewCount = 0,
  description,
  onPress,
  onBookPress
}: BusinessCardProps) => {
  return (
    <TouchableOpacity style={styles.businessCard} onPress={onPress}>
      <View style={styles.businessImagePlaceholder}>
        <Text style={styles.businessImageText}>{name.charAt(0)}</Text>
      </View>
      <Text style={styles.businessName}>{name}</Text>
      <Text style={styles.businessCategory}>★ {rating.toFixed(1)} ({reviewCount} reviews)</Text>
      <Text style={styles.businessDescription} numberOfLines={2}>{description}</Text>
      <TouchableOpacity style={styles.bookButton} onPress={onBookPress}>
        <Text style={styles.bookButtonText}>Book Now</Text>
      </TouchableOpacity>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
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