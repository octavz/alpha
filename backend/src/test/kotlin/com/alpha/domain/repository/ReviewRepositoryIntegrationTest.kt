package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.*
import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
class ReviewRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var appointmentRepository: AppointmentRepository

    @Autowired
    private lateinit var businessRepository: BusinessRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var regionRepository: RegionRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var testBusiness: BusinessEntity
    private lateinit var testCustomer: UserEntity

    @BeforeEach
    fun setUp() {
        val owner = userRepository.save(UserEntity().apply {
            email = "owner-${UUID.randomUUID()}@example.com"
            name = "Owner"
            role = UserRole.BUSINESS_ADMIN
        })

        val r = regionRepository.save(RegionEntity().apply {
            name = "Test Region"
            code = "TR-${UUID.randomUUID()}"
            country = "Test"
            timezone = "UTC"
            isActive = true
        })

        val cat = categoryRepository.save(CategoryEntity().apply {
            name = "Test Category"
            slug = "test-cat-${UUID.randomUUID()}"
            isActive = true
        })

        testBusiness = businessRepository.save(BusinessEntity().apply {
            name = "Test Business"
            slug = "test-biz-${UUID.randomUUID()}"
            user = owner
            this.region = r
            category = cat
            isActive = true
            isVerified = true
        })

        testCustomer = userRepository.save(UserEntity().apply {
            email = "customer-${UUID.randomUUID()}@example.com"
            name = "Test Customer"
            role = UserRole.CUSTOMER
        })
    }

    private fun createReview(
        business: BusinessEntity = testBusiness,
        customer: UserEntity = testCustomer,
        rating: Int = 5,
        isApproved: Boolean = false,
        isFeatured: Boolean = false,
        title: String? = "Test Review",
        comment: String? = "Test Comment"
    ): ReviewEntity {
        return reviewRepository.save(ReviewEntity(
            business = business,
            customer = customer,
            rating = rating,
            isApproved = isApproved,
            isFeatured = isFeatured,
            title = title,
            comment = comment
        ))
    }

    @Test
    fun `findByBusinessId should return all reviews for business`() {
        createReview(rating = 5)
        createReview(rating = 4)

        val result = reviewRepository.findByBusinessId(testBusiness.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByCustomerId should return all reviews by customer`() {
        createReview(rating = 5, customer = testCustomer)
        createReview(rating = 4, customer = testCustomer)

        val result = reviewRepository.findByCustomerId(testCustomer.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByIsApprovedTrue should return only approved reviews`() {
        val approved = createReview(rating = 5, isApproved = true)
        createReview(rating = 3, isApproved = false)

        val result = reviewRepository.findByIsApprovedTrue()

        assertTrue(result.any { it.id == approved.id })
        assertEquals(1, result.count { it.isApproved })
    }

    @Test
    fun `findByIsFeaturedTrue should return only featured reviews`() {
        val featured = createReview(rating = 5, isApproved = true, isFeatured = true)
        createReview(rating = 4, isApproved = true, isFeatured = false)

        val result = reviewRepository.findByIsFeaturedTrue()

        assertEquals(1, result.size)
        assertEquals(featured.id, result[0].id)
    }

    @Test
    fun `findApprovedByBusiness should return approved reviews paginated`() {
        createReview(rating = 5, isApproved = true)
        createReview(rating = 4, isApproved = true)
        createReview(rating = 3, isApproved = false)

        val result = reviewRepository.findApprovedByBusiness(testBusiness.id!!, PageRequest.of(0, 10))

        assertEquals(2, result.content.size)
        assertTrue(result.content.all { it.isApproved })
    }

    @Test
    fun `findFeaturedByBusiness should return approved and featured reviews`() {
        val featured = createReview(rating = 5, isApproved = true, isFeatured = true)
        createReview(rating = 4, isApproved = true, isFeatured = false)
        createReview(rating = 3, isApproved = false, isFeatured = true)

        val result = reviewRepository.findFeaturedByBusiness(testBusiness.id!!, PageRequest.of(0, 10))

        assertEquals(1, result.content.size)
        assertEquals(featured.id, result.content[0].id)
    }

    @Test
    fun `calculateAverageRating should return average of approved reviews`() {
        createReview(rating = 5, isApproved = true)
        createReview(rating = 3, isApproved = true)
        createReview(rating = 4, isApproved = false)

        val avg = reviewRepository.calculateAverageRating(testBusiness.id!!)

        assertNotNull(avg)
        assertEquals(4.0, avg!!, 0.01)
    }

    @Test
    fun `calculateAverageRating should return null when no approved reviews`() {
        val avg = reviewRepository.calculateAverageRating(testBusiness.id!!)
        assertNull(avg)
    }

    @Test
    fun `countApprovedByBusiness should return count of approved reviews`() {
        createReview(rating = 5, isApproved = true)
        createReview(rating = 4, isApproved = true)
        createReview(rating = 3, isApproved = false)

        val count = reviewRepository.countApprovedByBusiness(testBusiness.id!!)

        assertEquals(2, count)
    }

    @Test
    fun `getRatingDistribution should return rating counts grouped by rating`() {
        createReview(rating = 5, isApproved = true)
        createReview(rating = 5, isApproved = true)
        createReview(rating = 3, isApproved = true)
        createReview(rating = 4, isApproved = false)

        val distribution = reviewRepository.getRatingDistribution(testBusiness.id!!)

        assertEquals(2, distribution.size)
    }

    @Test
    fun `findByAppointmentId should return review when exists`() {
        val appointment = appointmentRepository.save(AppointmentEntity().apply {
            this.business = testBusiness
            this.customer = testCustomer
            appointmentDate = java.time.LocalDate.now()
            startTime = java.time.LocalTime.of(10, 0)
            endTime = java.time.LocalTime.of(11, 0)
            status = com.alpha.domain.enums.AppointmentStatus.COMPLETED
            customerName = "Test"
            customerEmail = "test@test.com"
        })

        val review = reviewRepository.save(ReviewEntity(
            business = testBusiness,
            customer = testCustomer,
            rating = 5,
            isApproved = true,
            appointment = appointment
        ))

        val result = reviewRepository.findByAppointmentId(appointment.id!!)

        assertNotNull(result)
        assertEquals(review.id, result!!.id)
    }

    @Test
    fun `findByAppointmentId should return null when not exists`() {
        val result = reviewRepository.findByAppointmentId(UUID.randomUUID())

        assertNull(result)
    }
}
