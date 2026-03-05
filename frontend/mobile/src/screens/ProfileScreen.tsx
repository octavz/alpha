import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Alert } from 'react-native';

export const ProfileScreen = () => {
  const handleLogout = async () => {
    Alert.alert('Logout', 'Are you sure you want to logout?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Logout', style: 'destructive', onPress: () => {
        Alert.alert('Success', 'Logged out successfully!');
      }},
    ]);
  };

  return (
    <View style={styles.pageContainer}>
      <Text style={styles.pageTitle}>Profile</Text>
      
      <View style={styles.profileCard}>
        <Text style={styles.profileName}>John Doe</Text>
        <Text style={styles.profileEmail}>john.doe@example.com</Text>
        <Text style={styles.profileRole}>User</Text>
      </View>
      
      <TouchableOpacity style={styles.dangerButton} onPress={handleLogout}>
        <Text style={styles.dangerButtonText}>Logout</Text>
      </TouchableOpacity>
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
});