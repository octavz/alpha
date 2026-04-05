package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.RegionEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
class RegionRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var regionRepository: RegionRepository

    private fun createRegion(
        name: String = "Region ${UUID.randomUUID()}",
        code: String = "REG-${UUID.randomUUID()}",
        country: String = "Test Country",
        isActive: Boolean = true
    ): RegionEntity {
        return regionRepository.save(RegionEntity().apply {
            this.name = name
            this.code = code
            this.country = country
            this.timezone = "UTC"
            this.isActive = isActive
        })
    }

    @Test
    fun `findByCode should return region when exists`() {
        val region = createRegion(code = "UNIQUE-CODE-${UUID.randomUUID()}")

        val found = regionRepository.findByCode(region.code)

        assertNotNull(found)
        assertEquals(region.code, found?.code)
    }

    @Test
    fun `findByCode should return null when not exists`() {
        val found = regionRepository.findByCode("NONEXISTENT")
        assertNull(found)
    }

    @Test
    fun `findByCountry should return regions in country`() {
        createRegion(name = "Region 1", code = "R1-${UUID.randomUUID()}", country = "USA")
        createRegion(name = "Region 2", code = "R2-${UUID.randomUUID()}", country = "USA")
        createRegion(name = "Region 3", code = "R3-${UUID.randomUUID()}", country = "Canada")

        val result = regionRepository.findByCountry("USA")

        assertEquals(2, result.size)
        assertTrue(result.all { it.country == "USA" })
    }

    @Test
    fun `findByIsActiveTrue should return only active regions`() {
        val active = createRegion(code = "ACTIVE-${UUID.randomUUID()}", isActive = true)
        createRegion(code = "INACTIVE-${UUID.randomUUID()}", isActive = false)

        val result = regionRepository.findByIsActiveTrue()

        assertTrue(result.any { it.id == active.id })
        assertFalse(result.any { !it.isActive })
    }

    @Test
    fun `existsByCode should return true when exists`() {
        val region = createRegion(code = "EXISTS-${UUID.randomUUID()}")

        assertTrue(regionRepository.existsByCode(region.code))
    }

    @Test
    fun `existsByCode should return false when not exists`() {
        assertFalse(regionRepository.existsByCode("NONEXISTENT-CODE"))
    }

    @Test
    fun `search should find regions by name`() {
        val region = createRegion(name = "New York Area", code = "NYA-${UUID.randomUUID()}")

        val result = regionRepository.search("New York")

        assertEquals(1, result.size)
        assertEquals(region.id, result[0].id)
    }

    @Test
    fun `search should find regions by code`() {
        val region = createRegion(name = "Some Region", code = "SEARCH-CODE-123")

        val result = regionRepository.search("SEARCH-CODE-123")

        assertEquals(1, result.size)
        assertEquals(region.id, result[0].id)
    }

    @Test
    fun `search should find regions by country`() {
        val region = createRegion(name = "Some Region", code = "SC-${UUID.randomUUID()}", country = "Germany")

        val result = regionRepository.search("Germany")

        assertEquals(1, result.size)
        assertEquals(region.id, result[0].id)
    }

    @Test
    fun `search should be case insensitive`() {
        val region = createRegion(name = "UPPERCASE REGION", code = "UC-${UUID.randomUUID()}")

        val result = regionRepository.search("uppercase")

        assertEquals(1, result.size)
    }

    @Test
    fun `search should return empty list when no match`() {
        val result = regionRepository.search("nonexistent")
        assertTrue(result.isEmpty())
    }
}
