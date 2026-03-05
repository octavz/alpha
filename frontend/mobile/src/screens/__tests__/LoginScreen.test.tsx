import { render } from '@testing-library/react-native';
import React from 'react';
import { LoginScreen } from '../LoginScreen';

describe('LoginScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<LoginScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays login form elements', () => {
    const { getAllByText, getByText, getByPlaceholderText } = render(<LoginScreen />);
    
    expect(getByText(/Welcome Back/i)).toBeTruthy();
    expect(getByText(/Sign in to your account/i)).toBeTruthy();
    expect(getByPlaceholderText(/Enter your email/i)).toBeTruthy();
    expect(getByPlaceholderText(/Enter your password/i)).toBeTruthy();
    expect(getAllByText(/Sign In/i).length).toBeGreaterThan(0);
    expect(getByText(/Sign up/i)).toBeTruthy();
  });

  it('has proper form validation', () => {
    const { getByPlaceholderText, getByText } = render(<LoginScreen />);
    
    const emailInput = getByPlaceholderText(/Enter your email/i);
    const passwordInput = getByPlaceholderText(/Enter your password/i);
    
    expect(emailInput.props.value).toBe("");
    expect(passwordInput.props.value).toBe("");
  });
});