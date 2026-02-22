import React from 'react'
import { SafeAreaProvider } from 'react-native-safe-area-context'
import { NavigationContainer } from '@react-navigation/native'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { StatusBar } from 'react-native'
import { TamaguiProvider, config } from '@alpha/shared'
import { AuthProvider } from './src/contexts/AuthContext'
import { LoadingScreen } from './src/screens/LoadingScreen'
import { LoginScreen } from './src/screens/LoginScreen'
import { RegisterScreen } from './src/screens/RegisterScreen'
import { HomeScreen } from './src/screens/HomeScreen'

const Stack = createNativeStackNavigator()

export default function App() {
  return (
    <TamaguiProvider config={config} defaultTheme="light">
      <SafeAreaProvider>
        <AuthProvider>
          <NavigationContainer>
            <StatusBar barStyle="dark-content" />
            <Stack.Navigator
              initialRouteName="Loading"
              screenOptions={{
                headerShown: false,
                animation: 'fade',
              }}
            >
              <Stack.Screen name="Loading" component={LoadingScreen} />
              <Stack.Screen name="Login" component={LoginScreen} />
              <Stack.Screen name="Register" component={RegisterScreen} />
              <Stack.Screen name="Home" component={HomeScreen} />
            </Stack.Navigator>
          </NavigationContainer>
        </AuthProvider>
      </SafeAreaProvider>
    </TamaguiProvider>
  )
}