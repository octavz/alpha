import { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, ActivityIndicator, Alert } from 'react-native';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@alpha/shared';

export const RegisterPage = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { register, isLoading, error } = useAuthStore();
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      await register(email, password, name);
      navigate('/');
    } catch (err) {
      Alert.alert('Registration Failed', error || 'Please try again');
    }
  };

  return (
    <View style={styles.pageContainer}>
      <Text data-testid="register-title" style={styles.pageTitle}>Create Account</Text>
      <Text style={styles.pageSubtitle}>Join Alpha today!</Text>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Name</Text>
        <TextInput
          data-testid="register-name"
          style={styles.formInput}
          placeholder="Enter your name"
          placeholderTextColor="#adb5bd"
          value={name}
          onChangeText={setName}
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Email</Text>
        <TextInput
          data-testid="register-email"
          style={styles.formInput}
          placeholder="Enter your email"
          placeholderTextColor="#adb5bd"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.formLabel}>Password</Text>
        <TextInput
          data-testid="register-password"
          style={styles.formInput}
          placeholder="Create a password"
          placeholderTextColor="#adb5bd"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />
      </View>

      {error && <Text style={styles.errorText}>{error}</Text>}

      <TouchableOpacity style={styles.primaryButton} onPress={handleRegister} disabled={isLoading} data-testid="register-submit">
        {isLoading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.primaryButtonText}>Register</Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigate('/login')} data-testid="login-link">
        <Text style={styles.linkText}>Already have an account? Login</Text>
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
  linkText: {
    fontSize: 16,
    color: '#007bff',
    textAlign: 'center',
  },
  errorText: {
    fontSize: 14,
    color: '#dc3545',
    marginBottom: 16,
    textAlign: 'center',
  },
});

export default RegisterPage;
