package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import io.getquill.*

trait BusinessHoursRepository:
  def findById(id: UUID): Task[Option[BusinessHours]]
  def findByBusinessId(businessId: UUID): Task[List[BusinessHours]]
  def findByBusinessIdAndDay(businessId: UUID, dayOfWeek: Int): Task[Option[BusinessHours]]
  def create(hours: BusinessHours): Task[UUID]
  def update(hours: BusinessHours): Task[Int]
  def delete(id: UUID): Task[Int]
  def deleteByBusinessId(businessId: UUID): Task[Int]

object BusinessHoursRepository:
  val layer: ZLayer[PostgresCtx, Nothing, BusinessHoursRepository] =
    ZLayer.fromFunction(new BusinessHoursRepositoryImpl(_))

class BusinessHoursRepositoryImpl(ctx: PostgresCtx) extends BusinessHoursRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[BusinessHours]] =
    ZIO.attempt:
      run(query[BusinessHours].filter(_.id == lift(id))).headOption

  override def findByBusinessId(businessId: UUID): Task[List[BusinessHours]] =
    ZIO.attempt:
      run(query[BusinessHours].filter(_.businessId == lift(businessId))).toList

  override def findByBusinessIdAndDay(businessId: UUID, dayOfWeek: Int): Task[Option[BusinessHours]] =
    ZIO.attempt:
      run(
        query[BusinessHours].filter(h => h.businessId == lift(businessId) && h.dayOfWeek == lift(dayOfWeek))).headOption

  override def create(hours: BusinessHours): Task[UUID] =
    ZIO.attempt:
      run(query[BusinessHours].insertValue(lift(hours)))
      hours.id

  override def update(hours: BusinessHours): Task[Int] =
    ZIO.attempt:
      run(query[BusinessHours]
        .filter(_.id == lift(hours.id))
        .updateValue(lift(hours)))
      1

  override def delete(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[BusinessHours].filter(_.id == lift(id)).delete)
      1

  override def deleteByBusinessId(businessId: UUID): Task[Int] =
    ZIO.attempt:
      run(query[BusinessHours].filter(_.businessId == lift(businessId)).delete)
      0
