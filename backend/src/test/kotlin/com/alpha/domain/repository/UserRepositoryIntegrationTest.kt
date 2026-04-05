package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.RegionEntity
import com.alpha.domain.entity.UserEntity
import com.alpha.domain.enums.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
class UserRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var regionRepository: RegionRepository

    private fun createRegion(): RegionEntity {
        return regionRepository.save(RegionEntity().apply {
            name = "Test Region"
            code = "TR-${UUID.randomUUID()}"
            country = "Test Country"
            timezone = "UTC"
            isActive = true
        })
    }

    @Test
    fun `findByEmail should return user when exists`() {
        val r = createRegion()
        val user = UserEntity().apply {
            email = "test@example.com"
            name = "Test User"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.save(user)

        val found = userRepository.findByEmail("test@example.com")

        assertNotNull(found)
        assertEquals("test@example.com", found?.email)
        assertEquals("Test User", found?.name)
    }

    @Test
    fun `findByEmail should return null when not exists`() {
        val found = userRepository.findByEmail("nonexistent@example.com")
        assertNull(found)
    }

    @Test
    fun `findByGoogleId should return user when exists`() {
        val r = createRegion()
        val user = UserEntity().apply {
            email = "google@example.com"
            googleId = "google-123"
            name = "Google User"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.save(user)

        val found = userRepository.findByGoogleId("google-123")

        assertNotNull(found)
        assertEquals("google-123", found?.googleId)
    }

    @Test
    fun `findByGoogleId should return null when not exists`() {
        val found = userRepository.findByGoogleId("nonexistent")
        assertNull(found)
    }

    @Test
    fun `existsByEmail should return true when exists`() {
        val r = createRegion()
        val user = UserEntity().apply {
            email = "exists@example.com"
            name = "Test User"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.save(user)

        assertTrue(userRepository.existsByEmail("exists@example.com"))
    }

    @Test
    fun `existsByEmail should return false when not exists`() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"))
    }

    @Test
    fun `findByEmailVerifiedTrue should return only verified users`() {
        val r = createRegion()
        val verified = UserEntity().apply {
            email = "verified@example.com"
            name = "Verified User"
            role = UserRole.CUSTOMER
            emailVerified = true
            this.region = r
        }
        val unverified = UserEntity().apply {
            email = "unverified@example.com"
            name = "Unverified User"
            role = UserRole.CUSTOMER
            emailVerified = false
            this.region = r
        }
        userRepository.saveAll(listOf(verified, unverified))

        val result = userRepository.findByEmailVerifiedTrue()

        assertEquals(1, result.size)
        assertEquals("verified@example.com", result[0].email)
    }

    @Test
    fun `findByRole should return users with specific role`() {
        val r = createRegion()
        val admin = UserEntity().apply {
            email = "admin@example.com"
            name = "Admin User"
            role = UserRole.ADMIN
            this.region = r
        }
        val customer = UserEntity().apply {
            email = "customer@example.com"
            name = "Customer User"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.saveAll(listOf(admin, customer))

        val admins = userRepository.findByRole(UserRole.ADMIN)

        assertEquals(1, admins.size)
        assertEquals("admin@example.com", admins[0].email)
    }

    @Test
    fun `findByRegionId should return users in specific region`() {
        val r1 = createRegion()
        val r2 = regionRepository.save(RegionEntity().apply {
            name = "Other Region"
            code = "OR-${UUID.randomUUID()}"
            country = "Other Country"
            timezone = "UTC"
            isActive = true
        })

        val user1 = UserEntity().apply {
            email = "user1@example.com"
            name = "User 1"
            role = UserRole.CUSTOMER
            this.region = r1
        }
        val user2 = UserEntity().apply {
            email = "user2@example.com"
            name = "User 2"
            role = UserRole.CUSTOMER
            this.region = r2
        }
        userRepository.saveAll(listOf(user1, user2))

        val result = userRepository.findByRegionId(r1.id!!)

        assertEquals(1, result.size)
        assertEquals("user1@example.com", result[0].email)
    }

    @Test
    fun `findByIsBannedFalse should return only non-banned users`() {
        val r = createRegion()
        val active = UserEntity().apply {
            email = "active@example.com"
            name = "Active User"
            role = UserRole.CUSTOMER
            isBanned = false
            this.region = r
        }
        val banned = UserEntity().apply {
            email = "banned@example.com"
            name = "Banned User"
            role = UserRole.CUSTOMER
            isBanned = true
            this.region = r
        }
        userRepository.saveAll(listOf(active, banned))

        val result = userRepository.findByIsBannedFalse()

        assertEquals(1, result.size)
        assertEquals("active@example.com", result[0].email)
    }

    @Test
    fun `searchByEmailOrName should find users by email`() {
        val r = createRegion()
        val user = UserEntity().apply {
            email = "john.doe@example.com"
            name = "John Doe"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.save(user)

        val result = userRepository.searchByEmailOrName("john.doe")

        assertEquals(1, result.size)
        assertEquals("john.doe@example.com", result[0].email)
    }

    @Test
    fun `searchByEmailOrName should find users by name`() {
        val r = createRegion()
        val user = UserEntity().apply {
            email = "jane@example.com"
            name = "Jane Smith"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.save(user)

        val result = userRepository.searchByEmailOrName("Jane")

        assertEquals(1, result.size)
        assertEquals("Jane Smith", result[0].name)
    }

    @Test
    fun `searchByEmailOrName should be case insensitive`() {
        val r = createRegion()
        val user = UserEntity().apply {
            email = "CASE@example.com"
            name = "Case Test"
            role = UserRole.CUSTOMER
            this.region = r
        }
        userRepository.save(user)

        val result = userRepository.searchByEmailOrName("case")

        assertEquals(1, result.size)
    }

    @Test
    fun `searchByEmailOrName should return empty list when no match`() {
        val result = userRepository.searchByEmailOrName("nonexistent")
        assertTrue(result.isEmpty())
    }
}
