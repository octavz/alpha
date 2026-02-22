import { db } from '../db/client'
import { businesses, services, businessHours, users, categories, regions } from '../db/schema'
import { eq, sql } from 'drizzle-orm'
import { faker } from '@faker-js/faker'

// Business names by category
const businessNamesByCategory: Record<string, string[]> = {
  'beauty-salon': [
    'Luxe Hair Studio', 'Glamour Nails Spa', 'Royal Beauty Bar', 'Elegant Cuts Salon',
    'Blissful Spa Retreat', 'Chic Beauty Lounge', 'Posh Hair Design', 'Serenity Skin Care',
    'Urban Barber Co', 'Vintage Beauty Parlor', 'Modern Man Salon', 'Pure Essence Spa'
  ],
  'health-wellness': [
    'Wellness Medical Center', 'FitLife Gym', 'Holistic Health Clinic', 'Peak Performance PT',
    'Tranquil Therapy Center', 'Vitality Chiropractic', 'Mindful Meditation Studio',
    'Renew Wellness Spa', 'Active Life Fitness', 'Balance Yoga Studio', 'Healing Hands Massage'
  ],
  'home-services': [
    'ProFix Home Repair', 'Sparkle Clean Services', 'Master Plumbers Inc', 'Electric Solutions',
    'Green Thumb Landscaping', 'SafeGuard Security', 'Comfort HVAC Services', 'Dream Home Builders',
    'Quick Fix Handyman', 'Premium Painters', 'Total Home Care', 'Expert Roofing'
  ],
  'automotive': [
    'AutoCare Garage', 'Precision Tune-Up', 'Speedy Lube Express', 'Master Mechanic Shop',
    'Elite Detailing Studio', 'Tire Masters', 'Brake & Alignment Pro', 'Car Clinic',
    'Performance Auto Works', 'Family Auto Repair', 'Trusted Transmission', 'Mobile Mechanic'
  ],
  'professional-services': [
    'LegalEdge Law Firm', 'SmartTax Accounting', 'Business Growth Consultants',
    'Creative Marketing Agency', 'Tech Solutions Inc', 'Financial Planning Partners',
    'HR Excellence Group', 'Strategic Advisors LLC', 'Innovation Consulting',
    'Project Management Pros', 'Digital Transformation Co'
  ],
  'food-dining': [
    'Taste of Italy', 'Urban Bistro Cafe', 'Spice Route Kitchen', 'Fresh Farm Table',
    'Cozy Corner Bakery', 'Ocean Blue Seafood', 'Grill Masters BBQ', 'Green Leaf Vegan',
    'Sweet Treats Desserts', 'Brew & Bites Coffee', 'Fusion Flavors', 'Rustic Pizza Co'
  ],
  'retail': [
    'Trendy Boutique', 'Home Essentials Store', 'Tech Gadgets Hub', 'Fashion Forward',
    'Book Nook', 'Sports Gear Outlet', 'Artisan Crafts Market', 'Pet Paradise',
    'Garden Oasis Nursery', 'Toy Wonderland', 'Jewelry Gallery', 'Outdoor Adventure Shop'
  ],
  'education': [
    'Bright Minds Academy', 'SkillUp Training Center', 'Language Learning Lab',
    'Music Masters Studio', 'Art Expression School', 'STEM Discovery Center',
    'Early Learning Preschool', 'Test Prep Experts', 'Career Development Institute',
    'Creative Writing Workshop', 'Digital Skills Academy'
  ],
  'entertainment': [
    'Fun Zone Arcade', 'Cinema Paradise', 'Event Masters Planning', 'Game Night Lounge',
    'Live Music Venue', 'Escape Room Adventures', 'Bowling Alley Fun', 'Karaoke King',
    'Party Palace', 'Virtual Reality Arena', 'Comedy Club Central'
  ],
  'technology': [
    'Tech Support Solutions', 'Web Design Studio', 'App Development Pros',
    'IT Consulting Group', 'Cyber Security Experts', 'Cloud Services Inc',
    'Data Analytics Firm', 'Software Innovators', 'Network Solutions',
    'Digital Marketing Tech', 'AI Solutions Lab'
  ]
}

// Service names by category
const serviceNamesByCategory: Record<string, string[]> = {
  'beauty-salon': [
    'Haircut & Styling', 'Hair Coloring', 'Manicure', 'Pedicure', 'Facial Treatment',
    'Massage Therapy', 'Waxing', 'Makeup Application', 'Eyebrow Shaping', 'Hair Treatment'
  ],
  'health-wellness': [
    'Doctor Consultation', 'Physical Therapy', 'Massage Session', 'Yoga Class',
    'Personal Training', 'Chiropractic Adjustment', 'Nutrition Counseling', 'Meditation Session'
  ],
  'home-services': [
    'Plumbing Repair', 'Electrical Work', 'House Cleaning', 'Painting Service',
    'Landscaping', 'Handyman Services', 'HVAC Maintenance', 'Roof Repair'
  ],
  'automotive': [
    'Oil Change', 'Brake Service', 'Tire Rotation', 'Car Wash & Detailing',
    'Engine Diagnostic', 'Transmission Service', 'AC Repair', 'Battery Replacement'
  ],
  'professional-services': [
    'Legal Consultation', 'Tax Preparation', 'Business Strategy Session',
    'Marketing Plan Development', 'IT Support', 'Financial Planning'
  ],
  'food-dining': [
    'Table Reservation', 'Catering Service', 'Cooking Class', 'Wine Tasting',
    'Private Dining', 'Takeout Order', 'Delivery Service'
  ],
  'retail': [
    'Personal Shopping', 'Gift Wrapping', 'Product Consultation', 'Delivery Service',
    'Installation Service', 'Repair Service', 'Custom Order'
  ],
  'education': [
    'Private Tutoring', 'Group Class', 'Workshop', 'Test Preparation',
    'Skill Assessment', 'Career Counseling', 'Online Course'
  ],
  'entertainment': [
    'Event Booking', 'Ticket Purchase', 'Private Party', 'Group Activity',
    'Equipment Rental', 'Venue Reservation', 'Entertainment Package'
  ],
  'technology': [
    'IT Support', 'Website Development', 'App Development', 'Software Consultation',
    'Data Analysis', 'Network Setup', 'Security Audit'
  ]
}

async function seedBusinesses() {
  console.log('Starting business seed...')

  // Get all categories and regions
  const allCategories = await db.select().from(categories)
  const allRegions = await db.select().from(regions)

  if (allCategories.length === 0 || allRegions.length === 0) {
    console.error('No categories or regions found. Please run migrations first.')
    process.exit(1)
  }

  console.log(`Found ${allCategories.length} categories and ${allRegions.length} regions`)

  // Get or create test users
  const testUsers = []
  for (let i = 1; i <= 50; i++) {
    const email = `business${i}@test.com`
    const existingUser = await db.query.users.findFirst({
      where: eq(users.email, email)
    })

    if (!existingUser) {
      const [user] = await db.insert(users).values({
        email,
        name: faker.person.fullName(),
        phone: faker.phone.number(),
        role: 'business_admin',
        emailVerified: true,
        regionId: faker.helpers.arrayElement(allRegions).id
      }).returning()
      testUsers.push(user)
    } else {
      testUsers.push(existingUser)
    }
  }

  console.log(`Created/retrieved ${testUsers.length} test users`)

  // Create businesses
  const createdBusinesses = []
  let businessCount = 0

  for (const category of allCategories) {
    const categoryBusinessNames = businessNamesByCategory[category.slug] || []
    const categoryServiceNames = serviceNamesByCategory[category.slug] || []
    
    // Create 10-20 businesses per category
    const businessesToCreate = faker.number.int({ min: 10, max: 20 })
    
    for (let i = 0; i < businessesToCreate; i++) {
      businessCount++
      const user = testUsers[businessCount % testUsers.length]
      const region = faker.helpers.arrayElement(allRegions)
      
      const businessName = categoryBusinessNames[i % categoryBusinessNames.length] || 
        `${category.name} Business ${i + 1}`
      
      const slug = `${businessName.toLowerCase().replace(/[^a-z0-9]+/g, '-')}-${businessCount}`
      
      if (!user) continue // Skip if no user
      
      // Use raw SQL to handle conflicts
      await db.execute(sql`
        INSERT INTO businesses (
          id, user_id, name, slug, description, category_id, region_id,
          email, phone, website, address_line1, city, state, zip_code, country,
          latitude, longitude, logo_url, cover_image_url, is_verified, is_active,
          is_featured, verification_status, service_points_count, created_at, updated_at
        ) VALUES (
          gen_random_uuid(), ${user.id}, ${businessName}, ${slug}, 
          ${faker.lorem.paragraph({ min: 3, max: 5 }).substring(0, 200)},
          ${category.id}, ${region.id}, ${faker.internet.email()}, ${faker.phone.number()},
          ${faker.internet.url()}, ${faker.location.streetAddress()}, ${faker.location.city()},
          ${faker.location.state()}, ${faker.location.zipCode()}, ${faker.location.country()},
          ${faker.location.latitude().toString()}, ${faker.location.longitude().toString()},
          ${`https://picsum.photos/seed/${slug}/200/200`}, ${`https://picsum.photos/seed/${slug}-cover/800/400`},
          ${faker.datatype.boolean(0.8)}, true, ${faker.datatype.boolean(0.2)},
          ${faker.helpers.arrayElement(['pending', 'approved', 'rejected'])},
          ${faker.number.int({ min: 1, max: 5 })}, NOW(), NOW()
        )
        ON CONFLICT (slug) DO NOTHING
        RETURNING id
      `)
      
      // Get the business ID
      const business = await db.query.businesses.findFirst({
        where: eq(businesses.slug, slug)
      })

      if (business) {
        createdBusinesses.push(business)
      }

      // Create services for this business
      const servicesToCreate = faker.number.int({ min: 3, max: 8 })
      for (let j = 0; j < servicesToCreate; j++) {
        const serviceName = categoryServiceNames[j % categoryServiceNames.length] || 
          `${category.name} Service ${j + 1}`
        
        await db.insert(services).values({
          businessId: business!.id,
          name: serviceName,
          description: faker.lorem.sentence(),
          durationMinutes: faker.helpers.arrayElement([30, 45, 60, 90, 120]),
          price: faker.number.float({ min: 20, max: 200, fractionDigits: 2 }).toString(),
          isActive: true,
          sortOrder: j
        })
      }

      // Create business hours
      for (let day = 0; day < 7; day++) {
        const isClosed = day === 0 ? faker.datatype.boolean(0.3) : false // 30% chance closed on Sunday
        
        await db.insert(businessHours).values({
          businessId: business!.id,
          dayOfWeek: day,
          openTime: '09:00',
          closeTime: '17:00',
          isClosed
        })
      }

      if (businessCount % 10 === 0) {
        console.log(`Created ${businessCount} businesses...`)
      }
    }
  }

  console.log(`✅ Successfully created ${businessCount} businesses with services and hours`)
  console.log(`📊 Statistics:`)
  console.log(`   - Categories: ${allCategories.length}`)
  console.log(`   - Regions: ${allRegions.length}`)
  console.log(`   - Test Users: ${testUsers.length}`)
  console.log(`   - Total Businesses: ${businessCount}`)
  console.log(`   - Estimated Services: ${businessCount * 5} (average 5 per business)`)
  
  return createdBusinesses
}

// Run seed
seedBusinesses()
  .then(() => {
    console.log('🎉 Seed completed successfully!')
    process.exit(0)
  })
  .catch((error) => {
    console.error('❌ Seed failed:', error)
    process.exit(1)
  })