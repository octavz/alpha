package com.alpha.service

import com.alpha.domain.entity.*
import com.alpha.domain.enums.AppointmentStatus
import com.alpha.domain.enums.UserRole
import com.alpha.domain.repository.*
import com.alpha.service.dto.*
import com.alpha.service.exception.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class AppointmentServiceIntegrationTest {

    private val appointmentRepository = mockk<AppointmentRepository>()
    private val businessRepository = mockk<BusinessRepository>()
    private val userRepository = mockk<UserRepository>()
    private val securityContextService = mockk<SecurityContextService>()

    private val appointmentService = AppointmentService(
        appointmentRepository,
        businessRepository,
        userRepository,
        securityContextService
    )

    private lateinit var testCustomer: UserEntity
    private lateinit var testBusiness: BusinessEntity
    private lateinit var testAppointment: AppointmentEntity

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        testCustomer = UserEntity().apply {
            id = UUID.randomUUID()
            email = "customer@example.com"
            name = "Customer"
            role = UserRole.CUSTOMER
        }

        val owner = UserEntity().apply {
            id = UUID.randomUUID()
            email = "owner@example.com"
            name = "Owner"
            role = UserRole.BUSINESS_ADMIN
        }

        testBusiness = BusinessEntity().apply {
            id = UUID.randomUUID()
            name = "Test Business"
            slug = "test-business"
            user = owner
            isActive = true
            isVerified = true
        }

        testAppointment = AppointmentEntity().apply {
            id = UUID.randomUUID()
            this.business = testBusiness
            this.customer = testCustomer
            appointmentDate = LocalDate.now()
            startTime = LocalTime.of(10, 0)
            endTime = LocalTime.of(11, 0)
            status = AppointmentStatus.PENDING
            customerName = "Test Customer"
            customerEmail = "test@example.com"
        }
    }

    @Test
    fun `createAppointment should create appointment`() {
        val request = CreateAppointmentRequest(
            businessId = testBusiness.id!!,
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            customerName = "Test Customer",
            customerEmail = "test@example.com"
        )

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { userRepository.findById(testCustomer.id!!) } returns Optional.of(testCustomer)
        every { appointmentRepository.findByBusinessIdAndAppointmentDateAndStatus(any(), any(), any()) } returns emptyList()
        every { appointmentRepository.save(any()) } answers {
            val a = firstArg<AppointmentEntity>()
            a.apply { id = UUID.randomUUID() }
        }

        val result = appointmentService.createAppointment(request)

        assertEquals(AppointmentStatus.PENDING, result.status)
    }

    @Test
    fun `createAppointment should throw NotFoundException for invalid business`() {
        val request = CreateAppointmentRequest(
            businessId = UUID.randomUUID(),
            appointmentDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            customerName = "Test",
            customerEmail = "test@example.com"
        )

        every { businessRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            appointmentService.createAppointment(request)
        }
    }

    @Test
    fun `createAppointment should throw BusinessException for inactive business`() {
        val inactiveBusiness = BusinessEntity().apply {
            id = UUID.randomUUID()
            name = "Inactive Business"
            slug = "inactive-business"
            isActive = false
            isVerified = true
        }
        val request = CreateAppointmentRequest(
            businessId = inactiveBusiness.id!!,
            appointmentDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            customerName = "Test",
            customerEmail = "test@example.com"
        )

        every { businessRepository.findById(inactiveBusiness.id!!) } returns Optional.of(inactiveBusiness)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { userRepository.findById(testCustomer.id!!) } returns Optional.of(testCustomer)

        assertThrows(BusinessException::class.java) {
            appointmentService.createAppointment(request)
        }
    }

    @Test
    fun `createAppointment should throw BusinessException for conflicting slot`() {
        val request = CreateAppointmentRequest(
            businessId = testBusiness.id!!,
            appointmentDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            customerName = "Test",
            customerEmail = "test@example.com"
        )

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { userRepository.findById(testCustomer.id!!) } returns Optional.of(testCustomer)
        every { appointmentRepository.findByBusinessIdAndAppointmentDateAndStatus(any(), any(), any()) } returns listOf(testAppointment)

        assertThrows(BusinessException::class.java) {
            appointmentService.createAppointment(request)
        }
    }

    @Test
    fun `updateAppointment should update for owner`() {
        val request = UpdateAppointmentRequest(customerName = "Updated Name")

        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER
        every { appointmentRepository.save(testAppointment) } returns testAppointment

        val result = appointmentService.updateAppointment(testAppointment.id!!, request)

        assertEquals("Updated Name", testAppointment.customerName)
    }

    @Test
    fun `updateAppointment should throw AuthorizationException for unauthorized user`() {
        val request = UpdateAppointmentRequest(customerName = "Updated")

        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        assertThrows(AuthorizationException::class.java) {
            appointmentService.updateAppointment(testAppointment.id!!, request)
        }
    }

    @Test
    fun `getAppointment should return appointment for owner`() {
        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        val result = appointmentService.getAppointment(testAppointment.id!!)

        assertEquals(AppointmentStatus.PENDING, result.status)
    }

    @Test
    fun `getAppointment should throw NotFoundException`() {
        every { appointmentRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            appointmentService.getAppointment(UUID.randomUUID())
        }
    }

    @Test
    fun `getAppointment should throw AuthorizationException for unauthorized user`() {
        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        assertThrows(AuthorizationException::class.java) {
            appointmentService.getAppointment(testAppointment.id!!)
        }
    }

    @Test
    fun `searchAppointments should return customer appointments for non-admin`() {
        val page = PageRequest.of(0, 10)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER
        every { appointmentRepository.findCustomerAppointments(testCustomer.id!!, page) } returns PageImpl(listOf(testAppointment))

        val result = appointmentService.searchAppointments(null, null, null, null, null, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `searchAppointments should return all appointments for admin`() {
        val page = PageRequest.of(0, 10)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.ADMIN
        every { appointmentRepository.findAll(page) } returns PageImpl(listOf(testAppointment))

        val result = appointmentService.searchAppointments(null, null, null, null, null, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `searchAppointments with businessId should filter by business`() {
        val page = PageRequest.of(0, 10)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER
        every { appointmentRepository.findByBusinessId(testBusiness.id!!) } returns listOf(testAppointment)

        val result = appointmentService.searchAppointments(testBusiness.id!!, null, null, null, null, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `cancelAppointment should cancel for owner`() {
        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER
        every { appointmentRepository.save(testAppointment) } returns testAppointment

        val result = appointmentService.cancelAppointment(testAppointment.id!!)

        assertEquals(AppointmentStatus.CANCELLED, result.status)
    }

    @Test
    fun `cancelAppointment should throw AuthorizationException for unauthorized user`() {
        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        assertThrows(AuthorizationException::class.java) {
            appointmentService.cancelAppointment(testAppointment.id!!)
        }
    }

    @Test
    fun `getBusinessAvailability should return available slots`() {
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { appointmentRepository.findByBusinessIdAndAppointmentDate(testBusiness.id!!, LocalDate.now().plusDays(1)) } returns emptyList()

        val slots = appointmentService.getBusinessAvailability(testBusiness.id!!, LocalDate.now().plusDays(1))

        assertTrue(slots.isNotEmpty())
        assertEquals(LocalTime.of(9, 0), slots[0])
    }

    @Test
    fun `getBusinessAvailability should exclude booked slots`() {
        val bookedAppointment = AppointmentEntity().apply {
            id = UUID.randomUUID()
            startTime = LocalTime.of(9, 0)
            endTime = LocalTime.of(10, 0)
            status = AppointmentStatus.PENDING
        }

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { appointmentRepository.findByBusinessIdAndAppointmentDate(testBusiness.id!!, LocalDate.now().plusDays(1)) } returns listOf(bookedAppointment)

        val slots = appointmentService.getBusinessAvailability(testBusiness.id!!, LocalDate.now().plusDays(1))

        // 9:00 is booked, 9:30 is within the 9-10 slot, 10:00 should be available
        assertFalse(slots.contains(LocalTime.of(9, 0)))
        assertFalse(slots.contains(LocalTime.of(9, 30)))
        assertTrue(slots.contains(LocalTime.of(10, 0)))
    }

    @Test
    fun `getBusinessAppointments should return for business owner`() {
        val page = PageRequest.of(0, 10)
        val owner = testBusiness.user!!

        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns owner.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.BUSINESS_ADMIN
        every { appointmentRepository.findBusinessAppointments(testBusiness.id!!, page) } returns PageImpl(listOf(testAppointment))

        val result = appointmentService.getBusinessAppointments(testBusiness.id!!, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `getBusinessAppointments should throw AuthorizationException for non-owner`() {
        val page = PageRequest.of(0, 10)
        every { businessRepository.findById(testBusiness.id!!) } returns Optional.of(testBusiness)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER

        assertThrows(AuthorizationException::class.java) {
            appointmentService.getBusinessAppointments(testBusiness.id!!, page)
        }
    }

    @Test
    fun `getBusinessAppointments should throw NotFoundException for invalid business`() {
        every { businessRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            appointmentService.getBusinessAppointments(UUID.randomUUID(), PageRequest.of(0, 10))
        }
    }

    @Test
    fun `updateAppointment should allow status change for admin`() {
        val request = UpdateAppointmentRequest(status = AppointmentStatus.CONFIRMED)

        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.ADMIN
        every { appointmentRepository.save(testAppointment) } returns testAppointment

        val result = appointmentService.updateAppointment(testAppointment.id!!, request)

        verify { appointmentRepository.save(testAppointment) }
    }

    @Test
    fun `updateAppointment should allow status change for business owner`() {
        val owner = testBusiness.user!!
        val request = UpdateAppointmentRequest(status = AppointmentStatus.CONFIRMED)

        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns owner.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.BUSINESS_ADMIN
        every { appointmentRepository.save(testAppointment) } returns testAppointment

        val result = appointmentService.updateAppointment(testAppointment.id!!, request)

        verify { appointmentRepository.save(testAppointment) }
    }

    @Test
    fun `searchAppointments should filter by status`() {
        val page = PageRequest.of(0, 10)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER
        every { appointmentRepository.findCustomerAppointments(testCustomer.id!!, page) } returns PageImpl(listOf(testAppointment))

        val result = appointmentService.searchAppointments(null, null, AppointmentStatus.PENDING, null, null, page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `searchAppointments should filter by date range`() {
        val page = PageRequest.of(0, 10)
        every { securityContextService.getCurrentUserId() } returns testCustomer.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.CUSTOMER
        every { appointmentRepository.findCustomerAppointments(testCustomer.id!!, page) } returns PageImpl(listOf(testAppointment))

        val today = LocalDate.now()
        val result = appointmentService.searchAppointments(null, null, null, today, today.plusDays(1), page)

        assertEquals(1, result.content.size)
    }

    @Test
    fun `cancelAppointment should cancel for business owner`() {
        val owner = testBusiness.user!!

        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns owner.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.BUSINESS_ADMIN
        every { appointmentRepository.save(testAppointment) } returns testAppointment

        val result = appointmentService.cancelAppointment(testAppointment.id!!)

        assertEquals(AppointmentStatus.CANCELLED, result.status)
    }

    @Test
    fun `cancelAppointment should cancel for admin`() {
        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.ADMIN
        every { appointmentRepository.save(testAppointment) } returns testAppointment

        val result = appointmentService.cancelAppointment(testAppointment.id!!)

        assertEquals(AppointmentStatus.CANCELLED, result.status)
    }

    @Test
    fun `getAppointment should allow access for business owner`() {
        val owner = testBusiness.user!!

        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns owner.id!!
        every { securityContextService.getCurrentUserRole() } returns UserRole.BUSINESS_ADMIN

        val result = appointmentService.getAppointment(testAppointment.id!!)

        assertEquals(AppointmentStatus.PENDING, result.status)
    }

    @Test
    fun `getAppointment should allow access for admin`() {
        every { appointmentRepository.findById(testAppointment.id!!) } returns Optional.of(testAppointment)
        every { securityContextService.getCurrentUserId() } returns UUID.randomUUID()
        every { securityContextService.getCurrentUserRole() } returns UserRole.ADMIN

        val result = appointmentService.getAppointment(testAppointment.id!!)

        assertEquals(AppointmentStatus.PENDING, result.status)
    }
}
