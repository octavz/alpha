import React, { useEffect } from 'react'
import { View, ActivityIndicator, StyleSheet } from 'react-native'
import { useAuth } from '../contexts/AuthContext'
import { useTheme } from '../contexts/ThemeContext'

export const LoadingScreen: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth()
  const { colors } = useTheme()

  useEffect(() => {
    // Navigation is handled by the navigator based on auth state
  }, [isAuthenticated, isLoading])

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <ActivityIndicator size="large" color={colors.primary} />
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