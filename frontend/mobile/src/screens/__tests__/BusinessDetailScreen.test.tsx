import { render } from '@testing-library/react-native';
import React from 'react';
import { BusinessDetailScreen } from '../BusinessDetailScreen';

describe('BusinessDetailScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<BusinessDetailScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays business detail elements', () => {
    const { getByText } = render(<BusinessDetailScreen />);
    
    expect(getByText(/Business Name/i)).toBeTruthy();
    expect(getByText(/✓ Verified/i)).toBeTruthy();
    expect(getByText(/★ 4.5 \(120 reviews\)/i)).toBeTruthy();
    expect(getByText(/Description/i)).toBeTruthy();
    expect(getByText(/Book Appointment/i)).toBeTruthy();
  });
});