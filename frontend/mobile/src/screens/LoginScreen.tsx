import React, { useState } from 'react'
import { 
  Alert, 
  Platform, 
  Keyboard, 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  KeyboardAvoidingView,
  ActivityIndicator 
} from 'react-native'
import { Input, Card, Button } from 'react-native-elements'
import { useAuth } from '../contexts/AuthContext'

export const LoginScreen: React.FC = () => {
  const { login, isLoading } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState<Record<string, string>>({})

  const validateEmail = (email: string) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return re.test(email)
  }

  const validateForm = () => {
    const newErrors: Record<string, string> = {}
    
    if (!email) {
      newErrors.email = 'Email is required'
    } else if (!validateEmail(email)) {
      newErrors.email = 'Please enter a valid email'
    }
    
    if (!password) {
      newErrors.password = 'Password is required'
    } else if (password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters'
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleLogin = async () => {
    Keyboard.dismiss()
    
    if (!validateForm()) return

    try {
      await login(email, password)
      // Navigation is handled by the auth context
    } catch (error) {
      Alert.alert(
        'Login Failed',
        error instanceof Error ? error.message : 'An error occurred during login',
        [{ text: 'OK' }]
      )
    }
  }

  const handleRegister = () => {
    // Navigation to register screen is handled by the navigator
  }

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={{ flex: 1 }}
    >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.header}>
          <Text style={styles.title}>Welcome Back</Text>
          <Text style={styles.subtitle}>Sign in to your account</Text>
        </View>

        <Card containerStyle={styles.card}>
          <Input
            label="Email"
            value={email}
            onChangeText={setEmail}
            placeholder="Enter your email"
            errorMessage={errors.email}
            disabled={isLoading}
            autoCapitalize="none"
            keyboardType="email-address"
            containerStyle={styles.inputContainer}
          />

          <Input
            label="Password"
            value={password}
            onChangeText={setPassword}
            placeholder="Enter your password"
            errorMessage={errors.password}
            disabled={isLoading}
            secureTextEntry
            containerStyle={styles.inputContainer}
          />

          <Button
            title={isLoading ? 'Signing in...' : 'Sign In'}
            onPress={handleLogin}
            loading={isLoading}
            disabled={isLoading}
            containerStyle={styles.buttonContainer}
            buttonStyle={styles.loginButton}
          />
        </Card>

        <Card containerStyle={styles.footerCard}>
          <View style={styles.footerContent}>
            <Text style={styles.footerText}>Don't have an account?</Text>
            <Button
              title="Sign up"
              type="clear"
              onPress={handleRegister}
              disabled={isLoading}
              titleStyle={styles.signUpButton}
            />
          </View>
        </Card>

        <View style={styles.bottomNote}>
          <Text style={styles.noteText}>
            Built with React Native Elements • Cross-platform UI
          </Text>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  )
}

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    backgroundColor: '#f8f9fa',
    padding: 20,
    justifyContent: 'center',
  },
  header: {
    alignItems: 'center',
    marginBottom: 30,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#6c757d',
    textAlign: 'center',
  },
  card: {
    borderRadius: 12,
    borderWidth: 0,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
    marginBottom: 20,
  },
  inputContainer: {
    paddingHorizontal: 0,
    marginBottom: 20,
  },
  buttonContainer: {
    marginTop: 10,
  },
  loginButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingVertical: 12,
  },
  footerCard: {
    borderRadius: 12,
    borderWidth: 0,
    backgroundColor: '#e9ecef',
    marginBottom: 20,
  },
  footerContent: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 12,
  },
  footerText: {
    fontSize: 16,
    color: '#495057',
    marginRight: 8,
  },
  signUpButton: {
    color: '#007bff',
    fontWeight: '600',
  },
  bottomNote: {
    alignItems: 'center',
    paddingTop: 20,
    borderTopWidth: 1,
    borderTopColor: '#dee2e6',
  },
  noteText: {
    fontSize: 14,
    color: '#adb5bd',
    textAlign: 'center',
  },
})
}