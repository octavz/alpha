import React from 'react';
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
} from 'react-native';
import { Input, Card, Button } from 'react-native-elements';

export const LoginScreen = () => {
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(false);

  const handleLogin = async () => {
    Keyboard.dismiss();
    setIsLoading(true);
    
    try {
      // Simulate login
      await new Promise(resolve => setTimeout(resolve, 1000));
      Alert.alert('Success', 'Login successful!');
    } catch (error) {
      Alert.alert('Login Failed', 'Invalid credentials');
    } finally {
      setIsLoading(false);
    }
  };

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
              disabled={isLoading}
              titleStyle={styles.signUpButton}
            />
          </View>
        </Card>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

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
});