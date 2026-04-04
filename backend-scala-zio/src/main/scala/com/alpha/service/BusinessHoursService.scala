package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID

trait BusinessHoursService:
  def getHours(id: UUID): Task[Option[BusinessHours]]
  def getHoursByBusiness(businessId: UUID): Task[List[BusinessHours]]
  def getHoursByBusinessAndDay(businessId: UUID, dayOfWeek: Int): Task[Option[BusinessHours]]
  def createHours(req: CreateBusinessHoursRequest): Task[BusinessHours]
  def updateHours(id: UUID, req: UpdateBusinessHoursRequest): Task[BusinessHours]
  def deleteHours(id: UUID): Task[Unit]
  def deleteAllByBusiness(businessId: UUID): Task[Unit]

object BusinessHoursService:
  val layer: ZLayer[BusinessHoursRepository & TimeProvider & UUIDProvider, Nothing, BusinessHoursService] = 
    ZLayer.fromFunction(new BusinessHoursServiceImpl(_, _, _))

class BusinessHoursServiceImpl(hoursRepo: BusinessHoursRepository, timeProvider: TimeProvider, uuidProvider: UUIDProvider) extends BusinessHoursService:

  override def getHours(id: UUID): Task[Option[BusinessHours]] =
    hoursRepo.findById(id)

  override def getHoursByBusiness(businessId: UUID): Task[List[BusinessHours]] =
    hoursRepo.findByBusinessId(businessId)

  override def getHoursByBusinessAndDay(businessId: UUID, dayOfWeek: Int): Task[Option[BusinessHours]] =
    hoursRepo.findByBusinessIdAndDay(businessId, dayOfWeek)

  override def createHours(req: CreateBusinessHoursRequest): Task[BusinessHours] =
    val hours = BusinessHours(
      id = uuidProvider.randomUUID(),
      businessId = req.businessId,
      dayOfWeek = req.dayOfWeek,
      openTime = req.openTime,
      closeTime = req.closeTime,
      isClosed = req.isClosed
    )
    for
      id <- hoursRepo.create(hours)
    yield hours.copy(id = id)

  override def updateHours(id: UUID, req: UpdateBusinessHoursRequest): Task[BusinessHours] =
    for
      hoursOpt <- hoursRepo.findById(id)
      hours <- ZIO.fromOption(hoursOpt).orElseFail(new Exception("Business hours not found"))
      updated = hours.copy(
        openTime = req.openTime.orElse(hours.openTime),
        closeTime = req.closeTime.orElse(hours.closeTime),
        isClosed = req.isClosed.getOrElse(hours.isClosed)
      )
      _ <- hoursRepo.update(updated)
    yield updated

  override def deleteHours(id: UUID): Task[Unit] =
    hoursRepo.delete(id).unit

  override def deleteAllByBusiness(businessId: UUID): Task[Unit] =
    hoursRepo.deleteByBusinessId(businessId).unit
