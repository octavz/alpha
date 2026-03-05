import { Text, TouchableOpacity, StyleSheet } from 'react-native';

interface ResultCardProps {
  name: string;
  description: string;
  rating?: number;
  city?: string;
  onPress: () => void;
}

export const ResultCard = ({
  name,
  description,
  rating = 0,
  city,
  onPress
}: ResultCardProps) => {
  return (
    <TouchableOpacity style={styles.resultCard} onPress={onPress}>
      <Text style={styles.resultName}>{name}</Text>
      <Text style={styles.resultDescription}>{description}</Text>
      <Text style={styles.resultMeta}>★ {rating.toFixed(1)} {city ? `• ${city}` : ''}</Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  resultCard: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  resultName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 4,
  },
  resultDescription: {
    fontSize: 14,
    color: '#495057',
    marginBottom: 8,
  },
  resultMeta: {
    fontSize: 14,
    color: '#6c757d',
  },
});