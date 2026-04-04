package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import io.getquill.*

trait BusinessRepository:
  def findById(id: UUID): Task[Option[Business]]
  def findByUserId(userId: UUID): Task[List[Business]]
  def findBySlug(slug: String): Task[Option[Business]]
  def findByCategoryId(categoryId: UUID): Task[List[Business]]
  def findByRegionId(regionId: UUID): Task[List[Business]]
  def findFeatured(limit: Int): Task[List[Business]]
  def findByIsActiveTrue: Task[List[Business]]
  def findByIsVerifiedTrue: Task[List[Business]]
  def findByVerificationStatus(status: String): Task[List[Business]]
  def existsBySlug(slug: String): Task[Boolean]
  def searchActiveVerified(searchQuery: String): Task[List[Business]]
  def findActiveVerifiedByRegion(regionId: UUID): Task[List[Business]]
  def findActiveVerifiedByCategory(categoryId: UUID): Task[List[Business]]
  def create(business: Business): Task[UUID]
  def update(business: Business): Task[Int]
  def delete(id: UUID): Task[Int]

object BusinessRepository:
  val layer: ZLayer[PostgresCtx, Nothing, BusinessRepository] = 
    ZLayer.fromFunction(new BusinessRepositoryImpl(_))

class BusinessRepositoryImpl(ctx: PostgresCtx) extends BusinessRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.id == lift(id))).headOption

  override def findByUserId(userId: UUID): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.userId == lift(userId))).toList

  override def findBySlug(slug: String): Task[Option[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.slug == lift(slug))).headOption

  override def findByCategoryId(categoryId: UUID): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.categoryId == lift(categoryId))).toList

  override def findByRegionId(regionId: UUID): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.regionId == lift(regionId))).toList

  override def findFeatured(limit: Int): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.isFeatured == lift(true)).take(lift(limit))).toList

  override def findByIsActiveTrue: Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.isActive == lift(true))).toList

  override def findByIsVerifiedTrue: Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.isVerified == lift(true))).toList

  override def findByVerificationStatus(status: String): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business].filter(_.verificationStatus == lift(status))).toList

  override def existsBySlug(slug: String): Task[Boolean] = 
    ZIO.attempt:
      run(query[Business].filter(_.slug == lift(slug)).nonEmpty)

  override def searchActiveVerified(searchQuery: String): Task[List[Business]] = 
    ZIO.attempt:
      val pattern = "%" + searchQuery + "%"
      run(query[Business]
        .filter(b => b.isActive == lift(true) && b.isVerified == lift(true))
        .filter(b => b.name.like(lift(pattern)) || b.description.exists(_.like(lift(pattern))))
      ).toList

  override def findActiveVerifiedByRegion(regionId: UUID): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business]
        .filter(b => b.isActive == lift(true) && b.isVerified == lift(true) && b.regionId == lift(regionId))
      ).toList

  override def findActiveVerifiedByCategory(categoryId: UUID): Task[List[Business]] = 
    ZIO.attempt:
      run(query[Business]
        .filter(b => b.isActive == lift(true) && b.isVerified == lift(true) && b.categoryId == lift(categoryId))
      ).toList

  override def create(business: Business): Task[UUID] = 
    ZIO.attempt:
      run(query[Business].insertValue(lift(business)))
      business.id

  override def update(business: Business): Task[Int] = 
    ZIO.attempt:
      run(query[Business]
        .filter(_.id == lift(business.id))
        .updateValue(lift(business)))
      1

  override def delete(id: UUID): Task[Int] = 
    ZIO.attempt:
      run(query[Business].filter(_.id == lift(id)).delete)
      1
