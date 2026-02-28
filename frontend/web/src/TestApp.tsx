import React from 'react'
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom'
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity } from 'react-native'
import { Button } from 'react-native-elements'
import { apiClient } from '@alpha/shared'

// Public Search Page Component
const PublicSearchPage = () => {
  const navigate = useNavigate()
  const [searchQuery, setSearchQuery] = React.useState('')
  const [selectedRegion, setSelectedRegion] = React.useState('')
  const [showRegionDropdown, setShowRegionDropdown] = React.useState(false)
  const [regions, setRegions] = React.useState<Array<{id: string, name: string, code: string, country: string}>>([])
  const [categories, setCategories] = React.useState<Array<{id: string, name: string, icon: string}>>([])
  const [featuredBusinesses, setFeaturedBusinesses] = React.useState<Array<{id: number, name: string, category: string, rating: number, reviews: number, image: string}>>([])

  const defaultCategories = [
    { id: 'beauty', name: 'Beauty & Salon', icon: '💇' },
    { id: 'health', name: 'Health & Wellness', icon: '🏥' },
    { id: 'home', name: 'Home Services', icon: '🏠' },
    { id: 'automotive', name: 'Automotive', icon: '🚗' },
    { id: 'food', name: 'Food & Dining', icon: '🍽️' },
    { id: 'retail', name: 'Retail', icon: '🛍️' },
    { id: 'education', name: 'Education', icon: '🎓' },
    { id: 'professional', name: 'Professional Services', icon: '💼' },
  ]

  const defaultFeaturedBusinesses = [
    { id: 1, name: 'Elite Beauty Salon', category: 'Beauty & Salon', rating: 4.8, reviews: 124, image: 'https://picsum.photos/300/200?random=1' },
    { id: 2, name: 'Wellness Medical Center', category: 'Health & Wellness', rating: 4.9, reviews: 89, image: 'https://picsum.photos/300/200?random=2' },
    { id: 3, name: 'AutoFix Garage', category: 'Automotive', rating: 4.7, reviews: 203, image: 'https://picsum.photos/300/200?random=3' },
    { id: 4, name: 'HomeCare Services', category: 'Home Services', rating: 4.6, reviews: 156, image: 'https://picsum.photos/300/200?random=4' },
  ]

  React.useEffect(() => {
    // Load categories and featured businesses on mount
    setCategories(defaultCategories)
    setFeaturedBusinesses(defaultFeaturedBusinesses)
    
    // Use mock regions for now (backend has database issue)
    const mockRegions = [
      { id: 'nyc', name: 'New York City', code: 'NYC', country: 'USA' },
      { id: 'la', name: 'Los Angeles', code: 'LA', country: 'USA' },
      { id: 'chi', name: 'Chicago', code: 'CHI', country: 'USA' },
      { id: 'mia', name: 'Miami', code: 'MIA', country: 'USA' },
      { id: 'hou', name: 'Houston', code: 'HOU', country: 'USA' },
      { id: 'phi', name: 'Philadelphia', code: 'PHI', country: 'USA' },
      { id: 'phx', name: 'Phoenix', code: 'PHX', country: 'USA' },
      { id: 'san', name: 'San Antonio', code: 'SAN', country: 'USA' },
      { id: 'sd', name: 'San Diego', code: 'SD', country: 'USA' },
      { id: 'dal', name: 'Dallas', code: 'DAL', country: 'USA' },
    ]
    setRegions(mockRegions)
    if (!selectedRegion) {
      setSelectedRegion(mockRegions[0].id)
    }
    
    // Try to check API health (but don't depend on it for regions)
    const checkApiHealth = async () => {
      try {
        const healthResult = await apiClient.healthCheck()
        console.log('API health:', healthResult)
      } catch (error) {
        console.log('API health check failed:', error)
      }
    }
    
    checkApiHealth()
  }, [])

  // Close dropdown when clicking outside
  React.useEffect(() => {
    const handleClickOutside = () => {
      if (showRegionDropdown) {
        setShowRegionDropdown(false)
      }
    }

    // Add event listener
    document.addEventListener('click', handleClickOutside)
    
    return () => {
      document.removeEventListener('click', handleClickOutside)
    }
  }, [showRegionDropdown])

  const handleSearch = () => {
    console.log('Searching for:', searchQuery, 'in region:', selectedRegion)
    // Navigate to search results page
    navigate('/search')
  }

  const handleRegionSelect = (regionId: string) => {
    setSelectedRegion(regionId)
    setShowRegionDropdown(false)
    console.log('Region changed to:', regionId)
    // TODO: Fetch businesses for new region
  }

  return (
    <ScrollView style={styles.container}>
      {/* Header with Logo, Region Selector and User Actions */}
      <View style={styles.header}>
        <View style={styles.logoContainer}>
          <Text style={styles.logo}>AlphaDirectory</Text>
          <Text style={styles.logoSubtitle}>Find Local Businesses</Text>
        </View>
        <View style={styles.headerControls}>
          <View style={styles.regionSelector}>
            <Text style={styles.regionLabel}>Region:</Text>
            <TouchableOpacity 
              style={styles.regionDropdown}
              onPress={() => setShowRegionDropdown(!showRegionDropdown)}
            >
              <Text style={styles.regionText}>
                {regions.find(r => r.id === selectedRegion)?.name}
              </Text>
            </TouchableOpacity>
            
            {showRegionDropdown && (
              <View style={styles.regionDropdownMenu}>
                {regions.map((region) => (
                  <TouchableOpacity
                    key={region.id}
                    style={[
                      styles.regionDropdownItem,
                      selectedRegion === region.id && styles.regionDropdownItemSelected
                    ]}
                    onPress={() => handleRegionSelect(region.id)}
                  >
                    <Text style={[
                      styles.regionDropdownItemText,
                      selectedRegion === region.id && styles.regionDropdownItemTextSelected
                    ]}>
                      {region.name}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            )}
          </View>
          <View style={styles.userActions}>
            <Button
              title="Sign in"
              type="clear"
              onPress={() => navigate('/login')}
              titleStyle={styles.signInButton}
            />
            <Button
              title="Register"
              buttonStyle={styles.registerButton}
              onPress={() => navigate('/register')}
            />
          </View>
        </View>
      </View>

      {/* Hero Section */}
      <View style={styles.hero}>
        <Text style={styles.heroTitle}>Find the best local businesses</Text>
        <Text style={styles.heroSubtitle}>Search for services, read reviews, and book appointments</Text>
        
        {/* Search Box */}
        <View style={styles.searchContainer}>
          <View style={styles.searchBox}>
            <TextInput
              style={styles.searchInput}
              placeholder="What are you looking for?"
              value={searchQuery}
              onChangeText={setSearchQuery}
              onSubmitEditing={handleSearch}
              returnKeyType="search"
            />
            <Button
              title="Search"
              buttonStyle={styles.searchButton}
              onPress={handleSearch}
            />
          </View>
        </View>
      </View>

      {/* Categories */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Browse by Category</Text>
        <View style={styles.categoryContainer}>
          <View style={styles.categoryGrid}>
            {categories.map((category) => (
              <View key={category.id} style={styles.categoryCard}>
                <View style={styles.categoryIconContainer}>
                  <Text style={styles.categoryIcon}>{category.icon}</Text>
                </View>
                <Text style={styles.categoryName}>{category.name}</Text>
              </View>
            ))}
          </View>
        </View>
      </View>

      {/* Featured Businesses */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Featured Businesses</Text>
        <Text style={styles.sectionSubtitle}>Highly rated businesses in {regions.find(r => r.id === selectedRegion)?.name}</Text>
        <View style={styles.businessContainer}>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.businessScroll}>
            {featuredBusinesses.map((business) => (
              <View key={business.id} style={styles.businessCard}>
                <View style={styles.businessImageContainer}>
                  <View style={styles.businessImagePlaceholder}>
                    <Text style={styles.businessImageText}>Image</Text>
                  </View>
                </View>
                <Text style={styles.businessName}>{business.name}</Text>
                <Text style={styles.businessCategory}>{business.category}</Text>
                <View style={styles.ratingContainer}>
                  <Text style={styles.rating}>★ {business.rating}</Text>
                  <Text style={styles.reviews}>({business.reviews})</Text>
                </View>
                <Button
                  title="View Details"
                  type="clear"
                  titleStyle={styles.viewDetailsButton}
                  onPress={() => navigate(`/business/${business.id}`)}
                />
              </View>
            ))}
          </ScrollView>
        </View>
      </View>

      {/* Business Registration Section */}
      <View style={styles.businessSection}>
        <View style={styles.businessContent}>
          <Text style={styles.businessTitle}>Are you a business owner?</Text>
          <Text style={styles.businessDescription}>
            List your business on AlphaDirectory to reach more customers, manage appointments, and grow your business.
          </Text>
          <View style={styles.businessButtons}>
            <Button
              title="Business Login"
              type="outline"
              buttonStyle={styles.businessLoginButton}
              titleStyle={styles.businessLoginText}
              onPress={() => navigate('/business/login')}
            />
            <Button
              title="Register Business"
              buttonStyle={styles.registerBusinessButton}
              onPress={() => navigate('/business/register')}
            />
          </View>
        </View>
      </View>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>© 2026 AlphaDirectory. All rights reserved.</Text>
        <View style={styles.footerLinks}>
          <Text style={styles.footerLink}>About</Text>
          <Text style={styles.footerLink}>Privacy</Text>
          <Text style={styles.footerLink}>Terms</Text>
          <Text style={styles.footerLink}>Contact</Text>
          <Text style={styles.footerLink}>Help</Text>
        </View>
      </View>
    </ScrollView>
  )
}

// Login Page Component
const LoginPage = () => {
  const navigate = useNavigate()
  const [email, setEmail] = React.useState('')
  const [password, setPassword] = React.useState('')

  return (
    <View style={styles.loginContainer}>
      <View style={styles.loginHeader}>
        <Button
          title="← Back to Search"
          type="clear"
          onPress={() => navigate('/')}
          titleStyle={styles.backButton}
        />
      </View>
      
      <View style={styles.loginCard}>
        <Text style={styles.loginTitle}>Sign in to AlphaDirectory</Text>
        <Text style={styles.loginSubtitle}>Access your account and manage your bookings</Text>
        
        <View style={styles.inputContainer}>
          <Text style={styles.inputLabel}>Email</Text>
          <TextInput
            style={styles.input}
            placeholder="your@email.com"
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            autoCapitalize="none"
          />
        </View>
        
        <View style={styles.inputContainer}>
          <Text style={styles.inputLabel}>Password</Text>
          <TextInput
            style={styles.input}
            placeholder="Enter your password"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
          />
        </View>
        
        <Button
          title="Sign in"
          buttonStyle={styles.loginButton}
          onPress={() => console.log('Login attempt')}
        />
        
        <View style={styles.divider}>
          <View style={styles.dividerLine} />
          <Text style={styles.dividerText}>or</Text>
          <View style={styles.dividerLine} />
        </View>
        
        <Button
          title="Sign in with Google"
          type="outline"
          buttonStyle={styles.googleButton}
          titleStyle={styles.googleButtonText}
          icon={{
            name: 'google',
            type: 'font-awesome',
            size: 16,
            color: '#4285F4',
          }}
          onPress={() => console.log('Google login')}
        />
        
        <View style={styles.loginFooter}>
          <Text style={styles.loginFooterText}>Don't have an account?</Text>
          <Button
            title="Create account"
            type="clear"
            titleStyle={styles.createAccountButton}
            onPress={() => navigate('/register')}
          />
        </View>
      </View>
    </View>
  )
}

// Register Page Component (similar to login)
const RegisterPage = () => {
  const navigate = useNavigate()
  
  return (
    <View style={styles.loginContainer}>
      <View style={styles.loginHeader}>
        <Button
          title="← Back to Search"
          type="clear"
          onPress={() => navigate('/')}
          titleStyle={styles.backButton}
        />
      </View>
      
      <View style={styles.loginCard}>
        <Text style={styles.loginTitle}>Create your account</Text>
        <Text style={styles.loginSubtitle}>Join AlphaDirectory to book appointments and manage your bookings</Text>
        
        {/* Registration form would go here */}
        <Text style={styles.placeholderText}>Registration form will be implemented here</Text>
        
        <Button
          title="Continue with Google"
          type="outline"
          buttonStyle={styles.googleButton}
          titleStyle={styles.googleButtonText}
          icon={{
            name: 'google',
            type: 'font-awesome',
            size: 16,
            color: '#4285F4',
          }}
          onPress={() => console.log('Google registration')}
        />
        
        <View style={styles.loginFooter}>
          <Text style={styles.loginFooterText}>Already have an account?</Text>
          <Button
            title="Sign in"
            type="clear"
            titleStyle={styles.createAccountButton}
            onPress={() => navigate('/login')}
          />
        </View>
      </View>
    </View>
  )
}

// Search Results Page Component
const SearchResultsPage = () => {
  const navigate = useNavigate()
  const [searchResults] = React.useState([
    { id: 1, name: 'Elite Beauty Salon', category: 'Beauty & Salon', rating: 4.8, reviews: 124, address: '123 Main St, New York', description: 'Premium beauty services with expert stylists' },
    { id: 2, name: 'Wellness Medical Center', category: 'Health & Wellness', rating: 4.9, reviews: 89, address: '456 Health Ave, New York', description: 'Comprehensive medical and wellness services' },
    { id: 3, name: 'AutoFix Garage', category: 'Automotive', rating: 4.7, reviews: 203, address: '789 Auto Rd, New York', description: 'Professional automotive repair and maintenance' },
    { id: 4, name: 'HomeCare Services', category: 'Home Services', rating: 4.6, reviews: 156, address: '321 Home St, New York', description: 'Reliable home maintenance and repair services' },
    { id: 5, name: 'Gourmet Bistro', category: 'Food & Dining', rating: 4.5, reviews: 287, address: '654 Food Blvd, New York', description: 'Fine dining experience with local ingredients' },
  ])

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <View style={styles.logoContainer}>
          <TouchableOpacity onPress={() => navigate('/')}>
            <Text style={styles.logo}>AlphaDirectory</Text>
          </TouchableOpacity>
          <Text style={styles.logoSubtitle}>Search Results</Text>
        </View>
        <View style={styles.headerControls}>
          <View style={styles.userActions}>
            <Button
              title="Sign in"
              type="clear"
              onPress={() => navigate('/login')}
              titleStyle={styles.signInButton}
            />
            <Button
              title="Register"
              buttonStyle={styles.registerButton}
              onPress={() => navigate('/register')}
            />
          </View>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Search Results (5 businesses)</Text>
        <Text style={styles.sectionSubtitle}>Showing businesses in New York</Text>
        
        <View style={styles.resultsContainer}>
          {searchResults.map((business) => (
            <TouchableOpacity 
              key={business.id} 
              style={styles.resultCard}
              onPress={() => navigate(`/business/${business.id}`)}
            >
              <View style={styles.resultContent}>
                <View style={styles.resultHeader}>
                  <Text style={styles.resultName}>{business.name}</Text>
                  <View style={styles.ratingContainer}>
                    <Text style={styles.rating}>★ {business.rating}</Text>
                    <Text style={styles.reviews}>({business.reviews})</Text>
                  </View>
                </View>
                <Text style={styles.resultCategory}>{business.category}</Text>
                <Text style={styles.resultAddress}>{business.address}</Text>
                <Text style={styles.resultDescription}>{business.description}</Text>
                <View style={styles.resultActions}>
                  <Button
                    title="View Details"
                    type="clear"
                    titleStyle={styles.viewDetailsButton}
                    onPress={() => navigate(`/business/${business.id}`)}
                  />
                  <Button
                    title="Book Appointment"
                    buttonStyle={styles.bookButton}
                    onPress={() => navigate(`/business/${business.id}/book`)}
                  />
                </View>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      </View>
    </ScrollView>
  )
}

// Business Detail Page Component
const BusinessDetailPage = () => {
  const navigate = useNavigate()
  
  const business = {
    id: 1,
    name: 'Elite Beauty Salon',
    category: 'Beauty & Salon',
    rating: 4.8,
    reviews: 124,
    address: '123 Main St, New York, NY 10001',
    description: 'Premium beauty services with expert stylists. We offer haircuts, coloring, styling, and spa treatments in a luxurious environment.',
    hours: 'Mon-Fri: 9am-8pm, Sat: 10am-6pm, Sun: 11am-5pm',
    phone: '(555) 123-4567',
    email: 'info@elitebeauty.com',
    services: ['Haircut & Styling', 'Hair Coloring', 'Spa Treatments', 'Manicure/Pedicure', 'Facial Treatments'],
    employees: 5,
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <View style={styles.logoContainer}>
          <TouchableOpacity onPress={() => navigate('/')}>
            <Text style={styles.logo}>AlphaDirectory</Text>
          </TouchableOpacity>
          <Text style={styles.logoSubtitle}>Business Details</Text>
        </View>
        <View style={styles.headerControls}>
          <View style={styles.userActions}>
            <Button
              title="Sign in"
              type="clear"
              onPress={() => navigate('/login')}
              titleStyle={styles.signInButton}
            />
            <Button
              title="Register"
              buttonStyle={styles.registerButton}
              onPress={() => navigate('/register')}
            />
          </View>
        </View>
      </View>

      <View style={styles.section}>
        <View style={styles.businessDetailHeader}>
          <View>
            <Text style={styles.businessDetailName}>{business.name}</Text>
            <Text style={styles.businessDetailCategory}>{business.category}</Text>
            <View style={styles.ratingContainer}>
              <Text style={styles.rating}>★ {business.rating}</Text>
              <Text style={styles.reviews}>({business.reviews} reviews)</Text>
            </View>
          </View>
          <Button
            title="Book Appointment"
            buttonStyle={styles.bookButton}
            onPress={() => navigate(`/business/${business.id}/book`)}
          />
        </View>

        <View style={styles.businessDetailInfo}>
          <View style={styles.infoSection}>
            <Text style={styles.infoTitle}>Address</Text>
            <Text style={styles.infoText}>{business.address}</Text>
          </View>
          
          <View style={styles.infoSection}>
            <Text style={styles.infoTitle}>Contact</Text>
            <Text style={styles.infoText}>Phone: {business.phone}</Text>
            <Text style={styles.infoText}>Email: {business.email}</Text>
          </View>
          
          <View style={styles.infoSection}>
            <Text style={styles.infoTitle}>Hours</Text>
            <Text style={styles.infoText}>{business.hours}</Text>
          </View>
          
          <View style={styles.infoSection}>
            <Text style={styles.infoTitle}>Description</Text>
            <Text style={styles.infoText}>{business.description}</Text>
          </View>
          
          <View style={styles.infoSection}>
            <Text style={styles.infoTitle}>Services</Text>
            <View style={styles.servicesList}>
              {business.services.map((service, index) => (
                <View key={index} style={styles.serviceTag}>
                  <Text style={styles.serviceText}>{service}</Text>
                </View>
              ))}
            </View>
          </View>
          
          <View style={styles.infoSection}>
            <Text style={styles.infoTitle}>Service Points</Text>
            <Text style={styles.infoText}>{business.employees} available chairs/employees</Text>
          </View>
        </View>
      </View>
    </ScrollView>
  )
}

const BusinessLoginPage = () => (
  <View style={styles.placeholderPage}>
    <Text>Business Login Page</Text>
  </View>
)

const BusinessRegisterPage = () => (
  <View style={styles.placeholderPage}>
    <Text>Business Register Page</Text>
  </View>
)

// Main App Component
function TestApp() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<PublicSearchPage />} />
        <Route path="/search" element={<SearchResultsPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/business/:id" element={<BusinessDetailPage />} />
        <Route path="/business/:id/book" element={<BusinessDetailPage />} />
        <Route path="/business/login" element={<BusinessLoginPage />} />
        <Route path="/business/register" element={<BusinessRegisterPage />} />
      </Routes>
    </Router>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 24,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e8eaed',
  },
  logoContainer: {
    flexDirection: 'column',
  },
  logo: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#1a73e8',
  },
  logoSubtitle: {
    fontSize: 12,
    color: '#5f6368',
    marginTop: 2,
  },
  headerControls: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 32,
  },
  userActions: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  signInButton: {
    color: '#1a73e8',
    fontSize: 14,
    fontWeight: '500',
  },
  registerButton: {
    backgroundColor: '#1a73e8',
    borderRadius: 4,
    paddingHorizontal: 24,
    paddingVertical: 8,
  },
  hero: {
    paddingHorizontal: 24,
    paddingVertical: 48,
    backgroundColor: '#f8f9fa',
    alignItems: 'center',
  },
  heroTitle: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#202124',
    textAlign: 'center',
    marginBottom: 12,
  },
  heroSubtitle: {
    fontSize: 18,
    color: '#5f6368',
    textAlign: 'center',
    marginBottom: 40,
    maxWidth: 600,
  },
  searchContainer: {
    width: '100%',
    maxWidth: 600,
    alignItems: 'center',
  },
  searchBox: {
    width: '100%',
    backgroundColor: '#ffffff',
    borderRadius: 24,
    paddingHorizontal: 24,
    paddingVertical: 8,
    borderWidth: 1,
    borderColor: '#dfe1e5',
    flexDirection: 'row',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  searchInput: {
    flex: 1,
    height: 44,
    fontSize: 16,
    paddingHorizontal: 8,
  },
  regionSelector: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  regionLabel: {
    fontSize: 14,
    color: '#5f6368',
    marginRight: 8,
  },
  regionDropdown: {
    height: 36,
    minWidth: 120,
    borderWidth: 1,
    borderColor: '#dadce0',
    borderRadius: 4,
    justifyContent: 'center',
    paddingHorizontal: 12,
    backgroundColor: '#ffffff',
  },
  regionText: {
    fontSize: 14,
    color: '#202124',
    fontWeight: '500',
  },
  regionDropdownMenu: {
    position: 'absolute',
    top: 40,
    left: 0,
    width: 160,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#dadce0',
    borderRadius: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    zIndex: 1000,
  },
  regionDropdownItem: {
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#f1f3f4',
  },
  regionDropdownItemSelected: {
    backgroundColor: '#f1f8ff',
  },
  regionDropdownItemText: {
    fontSize: 14,
    color: '#202124',
  },
  regionDropdownItemTextSelected: {
    color: '#1a73e8',
    fontWeight: '500',
  },
  searchButton: {
    backgroundColor: '#1a73e8',
    borderRadius: 18,
    paddingHorizontal: 24,
    paddingVertical: 10,
  },
  section: {
    paddingHorizontal: 24,
    paddingVertical: 48,
  },
  sectionTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#202124',
    marginBottom: 8,
  },
  sectionSubtitle: {
    fontSize: 16,
    color: '#5f6368',
    marginBottom: 32,
  },
  categoryContainer: {
    alignItems: 'center',
  },
  categoryGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: 16,
    maxWidth: 900,
  },
  categoryCard: {
    width: 100,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 16,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#e8eaed',
    marginBottom: 16,
  },
  categoryIconContainer: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#f8f9fa',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  categoryIcon: {
    fontSize: 24,
  },
  categoryName: {
    fontSize: 13,
    color: '#202124',
    textAlign: 'center',
    fontWeight: '500',
    lineHeight: 18,
  },
  businessContainer: {
    alignItems: 'center',
  },
  businessScroll: {
    paddingHorizontal: 24,
  },
  businessCard: {
    width: 220,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#e8eaed',
    marginRight: 20,
    paddingBottom: 16,
    paddingHorizontal: 16,
    paddingTop: 16,
  },
  businessImageContainer: {
    height: 120,
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  businessImagePlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  businessImageText: {
    fontSize: 12,
    color: '#5f6368',
  },
  businessName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#202124',
    marginBottom: 4,
    textAlign: 'left',
  },
  businessCategory: {
    fontSize: 13,
    color: '#5f6368',
    marginBottom: 8,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  rating: {
    fontSize: 13,
    color: '#202124',
    fontWeight: '500',
    marginRight: 4,
  },
  reviews: {
    fontSize: 13,
    color: '#5f6368',
  },
  viewDetailsButton: {
    color: '#1a73e8',
    fontSize: 14,
    fontWeight: '500',
  },
  resultsContainer: {
    maxWidth: 800,
    width: '100%',
    marginHorizontal: 'auto',
  },
  resultCard: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e8eaed',
    marginBottom: 16,
    padding: 20,
  },
  resultContent: {
    flex: 1,
  },
  resultHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 8,
  },
  resultName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#202124',
    flex: 1,
    marginRight: 16,
  },
  resultCategory: {
    fontSize: 14,
    color: '#5f6368',
    marginBottom: 4,
  },
  resultAddress: {
    fontSize: 14,
    color: '#5f6368',
    marginBottom: 8,
  },
  resultDescription: {
    fontSize: 14,
    color: '#202124',
    lineHeight: 20,
    marginBottom: 16,
  },
  resultActions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  bookButton: {
    backgroundColor: '#1a73e8',
    borderRadius: 4,
    paddingHorizontal: 20,
    paddingVertical: 8,
  },
  businessDetailHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 32,
    paddingBottom: 24,
    borderBottomWidth: 1,
    borderBottomColor: '#e8eaed',
  },
  businessDetailName: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#202124',
    marginBottom: 4,
  },
  businessDetailCategory: {
    fontSize: 16,
    color: '#5f6368',
    marginBottom: 12,
  },
  businessDetailInfo: {
    maxWidth: 800,
    width: '100%',
    marginHorizontal: 'auto',
  },
  infoSection: {
    marginBottom: 24,
  },
  infoTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#202124',
    marginBottom: 8,
  },
  infoText: {
    fontSize: 16,
    color: '#5f6368',
    lineHeight: 24,
  },
  servicesList: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  serviceTag: {
    backgroundColor: '#f1f8ff',
    borderRadius: 16,
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderWidth: 1,
    borderColor: '#d2e3fc',
  },
  serviceText: {
    fontSize: 14,
    color: '#1a73e8',
  },
  businessSection: {
    backgroundColor: '#f8f9fa',
    paddingHorizontal: 24,
    paddingVertical: 48,
    borderTopWidth: 1,
    borderTopColor: '#e8eaed',
  },
  businessContent: {
    maxWidth: 600,
    marginHorizontal: 'auto',
    alignItems: 'center',
  },
  businessTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#202124',
    marginBottom: 16,
    textAlign: 'center',
  },
  businessDescription: {
    fontSize: 16,
    color: '#5f6368',
    textAlign: 'center',
    marginBottom: 32,
    maxWidth: 600,
    lineHeight: 24,
  },
  businessButtons: {
    flexDirection: 'row',
    gap: 16,
  },
  businessLoginButton: {
    borderColor: '#1a73e8',
    borderWidth: 1,
    borderRadius: 4,
    paddingHorizontal: 32,
    paddingVertical: 12,
  },
  businessLoginText: {
    color: '#1a73e8',
    fontSize: 14,
    fontWeight: '500',
  },
  registerBusinessButton: {
    backgroundColor: '#1a73e8',
    borderRadius: 4,
    paddingHorizontal: 32,
    paddingVertical: 12,
  },
  footer: {
    backgroundColor: '#f8f9fa',
    paddingHorizontal: 24,
    paddingVertical: 32,
    borderTopWidth: 1,
    borderTopColor: '#e8eaed',
    alignItems: 'center',
  },
  footerText: {
    fontSize: 14,
    color: '#5f6368',
    marginBottom: 16,
  },
  footerLinks: {
    flexDirection: 'row',
    gap: 24,
  },
  footerLink: {
    fontSize: 14,
    color: '#5f6368',
  },
  loginContainer: {
    flex: 1,
    backgroundColor: '#ffffff',
    padding: 24,
  },
  loginHeader: {
    marginBottom: 32,
  },
  backButton: {
    color: '#1a73e8',
    fontSize: 14,
  },
  loginCard: {
    maxWidth: 400,
    width: '100%',
    marginHorizontal: 'auto',
    backgroundColor: '#ffffff',
    borderRadius: 8,
    padding: 40,
    borderWidth: 1,
    borderColor: '#e8eaed',
  },
  loginTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#202124',
    marginBottom: 8,
    textAlign: 'center',
  },
  loginSubtitle: {
    fontSize: 14,
    color: '#5f6368',
    textAlign: 'center',
    marginBottom: 32,
    lineHeight: 20,
  },
  inputContainer: {
    marginBottom: 24,
  },
  inputLabel: {
    fontSize: 14,
    color: '#5f6368',
    marginBottom: 8,
    fontWeight: '500',
  },
  input: {
    height: 48,
    borderWidth: 1,
    borderColor: '#dadce0',
    borderRadius: 4,
    paddingHorizontal: 16,
    fontSize: 16,
  },
  loginButton: {
    backgroundColor: '#1a73e8',
    borderRadius: 4,
    paddingVertical: 12,
    marginBottom: 24,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 24,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: '#e8eaed',
  },
  dividerText: {
    fontSize: 14,
    color: '#5f6368',
    marginHorizontal: 16,
  },
  googleButton: {
    borderColor: '#dadce0',
    borderWidth: 1,
    borderRadius: 4,
    paddingVertical: 12,
  },
  googleButtonText: {
    color: '#3c4043',
    fontSize: 14,
    fontWeight: '500',
  },
  loginFooter: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 32,
    gap: 8,
  },
  loginFooterText: {
    fontSize: 14,
    color: '#5f6368',
  },
  createAccountButton: {
    color: '#1a73e8',
    fontSize: 14,
    fontWeight: '500',
  },
  placeholderText: {
    fontSize: 16,
    color: '#5f6368',
    textAlign: 'center',
    marginVertical: 32,
  },
  placeholderPage: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
})

export default TestApp