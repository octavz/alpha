import { useParams } from 'react-router-dom';
import { BusinessDetailPage } from './BusinessDetailPage';

export const BusinessDetailPageWrapper = () => {
  const { id } = useParams<{ id: string }>();
  return <BusinessDetailPage id={id || ''} />;
};