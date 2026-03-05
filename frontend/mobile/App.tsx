import React from 'react'
import { SafeAreaProvider } from 'react-native-safe-area-context'
import { NavigationContainer } from '@react-navigation/native'
import { createStackNavigator } from '@react-navigation/stack'
import { StatusBar } from 'react-native'

import { AuthProvider } from './src/contexts/AuthContext'
import { LoadingScreen } from './src/screens/LoadingScreen'
import { LoginScreen } from './src/screens/LoginScreen'
import { RegisterScreen } from './src/screens/RegisterScreen'
import { HomeScreen } from './src/screens/HomeScreen'
import { SearchScreen } from './src/screens/SearchScreen'
import { BusinessDetailScreen } from './src/screens/BusinessDetailScreen'
import { BookingScreen } from './src/screens/BookingScreen'
import { ProfileScreen } from './src/screens/ProfileScreen'

const Stack = createStackNavigator()

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
            <Stack.Screen name="Search" component={SearchScreen} />
            <Stack.Screen name="BusinessDetail" component={BusinessDetailScreen} />
            <Stack.Screen name="Booking" component={BookingScreen} />
            <Stack.Screen name="Profile" component={ProfileScreen} />
          </Stack.Navigator>
        </NavigationContainer>
      </AuthProvider>
    </SafeAreaProvider>
  )
}