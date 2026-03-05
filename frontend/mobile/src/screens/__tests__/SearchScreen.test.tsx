import { render } from '@testing-library/react-native';
import React from 'react';
import { SearchScreen } from '../SearchScreen';

describe('SearchScreen', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<SearchScreen />);
    expect(toJSON()).toMatchSnapshot();
  });

  it('displays search page elements', () => {
    const { getAllByText, getByText, getByPlaceholderText } = render(<SearchScreen />);
    
    expect(getByText(/Search Businesses/i)).toBeTruthy();
    expect(getByPlaceholderText(/Search.../i)).toBeTruthy();
    expect(getAllByText(/Search/i).length).toBeGreaterThan(0);
  });
});