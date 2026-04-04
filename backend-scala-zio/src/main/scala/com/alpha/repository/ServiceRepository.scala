package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import io.getquill.*

trait ServiceRepository:
  def findById(id: UUID): Task[Option[Service]]
  def findByBusinessId(businessId: UUID): Task[List[Service]]
  def findByBusinessIdAndActive(businessId: UUID): Task[List[Service]]
  def create(service: Service): Task[UUID]
  def update(service: Service): Task[Int]
  def delete(id: UUID): Task[Int]

object ServiceRepository:
  val layer: ZLayer[PostgresCtx, Nothing, ServiceRepository] = 
    ZLayer.fromFunction(new ServiceRepositoryImpl(_))

class ServiceRepositoryImpl(ctx: PostgresCtx) extends ServiceRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[Service]] = 
    ZIO.attempt:
      run(query[Service].filter(_.id == lift(id))).headOption

  override def findByBusinessId(businessId: UUID): Task[List[Service]] = 
    ZIO.attempt:
      run(query[Service].filter(_.businessId == lift(businessId))).toList

  override def findByBusinessIdAndActive(businessId: UUID): Task[List[Service]] = 
    ZIO.attempt:
      run(query[Service].filter(s => s.businessId == lift(businessId) && s.isActive == lift(true))).toList

  override def create(service: Service): Task[UUID] = 
    ZIO.attempt:
      run(query[Service].insertValue(lift(service)))
      service.id

  override def update(service: Service): Task[Int] = 
    ZIO.attempt:
      run(query[Service]
        .filter(_.id == lift(service.id))
        .updateValue(lift(service)))
      1

  override def delete(id: UUID): Task[Int] = 
    ZIO.attempt:
      run(query[Service].filter(_.id == lift(id)).delete)
      1
