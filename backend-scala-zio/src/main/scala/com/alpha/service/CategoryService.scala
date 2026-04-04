package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID

trait CategoryService:
  def getAllCategories: Task[List[Category]]
  def getCategory(id: UUID): Task[Option[Category]]
  def createCategory(req: CreateCategoryRequest): Task[Category]
  def updateCategory(id: UUID, req: UpdateCategoryRequest): Task[Category]
  def deleteCategory(id: UUID): Task[Unit]

object CategoryService:
  val layer: ZLayer[CategoryRepository & TimeProvider & UUIDProvider, Nothing, CategoryService] = 
    ZLayer.fromFunction(new CategoryServiceImpl(_, _, _))

class CategoryServiceImpl(categoryRepo: CategoryRepository, timeProvider: TimeProvider, uuidProvider: UUIDProvider) extends CategoryService:

  override def getAllCategories: Task[List[Category]] =
    categoryRepo.findAll

  override def getCategory(id: UUID): Task[Option[Category]] =
    categoryRepo.findById(id)

  override def createCategory(req: CreateCategoryRequest): Task[Category] =
    for
      exists <- categoryRepo.existsBySlug(req.slug)
      _ <- ZIO.fail(new Exception("Category with this slug already exists")).when(exists)
      category = Category(
        id = uuidProvider.randomUUID(),
        name = req.name,
        slug = req.slug,
        description = req.description,
        icon = req.icon,
        parentId = req.parentId,
        sortOrder = req.sortOrder,
        isActive = true,
        createdAt = timeProvider.now(),
        updatedAt = None
      )
      id <- categoryRepo.create(category)
    yield category.copy(id = id)

  override def updateCategory(id: UUID, req: UpdateCategoryRequest): Task[Category] =
    for
      categoryOpt <- categoryRepo.findById(id)
      category <- ZIO.fromOption(categoryOpt).orElseFail(new Exception("Category not found"))
      updated = category.copy(
        name = req.name.getOrElse(category.name),
        description = req.description.orElse(category.description),
        icon = req.icon.orElse(category.icon),
        parentId = req.parentId.orElse(category.parentId),
        sortOrder = req.sortOrder.getOrElse(category.sortOrder),
        isActive = req.isActive.getOrElse(category.isActive),
        updatedAt = Some(timeProvider.now())
      )
      _ <- categoryRepo.update(updated)
    yield updated

  override def deleteCategory(id: UUID): Task[Unit] =
    categoryRepo.delete(id).unit
