import { render } from '@testing-library/react-native';
import React from 'react';
import { RegisterScreen } from '../RegisterScreen';

describe('RegisterScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<RegisterScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays register page elements', () => {
    const { getByText, getByPlaceholderText } = render(<RegisterScreen />);
    
    expect(getByText(/Create Account/i)).toBeTruthy();
    expect(getByText(/Join Alpha today!/i)).toBeTruthy();
    expect(getByPlaceholderText(/Enter your name/i)).toBeTruthy();
    expect(getByPlaceholderText(/Enter your email/i)).toBeTruthy();
    expect(getByPlaceholderText(/Create a password/i)).toBeTruthy();
    expect(getByText(/Register/i)).toBeTruthy();
  });
});