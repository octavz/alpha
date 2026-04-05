package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.BusinessEntity
import com.alpha.domain.entity.CategoryEntity
import com.alpha.domain.entity.RegionEntity
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import com.alpha.domain.enums.VerificationStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Transactional
class BusinessRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var businessRepository: BusinessRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var regionRepository: RegionRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var testUser: UserEntity
    private lateinit var testRegion: RegionEntity
    private lateinit var testCategory: CategoryEntity

    @BeforeEach
    fun setUp() {
        testUser = userRepository.save(UserEntity().apply {
            email = "owner-${UUID.randomUUID()}@example.com"
            name = "Business Owner"
            role = UserRole.BUSINESS_ADMIN
            emailVerified = true
        })

        testRegion = regionRepository.save(RegionEntity().apply {
            name = "Test Region"
            code = "TR-${UUID.randomUUID()}"
            country = "Test Country"
            timezone = "UTC"
            isActive = true
        })

        testCategory = categoryRepository.save(CategoryEntity().apply {
            name = "Test Category"
            slug = "test-category-${UUID.randomUUID()}"
            isActive = true
        })
    }

    private fun createBusiness(
        name: String = "Test Business ${UUID.randomUUID()}",
        slug: String = "test-business-${UUID.randomUUID()}",
        isActive: Boolean = true,
        isVerified: Boolean = false,
        isFeatured: Boolean = false,
        verificationStatus: VerificationStatus = VerificationStatus.PENDING,
        owner: UserEntity = testUser,
        region: RegionEntity = testRegion,
        category: CategoryEntity = testCategory,
        latitude: BigDecimal? = null,
        longitude: BigDecimal? = null,
        description: String? = "Test Description"
    ): BusinessEntity {
        return businessRepository.save(BusinessEntity().apply {
            this.name = name
            this.slug = slug
            this.description = description
            this.user = owner
            this.region = region
            this.category = category
            this.isActive = isActive
            this.isVerified = isVerified
            this.isFeatured = isFeatured
            this.verificationStatus = verificationStatus
            this.latitude = latitude
            this.longitude = longitude
        })
    }

    @Test
    fun `findBySlug should return business when exists`() {
        val business = createBusiness(slug = "unique-slug-${UUID.randomUUID()}")

        val found = businessRepository.findBySlug(business.slug)

        assertNotNull(found)
        assertEquals(business.slug, found?.slug)
    }

    @Test
    fun `findBySlug should return null when not exists`() {
        val found = businessRepository.findBySlug("nonexistent-slug")
        assertNull(found)
    }

    @Test
    fun `findByUserId should return businesses owned by user`() {
        createBusiness(name = "Business 1", slug = "biz-1-${UUID.randomUUID()}")
        createBusiness(name = "Business 2", slug = "biz-2-${UUID.randomUUID()}")

        val result = businessRepository.findByUserId(testUser.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByRegionId should return businesses in region`() {
        val business = createBusiness(slug = "region-biz-${UUID.randomUUID()}")

        val result = businessRepository.findByRegionId(testRegion.id!!)

        assertEquals(1, result.size)
        assertEquals(business.name, result[0].name)
    }

    @Test
    fun `findByCategoryId should return businesses in category`() {
        val business = createBusiness(slug = "cat-biz-${UUID.randomUUID()}")

        val result = businessRepository.findByCategoryId(testCategory.id!!)

        assertEquals(1, result.size)
    }

    @Test
    fun `findByIsActiveTrue should return only active businesses`() {
        val active = createBusiness(slug = "active-${UUID.randomUUID()}", isActive = true)
        createBusiness(slug = "inactive-${UUID.randomUUID()}", isActive = false)

        val result = businessRepository.findByIsActiveTrue()

        assertTrue(result.any { it.id == active.id })
        assertFalse(result.any { !it.isActive })
    }

    @Test
    fun `findByIsVerifiedTrue should return only verified businesses`() {
        val verified = createBusiness(slug = "verified-${UUID.randomUUID()}", isVerified = true)
        createBusiness(slug = "unverified-${UUID.randomUUID()}", isVerified = false)

        val result = businessRepository.findByIsVerifiedTrue()

        assertTrue(result.any { it.id == verified.id })
        assertFalse(result.any { !it.isVerified })
    }

    @Test
    fun `findByIsFeaturedTrue should return only featured businesses`() {
        val featured = createBusiness(slug = "featured-${UUID.randomUUID()}", isFeatured = true)
        createBusiness(slug = "notfeatured-${UUID.randomUUID()}", isFeatured = false)

        val result = businessRepository.findByIsFeaturedTrue()

        assertTrue(result.any { it.id == featured.id })
        assertFalse(result.any { !it.isFeatured })
    }

    @Test
    fun `findByVerificationStatus should return businesses with specific status`() {
        val pending = createBusiness(slug = "pending-${UUID.randomUUID()}", verificationStatus = VerificationStatus.PENDING)
        createBusiness(slug = "approved-${UUID.randomUUID()}", verificationStatus = VerificationStatus.APPROVED)

        val result = businessRepository.findByVerificationStatus(VerificationStatus.PENDING)

        assertTrue(result.any { it.id == pending.id })
        assertFalse(result.any { it.verificationStatus != VerificationStatus.PENDING })
    }

    @Test
    fun `existsBySlug should return true when exists`() {
        val business = createBusiness(slug = "exists-slug-${UUID.randomUUID()}")

        assertTrue(businessRepository.existsBySlug(business.slug))
    }

    @Test
    fun `existsBySlug should return false when not exists`() {
        assertFalse(businessRepository.existsBySlug("nonexistent-slug"))
    }

    @Test
    fun `findActiveVerifiedBusinesses should return paginated active and verified businesses`() {
        val activeVerified = createBusiness(slug = "av-1-${UUID.randomUUID()}", isActive = true, isVerified = true)
        createBusiness(slug = "inactive-1-${UUID.randomUUID()}", isActive = false, isVerified = true)
        createBusiness(slug = "unverified-1-${UUID.randomUUID()}", isActive = true, isVerified = false)

        val result = businessRepository.findActiveVerifiedBusinesses(PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals(activeVerified.id, result.content[0].id)
    }

    @Test
    fun `findActiveVerifiedByRegion should return businesses in region that are active and verified`() {
        val business = createBusiness(slug = "avr-1-${UUID.randomUUID()}", isActive = true, isVerified = true)

        val result = businessRepository.findActiveVerifiedByRegion(testRegion.id!!, PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals(business.id, result.content[0].id)
    }

    @Test
    fun `findActiveVerifiedByCategory should return businesses in category that are active and verified`() {
        val business = createBusiness(slug = "avc-1-${UUID.randomUUID()}", isActive = true, isVerified = true)

        val result = businessRepository.findActiveVerifiedByCategory(testCategory.id!!, PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals(business.id, result.content[0].id)
    }

    @Test
    fun `searchActiveVerified should find businesses by name`() {
        val business = createBusiness(
            name = "Restaurant Italiano",
            slug = "search-name-${UUID.randomUUID()}",
            isActive = true,
            isVerified = true
        )

        val result = businessRepository.searchActiveVerified("Restaurant", PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals(business.id, result.content[0].id)
    }

    @Test
    fun `searchActiveVerified should find businesses by description`() {
        val business = createBusiness(
            name = "Some Business",
            slug = "search-desc-${UUID.randomUUID()}",
            isActive = true,
            isVerified = true,
            description = "Great Italian restaurant"
        )

        val result = businessRepository.searchActiveVerified("Italian", PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals(business.id, result.content[0].id)
    }

    @Test
    fun `searchActiveVerified should be case insensitive`() {
        val business = createBusiness(
            name = "UPPERCASE Business",
            slug = "search-case-${UUID.randomUUID()}",
            isActive = true,
            isVerified = true
        )

        val result = businessRepository.searchActiveVerified("uppercase", PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
    }

    @Test
    fun `searchActiveVerified should not return inactive businesses`() {
        createBusiness(
            name = "Inactive Restaurant",
            slug = "search-inactive-${UUID.randomUUID()}",
            isActive = false,
            isVerified = true,
            description = "Restaurant"
        )

        val result = businessRepository.searchActiveVerified("Restaurant", PageRequest.of(0, 10))

        assertEquals(0, result.content.size)
    }
}
