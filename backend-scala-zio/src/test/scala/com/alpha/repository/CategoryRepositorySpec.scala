package com.alpha.repository

import zio.*
import zio.test.*
import zio.test.TestAspect.*
import com.alpha.testutil.PostgresContainer
import com.alpha.domain.model.*
import java.time.OffsetDateTime
import java.util.UUID

object CategoryRepositorySpec extends ZIOSpecDefault:

  override def spec = suite("CategoryRepositorySpec")(
    test("findAll should return all active categories") {
      for
        postgres <- ZIO.service[Postgres]
        repo      = CategoryRepositoryImpl(postgres)
        result   <- repo.findAll
      yield assertTrue(result.nonEmpty)
    },
    test("findById should return category when exists") {
      for
        postgres   <- ZIO.service[Postgres]
        repo        = CategoryRepositoryImpl(postgres)
        categories <- repo.findAll
      yield
        assertTrue(categories.nonEmpty)
        assertTrue(categories.head.id != null)
    },
    test("create should insert and return id") {
      for
        postgres <- ZIO.service[Postgres]
        repo      = CategoryRepositoryImpl(postgres)
        id        = UUID.randomUUID()
        now       = OffsetDateTime.now()
        category  = Category(
                      id = id,
                      name = "Test Category",
                      slug = "test-category",
                      description = Some("Test Description"),
                      icon = Some("icon"),
                      parentId = None,
                      sortOrder = 100,
                      isActive = true,
                      createdAt = now
                    )
        result   <- repo.create(category)
      yield assertTrue(result == id)
    },
    test("update should modify existing category") {
      for
        postgres <- ZIO.service[Postgres]
        repo      = CategoryRepositoryImpl(postgres)
        id        = UUID.randomUUID()
        now       = OffsetDateTime.now()
        category  = Category(
                      id = id,
                      name = "Original",
                      slug = "original",
                      description = None,
                      icon = None,
                      parentId = None,
                      sortOrder = 1,
                      isActive = true,
                      createdAt = now
                    )
        _        <- repo.create(category)
        updated   = category.copy(name = "Updated", sortOrder = 2)
        _        <- repo.update(updated)
        result   <- repo.findById(id)
      yield assertTrue(result.isDefined) &&
        assertTrue(result.get.name == "Updated") &&
        assertTrue(result.get.sortOrder == 2)
    },
    test("delete should remove category") {
      for
        postgres <- ZIO.service[Postgres]
        repo      = CategoryRepositoryImpl(postgres)
        id        = UUID.randomUUID()
        now       = OffsetDateTime.now()
        category  = Category(
                      id = id,
                      name = "To Delete",
                      slug = "to-delete",
                      description = None,
                      icon = None,
                      parentId = None,
                      sortOrder = 1,
                      isActive = true,
                      createdAt = now
                    )
        _        <- repo.create(category)
        _        <- repo.delete(id)
        result   <- repo.findById(id)
      yield assertTrue(result.isEmpty)
    }
  ).provideCustomLayerShared(PostgresContainer.layer) @@ sequential
