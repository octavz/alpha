package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID

trait ServiceService:
  def getService(id: UUID): Task[Option[Service]]
  def getServicesByBusiness(businessId: UUID): Task[List[Service]]
  def getActiveServicesByBusiness(businessId: UUID): Task[List[Service]]
  def createService(businessId: UUID, req: CreateServiceRequest): Task[Service]
  def updateService(id: UUID, req: UpdateServiceRequest): Task[Service]
  def deleteService(id: UUID): Task[Unit]

object ServiceService:
  val layer: ZLayer[ServiceRepository & TimeProvider & UUIDProvider, Nothing, ServiceService] =
    ZLayer.fromFunction(new ServiceServiceImpl(_, _, _))

class ServiceServiceImpl(serviceRepo: ServiceRepository, timeProvider: TimeProvider, uuidProvider: UUIDProvider)
  extends ServiceService:

  override def getService(id: UUID): Task[Option[Service]] =
    serviceRepo.findById(id)

  override def getServicesByBusiness(businessId: UUID): Task[List[Service]] =
    serviceRepo.findByBusinessId(businessId)

  override def getActiveServicesByBusiness(businessId: UUID): Task[List[Service]] =
    serviceRepo.findByBusinessIdAndActive(businessId)

  override def createService(businessId: UUID, req: CreateServiceRequest): Task[Service] =
    val service = Service(
      id = uuidProvider.randomUUID(),
      businessId = businessId,
      name = req.name,
      description = req.description,
      durationMinutes = req.durationMinutes,
      price = req.price,
      isActive = req.isActive,
      sortOrder = req.sortOrder,
      createdAt = timeProvider.now(),
      updatedAt = None
    )
    for
      id <- serviceRepo.create(service)
    yield service.copy(id = id)

  override def updateService(id: UUID, req: UpdateServiceRequest): Task[Service] =
    for
      serviceOpt <- serviceRepo.findById(id)
      service    <- ZIO.fromOption(serviceOpt).orElseFail(new Exception("Service not found"))
      updated     = service.copy(
                      name = req.name.getOrElse(service.name),
                      description = req.description.orElse(service.description),
                      durationMinutes = req.durationMinutes.getOrElse(service.durationMinutes),
                      price = req.price.orElse(service.price),
                      isActive = req.isActive.getOrElse(service.isActive),
                      sortOrder = req.sortOrder.getOrElse(service.sortOrder),
                      updatedAt = Some(timeProvider.now())
                    )
      _          <- serviceRepo.update(updated)
    yield updated

  override def deleteService(id: UUID): Task[Unit] =
    serviceRepo.delete(id).unit
