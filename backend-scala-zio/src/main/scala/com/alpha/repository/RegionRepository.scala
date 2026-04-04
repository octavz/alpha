package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import io.getquill.*

trait RegionRepository:
  def findAll: Task[List[Region]]
  def findById(id: UUID): Task[Option[Region]]
  def findByCode(code: String): Task[Option[Region]]
  def findByCountry(country: String): Task[List[Region]]
  def findByIsActiveTrue: Task[List[Region]]
  def existsByCode(code: String): Task[Boolean]
  def search(query: String): Task[List[Region]]
  def create(region: Region): Task[UUID]
  def update(region: Region): Task[Int]
  def delete(id: UUID): Task[Int]

object RegionRepository:
  val layer: ZLayer[PostgresCtx, Nothing, RegionRepository] = 
    ZLayer.fromFunction(new RegionRepositoryImpl(_))

class RegionRepositoryImpl(ctx: PostgresCtx) extends RegionRepository:

  import ctx.*

  override def findAll: Task[List[Region]] = 
    ZIO.attempt:
      run(query[Region]).toList

  override def findById(id: UUID): Task[Option[Region]] = 
    ZIO.attempt:
      run(query[Region].filter(_.id == lift(id))).headOption

  override def findByCode(code: String): Task[Option[Region]] = 
    ZIO.attempt:
      run(query[Region].filter(_.code == lift(code))).headOption

  override def findByCountry(country: String): Task[List[Region]] = 
    ZIO.attempt:
      run(query[Region].filter(_.country == lift(country))).toList

  override def findByIsActiveTrue: Task[List[Region]] = 
    ZIO.attempt:
      run(query[Region].filter(_.isActive == lift(true))).toList

  override def existsByCode(code: String): Task[Boolean] = 
    ZIO.attempt:
      run(query[Region].filter(_.code == lift(code)).nonEmpty)

  override def search(queryStr: String): Task[List[Region]] = 
    ZIO.attempt:
      val pattern = "%" + queryStr + "%"
      run(query[Region]
        .filter(r => r.name.like(lift(pattern)) || r.code.like(lift(pattern)))
      ).toList

  override def create(region: Region): Task[UUID] = 
    ZIO.attempt:
      run(query[Region].insertValue(lift(region)))
      region.id

  override def update(region: Region): Task[Int] = 
    ZIO.attempt:
      run(query[Region]
        .filter(_.id == lift(region.id))
        .updateValue(lift(region)))
      1

  override def delete(id: UUID): Task[Int] = 
    ZIO.attempt:
      run(query[Region].filter(_.id == lift(id)).delete)
      1
