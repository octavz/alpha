import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { View, Text, StyleSheet, ScrollView } from 'react-native'
import { Button, Card } from 'react-native-elements'

// Placeholder components for different pages
const HomePage = () => (
  <ScrollView contentContainerStyle={styles.pageContainer}>
    <View style={styles.hero}>
      <Text style={styles.heroTitle}>Find Local Businesses Near You</Text>
      <Text style={styles.heroSubtitle}>
        Discover and book appointments with businesses in your region
      </Text>
      <View style={styles.searchBox}>
        <Text style={styles.searchPlaceholder}>Search for businesses, services, or categories...</Text>
        <Button title="Search" buttonStyle={styles.searchButton} />
      </View>
    </View>

    <View style={styles.section}>
      <Text style={styles.sectionTitle}>Popular Categories</Text>
      <View style={styles.categoryGrid}>
        {['Beauty & Salon', 'Health & Wellness', 'Home Services', 'Automotive', 'Food & Dining', 'Retail'].map((cat) => (
          <Card key={cat} containerStyle={styles.categoryCard}>
            <Card.Title style={styles.categoryTitle}>{cat}</Card.Title>
            <Text style={styles.categoryCount}>150+ businesses</Text>
          </Card>
        ))}
      </View>
    </View>

    <View style={styles.section}>
      <Text style={styles.sectionTitle}>Featured Businesses</Text>
      <Text style={styles.sectionSubtitle}>Highly rated businesses in your area</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false}>
        {[1, 2, 3, 4].map((i) => (
          <Card key={i} containerStyle={styles.businessCard}>
            <Card.Image 
              source={{ uri: `https://picsum.photos/300/200?random=${i}` }}
              style={styles.businessImage}
            />
            <Card.Title style={styles.businessName}>Business {i}</Card.Title>
            <Text style={styles.businessCategory}>Category • ★ 4.5 (120 reviews)</Text>
            <Text style={styles.businessDescription}>
              Providing excellent services with experienced professionals.
            </Text>
            <Button title="Book Now" buttonStyle={styles.bookButton} />
          </Card>
        ))}
      </ScrollView>
    </View>
  </ScrollView>
)

const SearchPage = () => (
  <View style={styles.pageContainer}>
    <Text>Search Page</Text>
  </View>
)

const BusinessDetailPage = () => (
  <View style={styles.pageContainer}>
    <Text>Business Detail Page</Text>
  </View>
)

const BookingPage = () => (
  <View style={styles.pageContainer}>
    <Text>Booking Page</Text>
  </View>
)

const LoginPage = () => (
  <View style={styles.pageContainer}>
    <Text>Login Page</Text>
  </View>
)

// Main App component with routing
function App() {
  return (
    <Router>
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Alpha Directory</Text>
          <Button title="Login" type="clear" titleStyle={styles.loginButton} />
        </View>
        
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/business/:id" element={<BusinessDetailPage />} />
          <Route path="/booking" element={<BookingPage />} />
          <Route path="/login" element={<LoginPage />} />
        </Routes>

        <View style={styles.footer}>
          <Text style={styles.footerText}>© 2026 Alpha Business Directory</Text>
          <Text style={styles.footerSubtext}>Find and book local businesses</Text>
        </View>
      </View>
    </Router>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderBottomWidth: 1,
    borderBottomColor: '#dee2e6',
    paddingHorizontal: 20,
    paddingVertical: 16,
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#212529',
  },
  loginButton: {
    color: '#007bff',
    fontWeight: '600',
  },
  pageContainer: {
    flex: 1,
    padding: 20,
  },
  hero: {
    alignItems: 'center',
    marginBottom: 40,
    paddingTop: 20,
  },
  heroTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#212529',
    textAlign: 'center',
    marginBottom: 12,
  },
  heroSubtitle: {
    fontSize: 18,
    color: '#6c757d',
    textAlign: 'center',
    marginBottom: 30,
    maxWidth: 600,
  },
  searchBox: {
    flexDirection: 'row',
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
    alignItems: 'center',
    width: '100%',
    maxWidth: 800,
  },
  searchPlaceholder: {
    flex: 1,
    fontSize: 16,
    color: '#adb5bd',
  },
  searchButton: {
    backgroundColor: '#007bff',
    borderRadius: 8,
    paddingHorizontal: 24,
    paddingVertical: 10,
  },
  section: {
    marginBottom: 40,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
  },
  sectionSubtitle: {
    fontSize: 16,
    color: '#6c757d',
    marginBottom: 20,
  },
  categoryGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 16,
  },
  categoryCard: {
    width: '48%',
    borderRadius: 12,
    borderWidth: 0,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
    margin: 0,
    marginBottom: 16,
  },
  categoryTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#212529',
    textAlign: 'center',
  },
  categoryCount: {
    fontSize: 14,
    color: '#6c757d',
    textAlign: 'center',
    marginTop: 4,
  },
  businessCard: {
    width: 280,
    borderRadius: 12,
    borderWidth: 0,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
    marginRight: 20,
    paddingBottom: 20,
  },
  businessImage: {
    height: 160,
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
  },
  businessName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#212529',
    marginTop: 12,
  },
  businessCategory: {
    fontSize: 14,
    color: '#6c757d',
    marginBottom: 8,
  },
  businessDescription: {
    fontSize: 14,
    color: '#495057',
    lineHeight: 20,
    marginBottom: 16,
  },
  bookButton: {
    backgroundColor: '#28a745',
    borderRadius: 8,
    paddingVertical: 10,
  },
  footer: {
    backgroundColor: '#343a40',
    padding: 24,
    alignItems: 'center',
  },
  footerText: {
    fontSize: 16,
    color: '#ffffff',
    fontWeight: '500',
    marginBottom: 4,
  },
  footerSubtext: {
    fontSize: 14,
    color: '#adb5bd',
  },
})

export default App