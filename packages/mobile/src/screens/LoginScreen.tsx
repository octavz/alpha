import React, { useState } from 'react'
import { Alert, Platform, Keyboard } from 'react-native'
import { useAuth } from '../contexts/AuthContext'
import { 
  YStack, 
  XStack, 
  Card, 
  CardHeader, 
  CardContent, 
  CardFooter,
  Heading,
  Paragraph,
  Separator,
  AlphaButton,
  InputField,
  Text,
  Spinner,
  ScrollView,
  KeyboardAvoidingView
} from '@alpha/shared'

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
        contentContainerStyle={{ flexGrow: 1 }}
        keyboardShouldPersistTaps="handled"
      >
        <YStack 
          flex={1} 
          backgroundColor="$background" 
          justifyContent="center" 
          alignItems="center"
          padding="$4"
        >
          <Card 
            width="100%" 
            maxWidth={400} 
            elevate 
            size="$4"
            animation="bouncy"
          >
            <CardHeader>
              <YStack alignItems="center" space="$2">
                <Heading size="$8" color="$color">
                  Welcome Back
                </Heading>
                <Paragraph color="$color" opacity={0.7}>
                  Sign in to your account
                </Paragraph>
              </YStack>
            </CardHeader>

            <CardContent>
              <YStack space="$4">
                <InputField
                  label="Email"
                  value={email}
                  onChangeText={setEmail}
                  placeholder="Enter your email"
                  error={errors.email}
                  disabled={isLoading}
                  autoCapitalize="none"
                  keyboardType="email-address"
                />

                <InputField
                  label="Password"
                  value={password}
                  onChangeText={setPassword}
                  placeholder="Enter your password"
                  error={errors.password}
                  disabled={isLoading}
                  secureTextEntry
                />

                <AlphaButton
                  onPress={handleLogin}
                  loading={isLoading}
                  disabled={isLoading}
                  icon={isLoading ? <Spinner /> : undefined}
                >
                  {isLoading ? 'Signing in...' : 'Sign In'}
                </AlphaButton>
              </YStack>
            </CardContent>

            <Separator marginVertical="$4" />

            <CardFooter>
              <XStack justifyContent="center" alignItems="center" space="$2">
                <Paragraph color="$color" opacity={0.7}>
                  Don't have an account?
                </Paragraph>
                <AlphaButton
                  variant="ghost"
                  size="sm"
                  onPress={handleRegister}
                  disabled={isLoading}
                >
                  <Text color="$primary" fontWeight="600">
                    Sign up
                  </Text>
                </AlphaButton>
              </XStack>
            </CardFooter>
          </Card>

          <YStack marginTop="$4" alignItems="center">
            <Paragraph size="$1" color="$color" opacity={0.5}>
              Built with Tamagui • Cross-platform UI
            </Paragraph>
          </YStack>
        </YStack>
      </ScrollView>
    </KeyboardAvoidingView>
  )
}