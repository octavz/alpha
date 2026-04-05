package com.alpha.web.controller

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.*
import com.alpha.domain.enums.AppointmentStatus
import com.alpha.domain.enums.UserRole
import com.alpha.domain.repository.*
import com.alpha.service.JwtService
import com.alpha.service.dto.CreateAppointmentRequest
import com.alpha.service.dto.UpdateAppointmentRequest
import tools.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@AutoConfigureMockMvc
class AppointmentControllerIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var businessRepository: BusinessRepository

    @Autowired
    private lateinit var regionRepository: RegionRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var appointmentRepository: AppointmentRepository

    @Autowired
    private lateinit var jwtService: JwtService

    private lateinit var testCustomer: UserEntity
    private lateinit var testBusiness: BusinessEntity
    private lateinit var testAppointment: AppointmentEntity
    private lateinit var customerAuth: String

    @BeforeEach
    fun setUp() {
        try { appointmentRepository.deleteAll() } catch (_: Exception) {}
        try { businessRepository.deleteAll() } catch (_: Exception) {}
        try { userRepository.deleteAll() } catch (_: Exception) {}
        try { regionRepository.deleteAll() } catch (_: Exception) {}
        try { categoryRepository.deleteAll() } catch (_: Exception) {}

        val region = regionRepository.save(RegionEntity().apply {
            name = "Test Region"
            code = "TR-${UUID.randomUUID()}"
            country = "Test"
            timezone = "UTC"
            isActive = true
        })

        val owner = userRepository.save(UserEntity().apply {
            email = "owner-${UUID.randomUUID()}@example.com"
            name = "Owner"
            role = UserRole.BUSINESS_ADMIN
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
            this.region = region
            this.category = cat
            isActive = true
            isVerified = true
        })

        testCustomer = userRepository.save(UserEntity().apply {
            email = "customer-${UUID.randomUUID()}@example.com"
            name = "Test Customer"
            role = UserRole.CUSTOMER
        })

        testAppointment = appointmentRepository.save(AppointmentEntity().apply {
            this.business = testBusiness
            this.customer = testCustomer
            appointmentDate = LocalDate.now()
            startTime = LocalTime.of(10, 0)
            endTime = LocalTime.of(11, 0)
            status = AppointmentStatus.PENDING
            customerName = "Test Customer"
            customerEmail = "test@example.com"
        })

        customerAuth = "Bearer ${jwtService.generateAccessToken(testCustomer)}"
    }

    @Test
    fun `createAppointment should create appointment successfully`() {
        val request = CreateAppointmentRequest(
            businessId = testBusiness.id!!,
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            customerName = "Test Customer",
            customerEmail = "test@example.com"
        )

        mockMvc.perform(
            post("/api/v1/appointments")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.businessId").value(testBusiness.id!!.toString()))
    }

    @Test
    fun `getAppointment should return appointment when found`() {
        mockMvc.perform(
            get("/api/v1/appointments/${testAppointment.id}")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(testAppointment.id.toString()))
    }

    @Test
    fun `getAppointment should return not found when not exists`() {
        mockMvc.perform(
            get("/api/v1/appointments/${UUID.randomUUID()}")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `updateAppointment should update appointment successfully`() {
        val request = UpdateAppointmentRequest(
            customerName = "Updated Name"
        )

        mockMvc.perform(
            put("/api/v1/appointments/${testAppointment.id}")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.customerName").value("Updated Name"))
    }

    @Test
    fun `updateAppointment should return not found when not exists`() {
        val request = UpdateAppointmentRequest(customerName = "Updated")

        mockMvc.perform(
            put("/api/v1/appointments/${UUID.randomUUID()}")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `cancelAppointment should cancel appointment successfully`() {
        mockMvc.perform(
            post("/api/v1/appointments/${testAppointment.id}/cancel")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.status").value("CANCELLED"))
    }

    @Test
    fun `cancelAppointment should return not found when not exists`() {
        mockMvc.perform(
            post("/api/v1/appointments/${UUID.randomUUID()}/cancel")
                .header("Authorization", customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `searchAppointments should return customer appointments`() {
        mockMvc.perform(
            get("/api/v1/appointments/search")
                .header("Authorization", customerAuth)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getBusinessAppointments should return business appointments`() {
        val ownerAuth = "Bearer ${jwtService.generateAccessToken(testBusiness.user!!)}"

        mockMvc.perform(
            get("/api/v1/appointments/business/${testBusiness.id}")
                .header("Authorization", ownerAuth)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getBusinessAvailability should return available time slots`() {
        mockMvc.perform(
            get("/api/v1/appointments/business/${testBusiness.id}/availability")
                .param("date", LocalDate.now().plusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `createAppointment should require authentication`() {
        val request = CreateAppointmentRequest(
            businessId = testBusiness.id!!,
            appointmentDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            customerName = "Test Customer",
            customerEmail = "test@example.com"
        )

        mockMvc.perform(
            post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `getAppointment should require authentication`() {
        mockMvc.perform(
            get("/api/v1/appointments/${testAppointment.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `updateAppointment should require authentication`() {
        val request = UpdateAppointmentRequest(customerName = "Updated")

        mockMvc.perform(
            put("/api/v1/appointments/${testAppointment.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `cancelAppointment should require authentication`() {
        mockMvc.perform(
            post("/api/v1/appointments/${testAppointment.id}/cancel")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `searchAppointments should require authentication`() {
        mockMvc.perform(
            get("/api/v1/appointments/search")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `getBusinessAppointments should require authentication`() {
        mockMvc.perform(
            get("/api/v1/appointments/business/${testBusiness.id}")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }
}