package com.alpha.scripts

import com.alpha.domain.entity.*
import com.alpha.domain.enums.UserRole
import com.alpha.domain.enums.VerificationStatus
import com.alpha.domain.repository.*
import com.github.javafaker.Faker
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random

@Configuration
@Profile("dev", "test")
class BusinessSeedScript(
    private val userRepository: UserRepository,
    private val businessRepository: BusinessRepository,
    private val categoryRepository: CategoryRepository,
    private val regionRepository: RegionRepository,
    private val serviceRepository: ServiceRepository,
    private val businessHoursRepository: BusinessHoursRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // Business names by category
    private val businessNamesByCategory = mapOf(
        "beauty-salon" to listOf(
            "Luxe Hair Studio", "Glamour Nails Spa", "Royal Beauty Bar", "Elegant Cuts Salon",
            "Blissful Spa Retreat", "Chic Beauty Lounge", "Posh Hair Design", "Serenity Skin Care",
            "Urban Barber Co", "Vintage Beauty Parlor", "Modern Man Salon", "Pure Essence Spa"
        ),
        "health-wellness" to listOf(
            "Wellness Medical Center", "FitLife Gym", "Holistic Health Clinic", "Peak Performance PT",
            "Tranquil Therapy Center", "Vitality Chiropractic", "Mindful Meditation Studio",
            "Renew Wellness Spa", "Active Life Fitness", "Balance Yoga Studio", "Healing Hands Massage"
        ),
        "home-services" to listOf(
            "ProFix Home Repair", "Sparkle Clean Services", "Master Plumbers Inc", "Electric Solutions",
            "Green Thumb Landscaping", "SafeGuard Security", "Comfort HVAC Services", "Dream Home Builders",
            "Quick Fix Handyman", "Premium Painters", "Total Home Care", "Expert Roofing"
        ),
        "automotive" to listOf(
            "AutoCare Garage", "Precision Tune-Up", "Speedy Lube Express", "Master Mechanic Shop",
            "Elite Detailing Studio", "Tire Masters", "Brake & Alignment Pro", "Car Clinic",
            "Performance Auto Works", "Family Auto Repair", "Trusted Transmission", "Mobile Mechanic"
        ),
        "professional-services" to listOf(
            "LegalEdge Law Firm", "SmartTax Accounting", "Business Growth Consultants",
            "Creative Marketing Agency", "Tech Solutions Inc", "Financial Planning Partners",
            "HR Excellence Group", "Strategic Advisors LLC", "Innovation Consulting",
            "Project Management Pros", "Digital Transformation Co"
        ),
        "food-dining" to listOf(
            "Taste of Italy", "Urban Bistro Cafe", "Spice Route Kitchen", "Fresh Farm Table",
            "Cozy Corner Bakery", "Ocean Blue Seafood", "Grill Masters BBQ", "Green Leaf Vegan",
            "Sweet Treats Desserts", "Brew & Bites Coffee", "Fusion Flavors", "Rustic Pizza Co"
        ),
        "retail" to listOf(
            "Trendy Boutique", "Home Essentials Store", "Tech Gadgets Hub", "Fashion Forward",
            "Book Nook", "Sports Gear Outlet", "Artisan Crafts Market", "Pet Paradise",
            "Garden Oasis Nursery", "Toy Wonderland", "Jewelry Gallery", "Outdoor Adventure Shop"
        ),
        "education" to listOf(
            "Bright Minds Academy", "SkillUp Training Center", "Language Learning Lab",
            "Music Masters Studio", "Art Expression School", "STEM Discovery Center",
            "Early Learning Preschool", "Test Prep Experts", "Career Development Institute",
            "Creative Writing Workshop", "Digital Skills Academy"
        ),
        "entertainment" to listOf(
            "Fun Zone Arcade", "Cinema Paradise", "Event Masters Planning", "Game Night Lounge",
            "Live Music Venue", "Escape Room Adventures", "Bowling Alley Fun", "Karaoke King",
            "Party Palace", "Virtual Reality Arena", "Comedy Club Central"
        ),
        "technology" to listOf(
            "Tech Support Solutions", "Web Design Studio", "App Development Pros",
            "IT Consulting Group", "Cyber Security Experts", "Cloud Services Inc",
            "Data Analytics Firm", "Software Innovators", "Network Solutions",
            "Digital Marketing Tech", "AI Solutions Lab"
        )
    )

    // Service names by category
    private val serviceNamesByCategory = mapOf(
        "beauty-salon" to listOf(
            "Haircut & Styling", "Hair Coloring", "Manicure", "Pedicure", "Facial Treatment",
            "Massage Therapy", "Waxing", "Makeup Application", "Eyebrow Shaping", "Hair Treatment"
        ),
        "health-wellness" to listOf(
            "Doctor Consultation", "Physical Therapy", "Massage Session", "Yoga Class",
            "Personal Training", "Chiropractic Adjustment", "Nutrition Counseling", "Meditation Session"
        ),
        "home-services" to listOf(
            "Plumbing Repair", "Electrical Work", "House Cleaning", "Painting Service",
            "Landscaping", "Handyman Services", "HVAC Maintenance", "Roof Repair"
        ),
        "automotive" to listOf(
            "Oil Change", "Brake Service", "Tire Rotation", "Car Wash & Detailing",
            "Engine Diagnostic", "Transmission Service", "AC Repair", "Battery Replacement"
        ),
        "professional-services" to listOf(
            "Legal Consultation", "Tax Preparation", "Business Strategy Session",
            "Marketing Plan Development", "IT Support", "Financial Planning"
        ),
        "food-dining" to listOf(
            "Table Reservation", "Catering Service", "Cooking Class", "Wine Tasting",
            "Private Dining", "Takeout Order", "Delivery Service"
        ),
        "retail" to listOf(
            "Personal Shopping", "Gift Wrapping", "Product Consultation", "Delivery Service",
            "Installation Service", "Repair Service", "Custom Order"
        ),
        "education" to listOf(
            "Private Tutoring", "Group Class", "Workshop", "Test Preparation",
            "Skill Assessment", "Career Counseling", "Online Course"
        ),
        "entertainment" to listOf(
            "Event Booking", "Ticket Purchase", "Private Party", "Group Activity",
            "Equipment Rental", "Venue Reservation", "Entertainment Package"
        ),
        "technology" to listOf(
            "IT Support", "Website Development", "App Development", "Software Consultation",
            "Data Analysis", "Network Setup", "Security Audit"
        )
    )

    @Bean
    @Transactional
    fun seedBusinesses(): CommandLineRunner {
        return CommandLineRunner { args ->
            println("Starting business seed...")

            // Check if we already have businesses
            val existingBusinessCount = businessRepository.count()
            if (existingBusinessCount > 100) {
                println("Database already has $existingBusinessCount businesses. Skipping seed.")
                return@CommandLineRunner
            }

            // Get all categories and regions
            val allCategories = categoryRepository.findAll()
            val allRegions = regionRepository.findAll()

            if (allCategories.isEmpty() || allRegions.isEmpty()) {
                println("ERROR: No categories or regions found. Please run migrations first.")
                return@CommandLineRunner
            }

            println("Found ${allCategories.size} categories and ${allRegions.size} regions")

            // Clean up any existing test users to avoid enum case issues
            println("Cleaning up existing test users...")
            for (i in 1..50) {
                val email = "business$i@test.com"
                try {
                    val existingUser = userRepository.findByEmail(email)
                    existingUser?.let { userRepository.delete(it) }
                } catch (e: Exception) {
                    // Ignore errors - user might not exist or have enum case issues
                }
            }

            // Create test users
            val testUsers = mutableListOf<UserEntity>()
            val faker = Faker()

            for (i in 1..50) {
                val email = "business$i@test.com"
                val passwordHash = passwordEncoder.encode("password123")
                val newUser = UserEntity().apply {
                    this.email = email
                    this.passwordHash = passwordHash
                    this.name = faker.name().fullName()
                    this.phone = faker.phoneNumber().phoneNumber()
                    this.role = UserRole.BUSINESS_ADMIN
                    this.emailVerified = true
                    this.region = allRegions.random()
                }
                val savedUser = userRepository.save(newUser)
                testUsers.add(savedUser)
            }

            println("Created/retrieved ${testUsers.size} test users")

            // Create businesses
            val createdBusinesses = mutableListOf<BusinessEntity>()
            var businessCount = 0

            for (category in allCategories) {
                val categoryBusinessNames = businessNamesByCategory[category.slug] ?: emptyList()
                val categoryServiceNames = serviceNamesByCategory[category.slug] ?: emptyList()

                // Create 10-20 businesses per category
                val businessesToCreate = Random.nextInt(10, 21)

                for (i in 0 until businessesToCreate) {
                    businessCount++
                    val user = testUsers[businessCount % testUsers.size]
                    val region = allRegions.random()

                    val businessName = if (categoryBusinessNames.isNotEmpty()) {
                        categoryBusinessNames[i % categoryBusinessNames.size]
                    } else {
                        "${category.name} Business ${i + 1}"
                    }

                    val slug = "${businessName.lowercase().replace(Regex("[^a-z0-9]+"), "-")}-$businessCount"

                    // Create business
                    val business = BusinessEntity().apply {
                        this.user = user
                        this.name = businessName
                        this.slug = slug
                        this.description = faker.lorem().paragraph().take(200)
                        this.category = category
                        this.region = region
                        this.email = faker.internet().emailAddress()
                        this.phone = faker.phoneNumber().phoneNumber()
                        this.website = faker.internet().url()
                        this.addressLine1 = faker.address().streetAddress()
                        this.city = faker.address().city()
                        this.state = faker.address().state()
                        this.zipCode = faker.address().zipCode()
                        this.country = faker.address().country()
                        this.latitude = BigDecimal.valueOf(faker.address().latitude().toDouble())
                        this.longitude = BigDecimal.valueOf(faker.address().longitude().toDouble())
                        this.logoUrl = "https://picsum.photos/seed/$slug/200/200"
                        this.coverImageUrl = "https://picsum.photos/seed/$slug-cover/800/400"
                        this.isVerified = Random.nextDouble() < 0.8 // 80% verified
                        this.isActive = true
                        this.isFeatured = Random.nextDouble() < 0.2 // 20% featured
                        this.verificationStatus = listOf(
                            VerificationStatus.PENDING,
                            VerificationStatus.APPROVED,
                            VerificationStatus.REJECTED
                        ).random()
                        this.servicePointsCount = Random.nextInt(1, 6)
                    }

                    val savedBusiness = businessRepository.save(business)
                    createdBusinesses.add(savedBusiness)

                    // Create services for this business
                    val servicesToCreate = Random.nextInt(3, 9)
                    for (j in 0 until servicesToCreate) {
                        val serviceName = if (categoryServiceNames.isNotEmpty()) {
                            categoryServiceNames[j % categoryServiceNames.size]
                        } else {
                            "${category.name} Service ${j + 1}"
                        }

                        val service = ServiceEntity().apply {
                            this.business = savedBusiness
                            this.name = serviceName
                            this.description = faker.lorem().sentence()
                            this.durationMinutes = listOf(30, 45, 60, 90, 120).random()
                            this.price = BigDecimal.valueOf(Random.nextDouble(20.0, 200.0))
                            this.isActive = true
                            this.sortOrder = j
                        }

                        serviceRepository.save(service)
                    }

                    // Create business hours
                    for (day in 0..6) {
                        val isClosed = if (day == 0) Random.nextDouble() < 0.3 else false // 30% chance closed on Sunday

                        val businessHour = BusinessHoursEntity(
                            business = savedBusiness,
                            dayOfWeek = day,
                            openTime = if (!isClosed) LocalTime.of(9, 0) else null,
                            closeTime = if (!isClosed) LocalTime.of(17, 0) else null,
                            isClosed = isClosed
                        )

                        businessHoursRepository.save(businessHour)
                    }

                    if (businessCount % 10 == 0) {
                        println("Created $businessCount businesses...")
                    }
                }
            }

            println("✅ Successfully created $businessCount businesses with services and hours")
            println("📊 Statistics:")
            println("   - Categories: ${allCategories.size}")
            println("   - Regions: ${allRegions.size}")
            println("   - Test Users: ${testUsers.size}")
            println("   - Total Businesses: $businessCount")
            println("   - Estimated Services: ${businessCount * 5} (average 5 per business)")
            println("🎉 Seed completed successfully!")
        }
    }
}