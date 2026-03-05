import { useParams } from 'react-router-dom';
import { BookingPage } from './BookingPage';

export const BookingPageWrapper = () => {
  const { businessId } = useParams<{ businessId: string }>();
  return <BookingPage businessId={businessId || ''} />;
};