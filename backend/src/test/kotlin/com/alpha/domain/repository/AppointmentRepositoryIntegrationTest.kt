package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.*
import com.alpha.domain.enums.AppointmentStatus
import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Transactional
class AppointmentRepositoryIntegrationTest : AbstractIntegrationTest() {

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

    private fun createAppointment(
        business: BusinessEntity = testBusiness,
        customer: UserEntity = testCustomer,
        date: LocalDate = LocalDate.now(),
        startTime: LocalTime = LocalTime.of(10, 0),
        endTime: LocalTime = LocalTime.of(11, 0),
        status: AppointmentStatus = AppointmentStatus.PENDING,
        customerName: String = "Test Customer",
        customerEmail: String = "test@example.com"
    ): AppointmentEntity {
        return appointmentRepository.save(AppointmentEntity().apply {
            this.business = business
            this.customer = customer
            this.appointmentDate = date
            this.startTime = startTime
            this.endTime = endTime
            this.status = status
            this.customerName = customerName
            this.customerEmail = customerEmail
        })
    }

    @Test
    fun `findByBusinessId should return all appointments for business`() {
        createAppointment()
        createAppointment()

        val result = appointmentRepository.findByBusinessId(testBusiness.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByCustomerId should return all appointments for customer`() {
        createAppointment()
        createAppointment()

        val result = appointmentRepository.findByCustomerId(testCustomer.id!!)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByStatus should return appointments with specific status`() {
        createAppointment(status = AppointmentStatus.CONFIRMED)
        createAppointment(status = AppointmentStatus.PENDING)

        val result = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED)

        assertEquals(1, result.size)
        assertEquals(AppointmentStatus.CONFIRMED, result[0].status)
    }

    @Test
    fun `findByBusinessIdAndAppointmentDate should return appointments on specific date`() {
        val today = LocalDate.now()
        val tomorrow = LocalDate.now().plusDays(1)

        createAppointment(date = today)
        createAppointment(date = today)
        createAppointment(date = tomorrow)

        val result = appointmentRepository.findByBusinessIdAndAppointmentDate(testBusiness.id!!, today)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByBusinessIdAndAppointmentDateAndStatus should filter by date and status`() {
        val today = LocalDate.now()

        createAppointment(date = today, status = AppointmentStatus.CONFIRMED)
        createAppointment(date = today, status = AppointmentStatus.PENDING)

        val result = appointmentRepository.findByBusinessIdAndAppointmentDateAndStatus(
            testBusiness.id!!, today, AppointmentStatus.CONFIRMED
        )

        assertEquals(1, result.size)
        assertEquals(AppointmentStatus.CONFIRMED, result[0].status)
    }

    @Test
    fun `findByBusinessIdAndDateRange should return appointments within date range`() {
        val start = LocalDate.now()
        val end = start.plusDays(7)

        createAppointment(date = start)
        createAppointment(date = start.plusDays(3))
        createAppointment(date = end)
        createAppointment(date = end.plusDays(1))

        val result = appointmentRepository.findByBusinessIdAndDateRange(testBusiness.id!!, start, end)

        assertEquals(3, result.size)
    }

    @Test
    fun `findBusinessAppointmentsByDateAndStatus should return appointments with multiple statuses`() {
        val today = LocalDate.now()

        createAppointment(date = today, status = AppointmentStatus.PENDING)
        createAppointment(date = today, status = AppointmentStatus.CONFIRMED)
        createAppointment(date = today, status = AppointmentStatus.CANCELLED)

        val result = appointmentRepository.findBusinessAppointmentsByDateAndStatus(
            testBusiness.id!!, today, listOf(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
        )

        assertEquals(2, result.size)
    }

    @Test
    fun `findBusinessAppointmentsByDateAndStatus should order by startTime`() {
        val today = LocalDate.now()

        createAppointment(date = today, startTime = LocalTime.of(14, 0), status = AppointmentStatus.PENDING)
        createAppointment(date = today, startTime = LocalTime.of(9, 0), status = AppointmentStatus.PENDING)

        val result = appointmentRepository.findBusinessAppointmentsByDateAndStatus(
            testBusiness.id!!, today, listOf(AppointmentStatus.PENDING)
        )

        assertEquals(2, result.size)
        assertEquals(LocalTime.of(9, 0), result[0].startTime)
        assertEquals(LocalTime.of(14, 0), result[1].startTime)
    }

    @Test
    fun `findCustomerAppointments should return paginated customer appointments`() {
        createAppointment(customer = testCustomer)
        createAppointment(customer = testCustomer)

        val result = appointmentRepository.findCustomerAppointments(testCustomer.id!!, PageRequest.of(0, 10))

        assertEquals(2, result.content.size)
        assertTrue(result.content.all { it.customer?.id == testCustomer.id })
    }

    @Test
    fun `findBusinessAppointments should return paginated business appointments`() {
        createAppointment(business = testBusiness)
        createAppointment(business = testBusiness)

        val result = appointmentRepository.findBusinessAppointments(testBusiness.id!!, PageRequest.of(0, 10))

        assertEquals(2, result.content.size)
        assertTrue(result.content.all { it.business?.id == testBusiness.id })
    }
}
