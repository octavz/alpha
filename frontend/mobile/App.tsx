import React from 'react'
import { SafeAreaProvider } from 'react-native-safe-area-context'
import { NavigationContainer } from '@react-navigation/native'
import { createStackNavigator } from '@react-navigation/stack'
import { StatusBar } from 'react-native'

import { AuthProvider } from './src/contexts/AuthContext'
import { LoadingScreen } from './src/screens/LoadingScreen'
import { LoginScreen } from './src/screens/LoginScreen'
// TODO: Create these screens
// import { RegisterScreen } from './src/screens/RegisterScreen'
// import { HomeScreen } from './src/screens/HomeScreen'

const Stack = createStackNavigator()

// Simple placeholder screens for now
const RegisterScreen = () => null
const HomeScreen = () => null

export default function App() {
  return (
    <SafeAreaProvider>
      <AuthProvider>
        <NavigationContainer>
          <StatusBar barStyle="dark-content" />
          <Stack.Navigator
            initialRouteName="Loading"
            screenOptions={{
              headerShown: false,
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
  )
}