package com.alpha.service

import zio.test.*
import zio.*
import com.alpha.domain.model.*
import com.alpha.domain.enums.*
import com.alpha.security.*
import com.alpha.repository.*
import java.time.OffsetDateTime
import java.util.UUID

object CategoryServiceSpec extends ZIOSpecDefault:

  def mockCategoryRepository(categories: List[Category] = Nil) = ZLayer.fromFunction: _ =>
    new CategoryRepository:
      override def findAll: ZIO[Any, Throwable, List[Category]]              = ZIO.succeed(categories)
      override def findById(id: UUID): ZIO[Any, Throwable, Option[Category]] =
        ZIO.succeed(categories.find(_.id == id))
      override def create(category: Category): ZIO[Any, Throwable, UUID]     = ZIO.succeed(category.id)
      override def update(category: Category): ZIO[Any, Throwable, Int]      = ZIO.succeed(1)
      override def delete(id: UUID): ZIO[Any, Throwable, Int]                = ZIO.succeed(1)

  val testCategory = Category(
    id = UUID.randomUUID(),
    name = "Test Category",
    slug = "test-category",
    description = Some("Description"),
    icon = Some("icon"),
    parentId = None,
    sortOrder = 1,
    isActive = true,
    createdAt = OffsetDateTime.now()
  )

  val testLayer = mockCategoryRepository(List(testCategory)) >>> CategoryService.layer

  override def spec = suite("CategoryServiceSpec")(
    test("getAllCategories should return all categories") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.getAllCategories
      yield assertTrue(result.size == 1) &&
        assertTrue(result.head.name == "Test Category")
    },
    test("getCategory should return category by id") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.getCategory(testCategory.id)
      yield assertTrue(result.isDefined) &&
        assertTrue(result.get.name == "Test Category")
    },
    test("getCategory should return None for non-existent id") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.getCategory(UUID.randomUUID())
      yield assertTrue(result.isEmpty)
    },
    test("createCategory should create and return category") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.createCategory(CreateCategoryRequest("New Category"))
      yield assertTrue(result.name == "New Category") &&
        assertTrue(result.slug == "new-category")
    },
    test("updateCategory should update existing category") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.updateCategory(testCategory.id, UpdateCategoryRequest(name = Some("Updated")))
      yield assertTrue(result.isDefined) &&
        assertTrue(result.get.name == "Updated")
    },
    test("updateCategory should return None for non-existent category") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.updateCategory(UUID.randomUUID(), UpdateCategoryRequest(name = Some("Updated")))
      yield assertTrue(result.isEmpty)
    },
    test("deleteCategory should delete category") {
      for
        service <- ZIO.service[CategoryService]
        result  <- service.deleteCategory(testCategory.id)
      yield assertTrue(result)
    }
  ).provide(testLayer)
