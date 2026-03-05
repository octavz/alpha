import { render } from '@testing-library/react-native';
import React from 'react';
import { ProfileScreen } from '../ProfileScreen';

describe('ProfileScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<ProfileScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays profile page elements', () => {
    const { getByText } = render(<ProfileScreen />);
    
    expect(getByText(/Profile/i)).toBeTruthy();
  });
});