import React, { useEffect } from 'react'
import { View, ActivityIndicator, StyleSheet } from 'react-native'
import { useAuth } from '../contexts/AuthContext'

export const LoadingScreen: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth()

  useEffect(() => {
    // Navigation is handled by the navigator based on auth state
  }, [isAuthenticated, isLoading])

  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color="#007bff" />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  }
})