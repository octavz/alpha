package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID

trait RegionService:
  def getAllRegions: Task[List[Region]]
  def getRegionById(id: UUID): Task[Option[Region]]
  def getRegionByCode(code: String): Task[Option[Region]]
  def searchRegions(query: String): Task[List[Region]]
  def createRegion(req: CreateRegionRequest): Task[Region]
  def updateRegion(id: UUID, req: UpdateRegionRequest): Task[Region]
  def deleteRegion(id: UUID): Task[Unit]

object RegionService:
  val layer: ZLayer[RegionRepository & TimeProvider & UUIDProvider, Nothing, RegionService] =
    ZLayer.fromFunction(new RegionServiceImpl(_, _, _))

class RegionServiceImpl(regionRepo: RegionRepository, timeProvider: TimeProvider, uuidProvider: UUIDProvider)
  extends RegionService:

  override def getAllRegions: Task[List[Region]] =
    regionRepo.findAll

  override def getRegionById(id: UUID): Task[Option[Region]] =
    regionRepo.findById(id)

  override def getRegionByCode(code: String): Task[Option[Region]] =
    regionRepo.findByCode(code)

  override def searchRegions(query: String): Task[List[Region]] =
    regionRepo.search(query)

  override def createRegion(req: CreateRegionRequest): Task[Region] =
    for
      exists <- regionRepo.existsByCode(req.code)
      _      <- ZIO.fail(new Exception("Region with this code already exists")).when(exists)
      region  = Region(
                  id = uuidProvider.randomUUID(),
                  name = req.name,
                  code = req.code,
                  country = req.country,
                  timezone = req.timezone,
                  isActive = true,
                  createdAt = timeProvider.now(),
                  updatedAt = None
                )
      id     <- regionRepo.create(region)
    yield region.copy(id = id)

  override def updateRegion(id: UUID, req: UpdateRegionRequest): Task[Region] =
    for
      regionOpt <- regionRepo.findById(id)
      region    <- ZIO.fromOption(regionOpt).orElseFail(new Exception("Region not found"))
      updated    = region.copy(
                     name = req.name.getOrElse(region.name),
                     code = req.code.getOrElse(region.code),
                     country = req.country.getOrElse(region.country),
                     timezone = req.timezone.getOrElse(region.timezone),
                     isActive = req.isActive.getOrElse(region.isActive),
                     updatedAt = Some(timeProvider.now())
                   )
      _         <- regionRepo.update(updated)
    yield updated

  override def deleteRegion(id: UUID): Task[Unit] =
    regionRepo.delete(id).unit
