import { Text, TouchableOpacity, StyleSheet } from 'react-native';

interface CategoryCardProps {
  name: string;
  onPress: () => void;
}

export const CategoryCard = ({ name, onPress }: CategoryCardProps) => {
  return (
    <TouchableOpacity style={styles.categoryCard} onPress={onPress}>
      <Text style={styles.categoryTitle}>{name}</Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  categoryCard: {
    width: '48%',
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 20,
    marginBottom: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  categoryTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#212529',
  },
});