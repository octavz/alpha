package com.alpha.service

import com.alpha.domain.entity.CategoryEntity
import com.alpha.domain.repository.CategoryRepository
import com.alpha.service.dto.*
import com.alpha.service.exception.NotFoundException
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CategoryServiceIntegrationTest {

    private val categoryRepository = mockk<CategoryRepository>()
    private val categoryService = CategoryService(categoryRepository)

    private lateinit var testCategory: CategoryEntity

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        testCategory = CategoryEntity().apply {
            id = UUID.randomUUID()
            name = "Test Category"
            slug = "test-category"
            description = "Test Description"
            isActive = true
            sortOrder = 1
            createdAt = java.time.OffsetDateTime.now()
        }
    }

    @Test
    fun `getAllCategories should return root categories`() {
        every { categoryRepository.findRootCategories() } returns listOf(testCategory)

        val result = categoryService.getAllCategories()

        assertEquals(1, result.size)
        assertEquals("Test Category", result[0].name)
    }

    @Test
    fun `getCategory should return category`() {
        every { categoryRepository.findById(testCategory.id!!) } returns Optional.of(testCategory)

        val result = categoryService.getCategory(testCategory.id!!)

        assertEquals("Test Category", result.name)
    }

    @Test
    fun `getCategory should throw NotFoundException`() {
        every { categoryRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            categoryService.getCategory(UUID.randomUUID())
        }
    }

    @Test
    fun `createCategory should create and return category`() {
        val request = CreateCategoryRequest(
            name = "New Category",
            description = "New Description",
            icon = "icon.png",
            sortOrder = 1,
            isActive = true
        )

        every { categoryRepository.existsBySlug(any()) } returns false
        every { categoryRepository.save(any()) } answers {
            val c = firstArg<CategoryEntity>()
            c.apply { 
                id = UUID.randomUUID()
                createdAt = java.time.OffsetDateTime.now()
            }
        }

        val result = categoryService.createCategory(request)

        assertEquals("New Category", result.name)
        assertEquals("new-category", result.slug)
    }

    @Test
    fun `updateCategory should update category`() {
        val request = UpdateCategoryRequest(
            name = "Updated Category",
            description = "Updated Description",
            isActive = false
        )

        every { categoryRepository.findById(testCategory.id!!) } returns Optional.of(testCategory)
        every { categoryRepository.save(testCategory) } returns testCategory

        val result = categoryService.updateCategory(testCategory.id!!, request)

        assertEquals("Updated Category", testCategory.name)
        assertEquals("Updated Description", testCategory.description)
        assertFalse(testCategory.isActive)
    }

    @Test
    fun `updateCategory should only update non-null fields`() {
        val request = UpdateCategoryRequest(name = "Updated Only")

        every { categoryRepository.findById(testCategory.id!!) } returns Optional.of(testCategory)
        every { categoryRepository.save(testCategory) } returns testCategory

        categoryService.updateCategory(testCategory.id!!, request)

        assertEquals("Updated Only", testCategory.name)
        assertEquals("Test Description", testCategory.description)
        assertTrue(testCategory.isActive)
    }

    @Test
    fun `updateCategory should throw NotFoundException`() {
        every { categoryRepository.findById(any()) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            categoryService.updateCategory(UUID.randomUUID(), UpdateCategoryRequest(name = "Updated"))
        }
    }

    @Test
    fun `deleteCategory should delete category`() {
        every { categoryRepository.existsById(testCategory.id!!) } returns true
        every { categoryRepository.deleteById(testCategory.id!!) } just runs

        categoryService.deleteCategory(testCategory.id!!)

        verify { categoryRepository.deleteById(testCategory.id!!) }
    }

    @Test
    fun `deleteCategory should throw NotFoundException`() {
        every { categoryRepository.existsById(any()) } returns false

        assertThrows(NotFoundException::class.java) {
            categoryService.deleteCategory(UUID.randomUUID())
        }
    }
}
