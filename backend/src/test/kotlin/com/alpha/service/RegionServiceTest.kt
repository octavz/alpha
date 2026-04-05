package com.alpha.service

import com.alpha.domain.entity.RegionEntity
import com.alpha.domain.repository.RegionRepository
import com.alpha.service.dto.*
import com.alpha.service.exception.ConflictException
import com.alpha.service.exception.NotFoundException
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RegionServiceIntegrationTest {

    private val regionRepository = mockk<RegionRepository>()
    private val regionService = RegionService(regionRepository)

    private lateinit var testRegion: RegionEntity

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        testRegion = RegionEntity().apply {
            id = UUID.randomUUID()
            name = "Test Region"
            code = "TR"
            country = "Test Country"
            timezone = "UTC"
            isActive = true
            createdAt = java.time.OffsetDateTime.now()
        }
    }

    @Test
    fun `getAllRegions should return active regions`() {
        every { regionRepository.findByIsActiveTrue() } returns listOf(testRegion)

        val result = regionService.getAllRegions()

        assertEquals(1, result.size)
        assertEquals("Test Region", result[0].name)
    }

    @Test
    fun `getRegion should return region`() {
        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)

        val result = regionService.getRegion(testRegion.id!!)

        assertEquals("Test Region", result.name)
    }

    @Test
    fun `getRegion should throw NotFoundException`() {
        every { regionRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            regionService.getRegion(UUID.randomUUID())
        }
    }

    @Test
    fun `getRegionByCode should return region`() {
        every { regionRepository.findByCode("TR") } returns testRegion

        val result = regionService.getRegionByCode("TR")

        assertEquals("Test Region", result.name)
    }

    @Test
    fun `getRegionByCode should throw NotFoundException`() {
        every { regionRepository.findByCode("XX") } returns null

        assertThrows(NotFoundException::class.java) {
            regionService.getRegionByCode("XX")
        }
    }

    @Test
    fun `createRegion should create region`() {
        val request = CreateRegionRequest(
            name = "New Region",
            code = "NR",
            country = "New Country",
            timezone = "UTC",
            isActive = true
        )

        every { regionRepository.existsByCode("NR") } returns false
        every { regionRepository.save(any()) } answers {
            val r = firstArg<RegionEntity>()
            r.apply { 
                id = UUID.randomUUID()
                createdAt = java.time.OffsetDateTime.now()
            }
        }

        val result = regionService.createRegion(request)

        assertEquals("New Region", result.name)
        assertEquals("NR", result.code)
    }

    @Test
    fun `createRegion should throw ConflictException for duplicate code`() {
        val request = CreateRegionRequest(
            name = "Duplicate",
            code = "TR",
            country = "Duplicate",
            timezone = "UTC",
            isActive = true
        )

        every { regionRepository.existsByCode("TR") } returns true

        assertThrows(ConflictException::class.java) {
            regionService.createRegion(request)
        }
    }

    @Test
    fun `updateRegion should update region`() {
        val request = UpdateRegionRequest(
            name = "Updated Region",
            country = "Updated Country",
            isActive = false
        )

        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)
        every { regionRepository.save(testRegion) } returns testRegion

        val result = regionService.updateRegion(testRegion.id!!, request)

        assertEquals("Updated Region", testRegion.name)
        assertEquals("Updated Country", testRegion.country)
        assertFalse(testRegion.isActive)
    }

    @Test
    fun `updateRegion should check code conflict when changing code`() {
        val request = UpdateRegionRequest(code = "NEW")

        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)
        every { regionRepository.existsByCode("NEW") } returns true

        assertThrows(ConflictException::class.java) {
            regionService.updateRegion(testRegion.id!!, request)
        }
    }

    @Test
    fun `updateRegion should allow same code`() {
        val request = UpdateRegionRequest(code = "TR")

        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)
        every { regionRepository.save(testRegion) } returns testRegion

        val result = regionService.updateRegion(testRegion.id!!, request)

        assertEquals("TR", result.code)
    }

    @Test
    fun `updateRegion should throw NotFoundException`() {
        every { regionRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            regionService.updateRegion(UUID.randomUUID(), UpdateRegionRequest(name = "Updated"))
        }
    }

    @Test
    fun `deleteRegion should mark as inactive`() {
        every { regionRepository.findById(testRegion.id!!) } returns Optional.of(testRegion)
        every { regionRepository.save(testRegion) } returns testRegion

        regionService.deleteRegion(testRegion.id!!)

        assertFalse(testRegion.isActive)
    }

    @Test
    fun `deleteRegion should throw NotFoundException`() {
        every { regionRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            regionService.deleteRegion(UUID.randomUUID())
        }
    }

    @Test
    fun `searchRegions should return matching regions`() {
        every { regionRepository.search("Test") } returns listOf(testRegion)

        val result = regionService.searchRegions("Test")

        assertEquals(1, result.size)
        assertEquals("Test Region", result[0].name)
    }
}
