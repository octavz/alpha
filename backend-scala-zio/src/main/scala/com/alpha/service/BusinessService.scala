package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID

trait BusinessService:
  def getBusiness(id: UUID): Task[Option[Business]]
  def getBusinessBySlug(slug: String): Task[Option[Business]]
  def getBusinessesByUser(userId: UUID): Task[List[Business]]
  def getBusinessesByCategory(categoryId: UUID): Task[List[Business]]
  def getBusinessesByRegion(regionId: UUID): Task[List[Business]]
  def getFeaturedBusinesses(limit: Int): Task[List[Business]]
  def searchBusinesses(query: String): Task[List[Business]]
  def createBusiness(userId: UUID, req: CreateBusinessRequest): Task[Business]
  def updateBusiness(id: UUID, req: UpdateBusinessRequest): Task[Business]
  def deleteBusiness(id: UUID): Task[Unit]
  def verifyBusiness(id: UUID): Task[Business]

object BusinessService:
  val layer: ZLayer[BusinessRepository & TimeProvider & UUIDProvider, Nothing, BusinessService] = 
    ZLayer.fromFunction(new BusinessServiceImpl(_, _, _))

class BusinessServiceImpl(businessRepo: BusinessRepository, timeProvider: TimeProvider, uuidProvider: UUIDProvider) extends BusinessService:

  override def getBusiness(id: UUID): Task[Option[Business]] =
    businessRepo.findById(id)

  override def getBusinessBySlug(slug: String): Task[Option[Business]] =
    businessRepo.findBySlug(slug)

  override def getBusinessesByUser(userId: UUID): Task[List[Business]] =
    businessRepo.findByUserId(userId)

  override def getBusinessesByCategory(categoryId: UUID): Task[List[Business]] =
    businessRepo.findByCategoryId(categoryId)

  override def getBusinessesByRegion(regionId: UUID): Task[List[Business]] =
    businessRepo.findByRegionId(regionId)

  override def getFeaturedBusinesses(limit: Int): Task[List[Business]] =
    businessRepo.findFeatured(limit)

  override def searchBusinesses(query: String): Task[List[Business]] =
    businessRepo.searchActiveVerified(query)

  override def createBusiness(userId: UUID, req: CreateBusinessRequest): Task[Business] =
    for
      exists <- businessRepo.existsBySlug(req.slug)
      _ <- ZIO.fail(new Exception("Business with this slug already exists")).when(exists)
      business = Business(
        id = uuidProvider.randomUUID(),
        userId = userId,
        name = req.name,
        slug = req.slug,
        description = req.description,
        email = req.email,
        phone = req.phone,
        website = req.website,
        addressLine1 = req.addressLine1,
        addressLine2 = req.addressLine2,
        city = req.city,
        state = req.state,
        zipCode = req.zipCode,
        country = req.country,
        latitude = req.latitude,
        longitude = req.longitude,
        categoryId = req.categoryId,
        regionId = req.regionId,
        verificationStatus = "PENDING",
        isVerified = false,
        isActive = true,
        isFeatured = false,
        logoUrl = None,
        coverImageUrl = None,
        servicePointsCount = 0,
        createdAt = timeProvider.now(),
        updatedAt = None
      )
      id <- businessRepo.create(business)
    yield business.copy(id = id)

  override def updateBusiness(id: UUID, req: UpdateBusinessRequest): Task[Business] =
    for
      businessOpt <- businessRepo.findById(id)
      business <- ZIO.fromOption(businessOpt).orElseFail(new Exception("Business not found"))
      updated = business.copy(
        name = req.name.getOrElse(business.name),
        description = req.description.orElse(business.description),
        email = req.email.orElse(business.email),
        phone = req.phone.orElse(business.phone),
        website = req.website.orElse(business.website),
        addressLine1 = req.addressLine1.orElse(business.addressLine1),
        addressLine2 = req.addressLine2.orElse(business.addressLine2),
        city = req.city.orElse(business.city),
        state = req.state.orElse(business.state),
        zipCode = req.zipCode.orElse(business.zipCode),
        country = req.country.orElse(business.country),
        latitude = req.latitude.orElse(business.latitude),
        longitude = req.longitude.orElse(business.longitude),
        categoryId = req.categoryId.getOrElse(business.categoryId),
        regionId = req.regionId.getOrElse(business.regionId),
        logoUrl = req.logoUrl.orElse(business.logoUrl),
        coverImageUrl = req.coverImageUrl.orElse(business.coverImageUrl),
        updatedAt = Some(timeProvider.now())
      )
      _ <- businessRepo.update(updated)
    yield updated

  override def deleteBusiness(id: UUID): Task[Unit] =
    businessRepo.delete(id).unit

  override def verifyBusiness(id: UUID): Task[Business] =
    for
      businessOpt <- businessRepo.findById(id)
      business <- ZIO.fromOption(businessOpt).orElseFail(new Exception("Business not found"))
      updated = business.copy(
        verificationStatus = "APPROVED",
        isVerified = true,
        updatedAt = Some(timeProvider.now())
      )
      _ <- businessRepo.update(updated)
    yield updated
