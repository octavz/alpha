import React from 'react'
import { View, Text, StyleSheet, ScrollView } from 'react-native'
import { Button } from 'react-native-elements'

function SimpleApp() {
  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Alpha Business Directory</Text>
        <Text style={styles.subtitle}>
          Welcome to the business directory platform. Now using React Native Elements.
        </Text>
      </View>

      <View style={styles.buttonRow}>
        <Button
          title="Search Businesses"
          buttonStyle={styles.primaryButton}
          containerStyle={styles.buttonContainer}
        />
        <Button
          title="View Categories"
          buttonStyle={styles.secondaryButton}
          containerStyle={styles.buttonContainer}
        />
        <Button
          title="Book Appointment"
          buttonStyle={styles.successButton}
          containerStyle={styles.buttonContainer}
        />
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Coming Soon</Text>
        <View style={styles.cardDivider} />
        <Text style={styles.cardText}>
          • Region-based business search
          {'\n'}• Business profiles with detailed information
          {'\n'}• Appointment booking system
          {'\n'}• Customer and business dashboards
          {'\n'}• Admin management panel
        </Text>
      </View>

      <View style={styles.statusCard}>
        <Text style={styles.cardTitle}>Development Status</Text>
        <View style={styles.cardDivider} />
        <Text style={styles.statusText}>
          <Text style={styles.bold}>Backend Status:</Text> ✅ Complete with 168 test businesses, full-text search, appointment system, and admin APIs.
          {'\n\n'}
          <Text style={styles.bold}>Frontend Status:</Text> 🔄 Switching to React Native Elements for unified web+mobile experience.
        </Text>
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerText}>
          Built with React Native Elements • Cross-platform UI
        </Text>
      </View>
    </ScrollView>
  )
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#f8f9fa',
    minHeight: '100%',
  },
  header: {
    marginBottom: 30,
    alignItems: 'center',
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 10,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 18,
    color: '#6c757d',
    textAlign: 'center',
    maxWidth: 600,
  },
  buttonRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    marginBottom: 40,
  },
  buttonContainer: {
    marginHorizontal: 6,
    marginVertical: 6,
  },
  primaryButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingHorizontal: 20,
    paddingVertical: 12,
  },
  secondaryButton: {
    backgroundColor: '#6c757d',
    borderRadius: 8,
    paddingHorizontal: 20,
    paddingVertical: 12,
  },
  successButton: {
    backgroundColor: '#28a745',
    borderRadius: 8,
    paddingHorizontal: 20,
    paddingVertical: 12,
  },
  card: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#dee2e6',
  },
  cardTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 12,
  },
  cardDivider: {
    height: 1,
    backgroundColor: '#dee2e6',
    marginBottom: 16,
  },
  cardText: {
    fontSize: 16,
    lineHeight: 24,
    color: '#6c757d',
  },
  statusCard: {
    backgroundColor: '#e9ecef',
    borderRadius: 12,
    padding: 20,
    marginBottom: 30,
    borderWidth: 1,
    borderColor: '#dee2e6',
  },
  statusText: {
    fontSize: 16,
    lineHeight: 24,
    color: '#495057',
  },
  bold: {
    fontWeight: 'bold',
  },
  footer: {
    alignItems: 'center',
    paddingTop: 20,
    borderTopWidth: 1,
    borderTopColor: '#dee2e6',
  },
  footerText: {
    fontSize: 14,
    color: '#adb5bd',
    textAlign: 'center',
  },
})

export default SimpleApp