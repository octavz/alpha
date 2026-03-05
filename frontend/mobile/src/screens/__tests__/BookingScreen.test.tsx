import { render } from '@testing-library/react-native';
import React from 'react';
import { BookingScreen } from '../BookingScreen';

describe('BookingScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<BookingScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays booking page elements', () => {
    const { getByText, getByPlaceholderText } = render(<BookingScreen />);
    
    expect(getByText(/Book Appointment/i)).toBeTruthy();
    expect(getByText(/Business Name/i)).toBeTruthy();
    expect(getByText(/Date & Time/i)).toBeTruthy();
    expect(getByText(/Notes \(optional\)/i)).toBeTruthy();
    expect(getByPlaceholderText(/Select date and time/i)).toBeTruthy();
    expect(getByText(/Confirm Booking/i)).toBeTruthy();
  });
});