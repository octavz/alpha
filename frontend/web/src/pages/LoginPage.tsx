import { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, ActivityIndicator, Alert } from 'react-native';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@alpha/shared';

export const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading, error } = useAuthStore();
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      Alert.alert('Login Failed', error || 'Invalid credentials');
    }
  };

  return (
    <View style={styles.pageContainer}>
      <Text data-testid="login-title" style={styles.pageTitle}>Login</Text>
      <Text style={styles.pageSubtitle}>Welcome back!</Text>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Email</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Enter your email"
          placeholderTextColor="#adb5bd"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
          data-testid="login-email"
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Password</Text>
        <TextInput
          style={styles.formInput}
          placeholder="Enter your password"
          placeholderTextColor="#adb5bd"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          data-testid="login-password"
        />
      </View>

      {error && <Text style={styles.errorText}>{error}</Text>}

      <TouchableOpacity 
        style={styles.primaryButton} 
        onPress={handleLogin} 
        disabled={isLoading}
        data-testid="login-submit"
      >
        {isLoading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.primaryButtonText}>Login</Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity 
        onPress={() => navigate('/register')}
        data-testid="register-link"
      >
        <Text style={styles.linkText}>Don't have an account? Register</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  pageContainer: {
    flex: 1,
    padding: 20,
    justifyContent: 'center',
  },
  pageTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
    textAlign: 'center',
  },
  pageSubtitle: {
    fontSize: 16,
    color: '#6c757d',
    marginBottom: 24,
    textAlign: 'center',
  },
  formGroup: {
    marginBottom: 20,
  },
  formLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#495057',
    marginBottom: 8,
  },
  formInput: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#dee2e6',
    color: '#212529',
  },
  primaryButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
    marginBottom: 16,
  },
  primaryButtonText: {
    color: '#ffffff',
    fontWeight: '600',
    fontSize: 16,
  },
  errorText: {
    fontSize: 14,
    color: '#dc3545',
    marginBottom: 16,
    textAlign: 'center',
  },
  linkText: {
    fontSize: 16,
    color: '#007bff',
    textAlign: 'center',
  },
});