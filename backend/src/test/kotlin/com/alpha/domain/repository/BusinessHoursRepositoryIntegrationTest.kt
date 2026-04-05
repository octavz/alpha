package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.*
import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

@Transactional
class BusinessHoursRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var businessHoursRepository: BusinessHoursRepository

    @Autowired
    private lateinit var businessRepository: BusinessRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var regionRepository: RegionRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var testBusiness: BusinessEntity

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
    }

    private fun createBusinessHours(
        business: BusinessEntity = testBusiness,
        dayOfWeek: Int = 1,
        openTime: LocalTime? = LocalTime.of(9, 0),
        closeTime: LocalTime? = LocalTime.of(17, 0),
        isClosed: Boolean = false
    ): BusinessHoursEntity {
        return businessHoursRepository.save(BusinessHoursEntity(
            business = business,
            dayOfWeek = dayOfWeek,
            openTime = openTime,
            closeTime = closeTime,
            isClosed = isClosed
        ))
    }

    @Test
    fun `findByBusinessId should return all business hours for business`() {
        createBusinessHours(dayOfWeek = 1)
        createBusinessHours(dayOfWeek = 2)
        createBusinessHours(dayOfWeek = 3)

        val result = businessHoursRepository.findByBusinessId(testBusiness.id!!)

        assertEquals(3, result.size)
    }

    @Test
    fun `findByBusinessIdOrdered should return hours ordered by dayOfWeek`() {
        createBusinessHours(dayOfWeek = 3)
        createBusinessHours(dayOfWeek = 1)
        createBusinessHours(dayOfWeek = 2)

        val result = businessHoursRepository.findByBusinessIdOrdered(testBusiness.id!!)

        assertEquals(3, result.size)
        assertEquals(1, result[0].dayOfWeek)
        assertEquals(2, result[1].dayOfWeek)
        assertEquals(3, result[2].dayOfWeek)
    }

    @Test
    fun `findByBusinessIdAndDayOfWeek should return hours for specific day`() {
        createBusinessHours(dayOfWeek = 1)
        createBusinessHours(dayOfWeek = 2)

        val result = businessHoursRepository.findByBusinessIdAndDayOfWeek(testBusiness.id!!, 1)

        assertNotNull(result)
        assertEquals(1, result?.dayOfWeek)
    }

    @Test
    fun `findByBusinessIdAndDayOfWeek should return null when no hours for day`() {
        createBusinessHours(dayOfWeek = 1)

        val result = businessHoursRepository.findByBusinessIdAndDayOfWeek(testBusiness.id!!, 5)

        assertNull(result)
    }
}
