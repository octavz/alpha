package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import io.getquill.*

trait CategoryRepository:
  def findAll: Task[List[Category]]
  def findById(id: UUID): Task[Option[Category]]
  def findBySlug(slug: String): Task[Option[Category]]
  def findByParentId(parentId: UUID): Task[List[Category]]
  def findByIsActiveTrue: Task[List[Category]]
  def existsBySlug(slug: String): Task[Boolean]
  def findRootCategories: Task[List[Category]]
  def search(query: String): Task[List[Category]]
  def create(category: Category): Task[UUID]
  def update(category: Category): Task[Int]
  def delete(id: UUID): Task[Int]

object CategoryRepository:
  val layer: ZLayer[PostgresCtx, Nothing, CategoryRepository] =
    ZLayer.fromFunction(new CategoryRepositoryImpl(_))

class CategoryRepositoryImpl(ctx: PostgresCtx) extends CategoryRepository:

  import ctx.*

  override def findAll: Task[List[Category]] =
    ZIO.attempt:
      run(query[Category]).toList

  override def findById(id: UUID): Task[Option[Category]] =
    ZIO.attempt:
      run(query[Category].filter(_.id == lift(id))).headOption

  override def findBySlug(slug: String): Task[Option[Category]] =
    ZIO.attempt:
      run(query[Category].filter(_.slug == lift(slug))).headOption

  override def findByParentId(parentId: UUID): Task[List[Category]] =
    ZIO.attempt:
      run(query[Category].filter(_.parentId.exists(_ == lift(parentId)))).toList

  override def findByIsActiveTrue: Task[List[Category]] =
    ZIO.attempt:
      run(query[Category].filter(_.isActive == lift(true))).toList

  override def existsBySlug(slug: String): Task[Boolean] =
    ZIO.attempt:
      run(query[Category].filter(_.slug == lift(slug)).nonEmpty)

  override def findRootCategories: Task[List[Category]] =
    ZIO.attempt:
      run(query[Category].filter(_.parentId.isEmpty)).toList

  override def search(queryStr: String): Task[List[Category]] =
    ZIO.attempt:
      val pattern = "%" + queryStr + "%"
      run(query[Category]
        .filter(c => c.name.like(lift(pattern)) || c.description.exists(_.like(lift(pattern))))).toList

  override def create(category: Category): Task[UUID] =
    ZIO.attempt:
      run(query[Category].insertValue(lift(category)))
      category.id

  override def update(category: Category): Task[Int] =
    ZIO.attempt:
      run(query[Category]
        .filter(_.id == lift(category.id))
        .updateValue(lift(category)))
      1

  override def delete(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[Category].filter(_.id == lift(id)).delete)
      1
