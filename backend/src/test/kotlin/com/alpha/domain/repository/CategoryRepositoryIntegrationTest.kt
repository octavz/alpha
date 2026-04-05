package com.alpha.domain.repository

import com.alpha.AbstractIntegrationTest
import com.alpha.domain.entity.CategoryEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
class CategoryRepositoryIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    private fun createCategory(
        name: String = "Category ${UUID.randomUUID()}",
        slug: String = "cat-${UUID.randomUUID()}",
        isActive: Boolean = true,
        parent: CategoryEntity? = null,
        sortOrder: Int = 0
    ): CategoryEntity {
        return categoryRepository.save(CategoryEntity().apply {
            this.name = name
            this.slug = slug
            this.isActive = isActive
            this.parent = parent
            this.sortOrder = sortOrder
        })
    }

    @Test
    fun `findBySlug should return category when exists`() {
        val category = createCategory(slug = "unique-slug-${UUID.randomUUID()}")

        val found = categoryRepository.findBySlug(category.slug)

        assertNotNull(found)
        assertEquals(category.slug, found?.slug)
    }

    @Test
    fun `findBySlug should return null when not exists`() {
        val found = categoryRepository.findBySlug("nonexistent")
        assertNull(found)
    }

    @Test
    fun `findByParentId should return child categories`() {
        val parent = createCategory(name = "Parent", slug = "parent-${UUID.randomUUID()}")
        createCategory(name = "Child 1", slug = "child1-${UUID.randomUUID()}", parent = parent)
        createCategory(name = "Child 2", slug = "child2-${UUID.randomUUID()}", parent = parent)

        val result = categoryRepository.findByParentId(parent.id)

        assertEquals(2, result.size)
    }

    @Test
    fun `findByParentId should return empty list when no children`() {
        val parent = createCategory(name = "No Children", slug = "nochildren-${UUID.randomUUID()}")

        val result = categoryRepository.findByParentId(parent.id)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByIsActiveTrue should return only active categories`() {
        val active = createCategory(slug = "active-cat-${UUID.randomUUID()}", isActive = true)
        createCategory(slug = "inactive-cat-${UUID.randomUUID()}", isActive = false)

        val result = categoryRepository.findByIsActiveTrue()

        assertTrue(result.any { it.id == active.id })
        assertFalse(result.any { !it.isActive })
    }

    @Test
    fun `existsBySlug should return true when exists`() {
        val category = createCategory(slug = "exists-cat-${UUID.randomUUID()}")

        assertTrue(categoryRepository.existsBySlug(category.slug))
    }

    @Test
    fun `existsBySlug should return false when not exists`() {
        assertFalse(categoryRepository.existsBySlug("nonexistent-slug"))
    }

    @Test
    fun `findRootCategories should return categories without parent`() {
        createCategory(name = "Root 1", slug = "root1-${UUID.randomUUID()}", sortOrder = 1)
        createCategory(name = "Root 2", slug = "root2-${UUID.randomUUID()}", sortOrder = 2)
        val parent = createCategory(name = "Parent", slug = "parent-root-${UUID.randomUUID()}")
        createCategory(name = "Child", slug = "child-root-${UUID.randomUUID()}", parent = parent)

        val result = categoryRepository.findRootCategories()

        assertFalse(result.any { it.parent != null })
    }

    @Test
    fun `findRootCategories should order by sortOrder`() {
        val first = createCategory(name = "First", slug = "first-${UUID.randomUUID()}", sortOrder = 1)
        val second = createCategory(name = "Second", slug = "second-${UUID.randomUUID()}", sortOrder = 2)

        val result = categoryRepository.findRootCategories()

        val firstIndex = result.indexOfFirst { it.id == first.id }
        val secondIndex = result.indexOfFirst { it.id == second.id }
        assertTrue(firstIndex < secondIndex)
    }

    @Test
    fun `search should find categories by name`() {
        val category = createCategory(name = "Italian Restaurants", slug = "italian-${UUID.randomUUID()}")

        val result = categoryRepository.search("Italian")

        assertEquals(1, result.size)
        assertEquals(category.id, result[0].id)
    }

    @Test
    fun `search should find categories by slug`() {
        val category = createCategory(name = "Some Category", slug = "unique-search-slug")

        val result = categoryRepository.search("unique-search-slug")

        assertEquals(1, result.size)
        assertEquals(category.id, result[0].id)
    }

    @Test
    fun `search should be case insensitive`() {
        val category = createCategory(name = "UPPERCASE Category", slug = "upper-${UUID.randomUUID()}")

        val result = categoryRepository.search("uppercase")

        assertEquals(1, result.size)
    }

    @Test
    fun `search should return empty list when no match`() {
        val result = categoryRepository.search("nonexistent")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByParentIdOrdered should return children ordered by sortOrder`() {
        val parent = createCategory(name = "Parent", slug = "parent-ordered-${UUID.randomUUID()}")
        val first = createCategory(name = "First", slug = "child2-ord-${UUID.randomUUID()}", parent = parent, sortOrder = 1)
        val second = createCategory(name = "Second", slug = "child1-ord-${UUID.randomUUID()}", parent = parent, sortOrder = 2)

        val result = categoryRepository.findByParentIdOrdered(parent.id!!)

        assertEquals(2, result.size)
        assertEquals(first.id, result[0].id)
        assertEquals(second.id, result[1].id)
    }
}
