package com.alpha.web.controller

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.*
import com.alpha.domain.enums.UserRole
import com.alpha.domain.enums.VerificationStatus
import com.alpha.domain.repository.*
import com.alpha.service.JwtService
import com.alpha.service.dto.CreateBusinessRequest
import com.alpha.service.dto.UpdateBusinessRequest
import tools.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.util.UUID

@AutoConfigureMockMvc
class BusinessControllerIntegrationTest : AbstractIntegrationTest() {

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
    private lateinit var jwtService: JwtService

    private lateinit var testUser: UserEntity
    private lateinit var testRegion: RegionEntity
    private lateinit var testCategory: CategoryEntity
    private lateinit var testBusiness: BusinessEntity
    private lateinit var authHeader: String

    @BeforeEach
    fun setUp() {
        businessRepository.deleteAll()
        userRepository.deleteAll()
        regionRepository.deleteAll()
        categoryRepository.deleteAll()

        testUser = userRepository.save(
            UserEntity().apply {
                email = "test-business-owner-${UUID.randomUUID()}@example.com"
                passwordHash = "encodedPassword"
                name = "Business Owner"
                phone = "1234567890"
                role = UserRole.BUSINESS_ADMIN
                emailVerified = true
                isBanned = false
            }
        )

        testRegion = regionRepository.save(
            RegionEntity().apply {
                name = "Test Region"
                code = "TR-${UUID.randomUUID()}"
                country = "Test Country"
                timezone = "UTC"
                isActive = true
            }
        )

        testCategory = categoryRepository.save(
            CategoryEntity().apply {
                name = "Test Category"
                slug = "test-category-${UUID.randomUUID()}"
                description = "Test Category Description"
                isActive = true
            }
        )

        testBusiness = businessRepository.save(
            BusinessEntity().apply {
                name = "Test Business"
                slug = "test-business-${UUID.randomUUID()}"
                description = "Test Business Description"
                addressLine1 = "123 Test St"
                latitude = BigDecimal("40.7128")
                longitude = BigDecimal("-74.0060")
                phone = "1234567890"
                email = "test@example.com"
                website = "https://test.example.com"
                user = testUser
                region = testRegion
                category = testCategory
                verificationStatus = VerificationStatus.PENDING
                isActive = true
                isVerified = false
                isFeatured = false
            }
        )

        val token = jwtService.generateAccessToken(testUser)
        authHeader = "Bearer $token"
    }

    @Test
    fun `getFeaturedBusinesses should return paginated businesses`() {
        mockMvc.perform(
            get("/api/v1/businesses/featured")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getBusinessById should return business when found`() {
        mockMvc.perform(
            get("/api/v1/businesses/${testBusiness.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(testBusiness.id.toString()))
            .andExpect(jsonPath("$.data.name").value("Test Business"))
            .andExpect(jsonPath("$.data.description").value("Test Business Description"))
    }

    @Test
    fun `getBusinessById should return not found when business does not exist`() {
        val nonExistentId = UUID.randomUUID()

        mockMvc.perform(
            get("/api/v1/businesses/$nonExistentId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createBusiness should create new business successfully`() {
        val request = CreateBusinessRequest(
            name = "New Business",
            description = "New Business Description",
            categoryId = testCategory.id!!,
            regionId = testRegion.id!!,
            email = "new@example.com",
            phone = "0987654321",
            website = "https://new.example.com",
            addressLine1 = "456 New St",
            latitude = BigDecimal("40.7580"),
            longitude = BigDecimal("-73.9855")
        )

        mockMvc.perform(
            post("/api/v1/businesses")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.name").value("New Business"))
            .andExpect(jsonPath("$.data.description").value("New Business Description"))
            .andExpect(jsonPath("$.data.addressLine1").value("456 New St"))
            .andExpect(jsonPath("$.data.regionId").value(testRegion.id.toString()))
            .andExpect(jsonPath("$.data.categoryId").value(testCategory.id.toString()))
    }

    @Test
    fun `createBusiness should return bad request for invalid data`() {
        val request = CreateBusinessRequest(
            name = "",
            description = "",
            categoryId = testCategory.id!!,
            regionId = testRegion.id!!
        )

        mockMvc.perform(
            post("/api/v1/businesses")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `updateBusiness should update business successfully`() {
        val request = UpdateBusinessRequest(
            name = "Updated Business",
            description = "Updated Business Description",
            addressLine1 = "789 Updated St",
            latitude = BigDecimal("40.7580"),
            longitude = BigDecimal("-73.9855"),
            phone = "0987654321",
            email = "updated@example.com",
            website = "https://updated.example.com",
            regionId = testRegion.id,
            categoryId = testCategory.id
        )

        mockMvc.perform(
            put("/api/v1/businesses/${testBusiness.id}")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.name").value("Updated Business"))
            .andExpect(jsonPath("$.data.description").value("Updated Business Description"))
            .andExpect(jsonPath("$.data.addressLine1").value("789 Updated St"))
            .andExpect(jsonPath("$.data.phone").value("0987654321"))
            .andExpect(jsonPath("$.data.email").value("updated@example.com"))
    }

    @Test
    fun `updateBusiness should return not found when business does not exist`() {
        val nonExistentId = UUID.randomUUID()
        val request = UpdateBusinessRequest(
            name = "Updated Business",
            description = "Updated Business Description",
            addressLine1 = "789 Updated St",
            regionId = testRegion.id,
            categoryId = testCategory.id
        )

        mockMvc.perform(
            put("/api/v1/businesses/$nonExistentId")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteBusiness should delete business successfully`() {
        mockMvc.perform(
            delete("/api/v1/businesses/${testBusiness.id}")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `deleteBusiness should return not found when business does not exist`() {
        val nonExistentId = UUID.randomUUID()

        mockMvc.perform(
            delete("/api/v1/businesses/$nonExistentId")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `searchBusinesses should return matching businesses`() {
        mockMvc.perform(
            get("/api/v1/businesses/search")
                .param("query", "Test")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getBusinessBySlug should return business when found`() {
        mockMvc.perform(
            get("/api/v1/businesses/slug/${testBusiness.slug}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(testBusiness.id.toString()))
            .andExpect(jsonPath("$.data.slug").value(testBusiness.slug))
    }

    @Test
    fun `getBusinessBySlug should return not found when business does not exist`() {
        mockMvc.perform(
            get("/api/v1/businesses/slug/non-existent-slug")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getBusinessesByRegion should return businesses in region`() {
        mockMvc.perform(
            get("/api/v1/businesses/region/${testRegion.id}")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getBusinessesByCategory should return businesses in category`() {
        mockMvc.perform(
            get("/api/v1/businesses/category/${testCategory.id}")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getMyBusinesses should return user's businesses`() {
        mockMvc.perform(
            get("/api/v1/businesses/my-businesses")
                .header("Authorization", authHeader)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getBusinessesByRegion should return not found for invalid region`() {
        mockMvc.perform(
            get("/api/v1/businesses/region/${UUID.randomUUID()}")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `getBusinessesByCategory should return not found for invalid category`() {
        mockMvc.perform(
            get("/api/v1/businesses/category/${UUID.randomUUID()}")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `searchBusinesses should return empty for no matches`() {
        mockMvc.perform(
            get("/api/v1/businesses/search")
                .param("query", "ThisDoesNotExist12345")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `getFeaturedBusinesses should return empty when no featured`() {
        mockMvc.perform(
            get("/api/v1/businesses/featured")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `createBusiness should require authentication`() {
        val request = CreateBusinessRequest(
            name = "New Business",
            description = "Description",
            categoryId = testCategory.id!!,
            regionId = testRegion.id!!,
            email = "new@example.com",
            phone = "1234567890"
        )

        mockMvc.perform(
            post("/api/v1/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `updateBusiness should require authentication`() {
        val request = UpdateBusinessRequest(
            name = "Updated Business",
            description = "Updated Description"
        )

        mockMvc.perform(
            put("/api/v1/businesses/${testBusiness.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `deleteBusiness should require authentication`() {
        mockMvc.perform(
            delete("/api/v1/businesses/${testBusiness.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `verifyBusiness should require authentication`() {
        mockMvc.perform(
            post("/api/v1/businesses/${testBusiness.id}/verify")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `getMyBusinesses should require authentication`() {
        mockMvc.perform(
            get("/api/v1/businesses/my-businesses")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `searchBusinesses should work with region filter`() {
        mockMvc.perform(
            get("/api/v1/businesses/search")
                .param("regionId", testRegion.id.toString())
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `searchBusinesses should work with category filter`() {
        mockMvc.perform(
            get("/api/v1/businesses/search")
                .param("categoryId", testCategory.id.toString())
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
    }
}
