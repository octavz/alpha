import { render } from '@testing-library/react-native';
import React from 'react';
import { HomeScreen } from '../HomeScreen';

describe('HomeScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<HomeScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays home page elements', () => {
    const { getByText, queryByText } = render(<HomeScreen />);
    
    expect(getByText(/Find Local Businesses Near You/i)).toBeTruthy();
    expect(getByText(/Discover and book appointments with businesses in your region/i)).toBeTruthy();
    expect(getByText(/Search/i)).toBeTruthy();
    expect(getByText(/Popular Categories/i)).toBeTruthy();
    expect(getByText(/Featured Businesses/i)).toBeTruthy();
  });
});