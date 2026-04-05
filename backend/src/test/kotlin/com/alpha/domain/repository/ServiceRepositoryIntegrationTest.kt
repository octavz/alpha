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
import java.util.UUID

@Transactional
class ServiceRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var serviceRepository: ServiceRepository

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

    private fun createService(
        business: BusinessEntity = testBusiness,
        name: String = "Service ${UUID.randomUUID()}",
        isActive: Boolean = true,
        sortOrder: Int = 0,
        durationMinutes: Int = 60,
        price: BigDecimal? = BigDecimal("50.00"),
        description: String? = "Test Service Description"
    ): ServiceEntity {
        return serviceRepository.save(ServiceEntity().apply {
            this.business = business
            this.name = name
            this.isActive = isActive
            this.sortOrder = sortOrder
            this.durationMinutes = durationMinutes
            this.price = price
            this.description = description
        })
    }

    @Test
    fun `findByBusinessId should return all services for business`() {
        createService(name = "Service 1")
        createService(name = "Service 2")

        val result = serviceRepository.findByBusinessId(testBusiness.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByBusinessIdAndIsActiveTrue should return only active services`() {
        val active = createService(name = "Active Service", isActive = true)
        createService(name = "Inactive Service", isActive = false)

        val result = serviceRepository.findByBusinessIdAndIsActiveTrue(testBusiness.id!!)

        assertEquals(1, result.size)
        assertEquals(active.id, result[0].id)
    }

    @Test
    fun `findByBusinessIdOrdered should return services ordered by sortOrder`() {
        createService(name = "Second", sortOrder = 2)
        createService(name = "First", sortOrder = 1)
        createService(name = "Third", sortOrder = 3)

        val result = serviceRepository.findByBusinessIdOrdered(testBusiness.id!!)

        assertEquals(3, result.size)
        assertEquals("First", result[0].name)
        assertEquals("Second", result[1].name)
        assertEquals("Third", result[2].name)
    }
}
