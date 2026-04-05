package com.alpha.service

import com.alpha.domain.entity.*
import com.alpha.domain.enums.AppointmentStatus
import com.alpha.domain.enums.UserRole
import com.alpha.domain.enums.VerificationStatus
import com.alpha.domain.repository.*
import com.alpha.service.dto.*
import com.alpha.service.exception.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class BusinessServiceIntegrationTest {

    private val businessRepository = mockk<BusinessRepository>()
    private val userRepository = mockk<UserRepository>()
    private val regionRepository = mockk<RegionRepository>()
    private val categoryRepository = mockk<CategoryRepository>()
    private val securityContextService = mockk<SecurityContextService>()

    private val businessService = BusinessService(
        businessRepository,
        userRepository,
        regionRepository,
        categoryRepository,
        securityContextService
    )

    private lateinit var testUser: UserEntity
    private lateinit var testRegion: RegionEntity
    private lateinit var testCategory: CategoryEntity
    private lateinit var testBusiness: BusinessEntity

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        testUser = UserEntity().apply {
            id = UUID.randomUUID()
            email = "owner@example.com"
            name = "Owner"
            role = UserRole.BUSINESS_ADMIN
        }

        testRegion = RegionEntity().apply {
            id = UUID.randomUUID()
            name = "Test Region"
            code = "TR"
            country = "Test"
            timezone = "UTC"
            isActive = true
        }

        testCategory = CategoryEntity().apply {
            id = UUID.randomUUID()
            name = "Test Category"
            slug = "test-category"
            isActive = true
        }

        testBusiness = BusinessEntity().apply {
            id = UUID.randomUUID()
            name = "Test Business"
            slug = "test-business"
            user = testUser
            region = testRegion
            category = testCategory
            isActive = true
            isVerified = true
            isFeatured = false
            verificationStatus = VerificationStatus.APPROVED
        }
    }

    @Test
    fun `createBusiness should create and return business`() {
        val request = CreateBusinessRequest(
            name = "New Business",
            description = "Description",
            categoryId = testCategory.id!!,
            regionId = testRegion.id!!,
            addressLine1 = "123 Test St"
        )

        every { securityContextService.getCurrentUserId() } returns testUser.id!!
        every { userRepository.findById(testUser.id!!) } returns Optional.of(testUser)
        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)
        every { categoryRepository.findById(testCategory.id!!) } returns Optional.of(testCategory)
        every { businessRepository.existsBySlug(any()) } returns false
        every { businessRepository.save(any()) } answers {
            val b = firstArg<BusinessEntity>()
            b.apply { id = UUID.randomUUID() }
        }

        val result = businessService.createBusiness(request)

        assertEquals("New Business", result.name)
        assertEquals("new-business", result.slug)
    }

    @Test
    fun `createBusiness should throw NotFoundException for invalid region`() {
        val request = CreateBusinessRequest(
            name = "New Business",
            categoryId = testCategory.id!!,
            regionId = UUID.randomUUID()
        )

        every { securityContextService.getCurrentUserId() } returns testUser.id!!
        every { userRepository.findById(testUser.id!!) } returns Optional.of(testUser)
        every { regionRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            businessService.createBusiness(request)
        }
    }

    @Test
    fun `updateBusiness should update business for owner`() {
        val request = UpdateBusinessRequest(
            name = "Updated Business",
            description = "Updated Description"
        )

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns testUser.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.BUSINESS_ADMIN
        every { businessRepository.save(testBusiness) } returns testBusiness

        val result = businessService.updateBusiness(testBusiness.id!!, request)

        assertEquals("Updated Business", testBusiness.name)
        assertEquals("Updated Description", testBusiness.description)
    }

    @Test
    fun `updateBusiness should throw AuthorizationException for non-owner`() {
        val request = UpdateBusinessRequest(name = "Updated")
        val otherUser = UUID.randomUUID()

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns otherUser
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        assertThrows(AuthorizationException::class.java) {
            businessService.updateBusiness(testBusiness.id!!, request)
        }
    }

    @Test
    fun `updateBusiness should allow admin to update any business`() {
        val request = UpdateBusinessRequest(name = "Admin Updated")

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.ADMIN
        every { businessRepository.save(testBusiness) } returns testBusiness

        val result = businessService.updateBusiness(testBusiness.id!!, request)

        assertEquals("Admin Updated", testBusiness.name)
    }

    @Test
    fun `getBusiness should return business`() {
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)

        val result = businessService.getBusiness(testBusiness.id!!)

        assertEquals("Test Business", result.name)
    }

    @Test
    fun `getBusiness should throw NotFoundException`() {
        every { businessRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            businessService.getBusiness(UUID.randomUUID())
        }
    }

    @Test
    fun `getBusinessBySlug should return business`() {
        every { businessRepository.findBySlug("test-business") } returns testBusiness

        val result = businessService.getBusinessBySlug("test-business")

        assertEquals("Test Business", result.name)
    }

    @Test
    fun `getBusinessBySlug should throw NotFoundException`() {
        every { businessRepository.findBySlug("nonexistent") } returns null

        assertThrows(NotFoundException::class.java) {
            businessService.getBusinessBySlug("nonexistent")
        }
    }

    @Test
    fun `searchBusinesses with query should use searchActiveVerified`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.searchActiveVerified("test", page) } returns PageImpl(emptyList())

        val result = businessService.searchBusinesses("test", null, null, page)

        assertEquals(0, result.content.size)
    }

    @Test
    fun `searchBusinesses with regionId should use findActiveVerifiedByRegion`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findActiveVerifiedByRegion(testRegion.id!!, page) } returns PageImpl(emptyList())

        val result = businessService.searchBusinesses(null, testRegion.id!!, null, page)

        assertEquals(0, result.content.size)
    }

    @Test
    fun `searchBusinesses with categoryId should use findActiveVerifiedByCategory`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findActiveVerifiedByCategory(testCategory.id!!, page) } returns PageImpl(emptyList())

        val result = businessService.searchBusinesses(null, null, testCategory.id!!, page)

        assertEquals(0, result.content.size)
    }

    @Test
    fun `searchBusinesses with no filters should use findActiveVerifiedBusinesses`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findActiveVerifiedBusinesses(page) } returns PageImpl(emptyList())

        val result = businessService.searchBusinesses(null, null, null, page)

        assertEquals(0, result.content.size)
    }

    @Test
    fun `getUserBusinesses should return paginated businesses`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findByUserId(testUser.id!!) } returns listOf(testBusiness)

        val result = businessService.getUserBusinesses(testUser.id!!, page)

        assertEquals(1, result.content.size)
        assertEquals(1L, result.totalElements)
    }

    @Test
    fun `getBusinessesByRegion should return businesses`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findActiveVerifiedByRegion(testRegion.id!!, page) } returns PageImpl(listOf(testBusiness))

        val result = businessService.getBusinessesByRegion(testRegion.id!!, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `getBusinessesByCategory should return businesses`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findActiveVerifiedByCategory(testCategory.id!!, page) } returns PageImpl(listOf(testBusiness))

        val result = businessService.getBusinessesByCategory(testCategory.id!!, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `getFeaturedBusinesses should return featured businesses`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findByIsFeaturedTrue() } returns listOf(testBusiness)

        val result = businessService.getFeaturedBusinesses(page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `verifyBusiness should approve business for admin`() {
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.isCurrentUserAdmin() } returns true
        every { businessRepository.save(testBusiness) } returns testBusiness

        val result = businessService.verifyBusiness(testBusiness.id!!)

        assertEquals(VerificationStatus.APPROVED, testBusiness.verificationStatus)
    }

    @Test
    fun `verifyBusiness should throw AuthorizationException for non-admin`() {
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.isCurrentUserAdmin() } returns false

        assertThrows(AuthorizationException::class.java) {
            businessService.verifyBusiness(testBusiness.id!!)
        }
    }

    @Test
    fun `verifyBusiness should throw NotFoundException`() {
        every { businessRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            businessService.verifyBusiness(UUID.randomUUID())
        }
    }

    @Test
    fun `deleteBusiness should delete for owner`() {
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns testUser.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.BUSINESS_ADMIN
        every { businessRepository.delete(testBusiness) } just runs

        businessService.deleteBusiness(testBusiness.id!!)

        verify { businessRepository.delete(testBusiness) }
    }

    @Test
    fun `deleteBusiness should throw AuthorizationException for non-owner`() {
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        assertThrows(AuthorizationException::class.java) {
            businessService.deleteBusiness(testBusiness.id!!)
        }
    }

    @Test
    fun `deleteBusiness should throw NotFoundException`() {
        every { businessRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            businessService.deleteBusiness(UUID.randomUUID())
        }
    }

    @Test
    fun `createBusiness should throw ConflictException for duplicate slug`() {
        val existingBusiness = BusinessEntity().apply {
            id = UUID.randomUUID()
            name = "Existing Business"
            slug = "existing-business"
        }
        val request = CreateBusinessRequest(
            name = "Existing Business",
            categoryId = testCategory.id!!,
            regionId = testRegion.id!!
        )

        every { securityContextService.getCurrentUserId() } returns testUser.id!!
        every { userRepository.findById(testUser.id!!) } returns Optional.of(testUser)
        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)
        every { categoryRepository.findById(testCategory.id!!) } returns Optional.of(testCategory)
        every { businessRepository.existsBySlug("existing-business") } returns true

        assertThrows(ConflictException::class.java) {
            businessService.createBusiness(request)
        }
    }

    @Test
    fun `searchBusinesses should search by query`() {
        every { businessRepository.searchActiveVerified(any(), any()) } returns PageImpl(listOf(testBusiness))

        val result = businessService.searchBusinesses("test", null, null, PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
    }
}
