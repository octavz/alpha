package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import io.getquill.*

trait ReviewRepository:
  def findById(id: UUID): Task[Option[Review]]
  def findByBusinessId(businessId: UUID): Task[List[Review]]
  def findByUserId(userId: UUID): Task[List[Review]]
  def findByBusinessIdAndApproved(businessId: UUID): Task[List[Review]]
  def findApprovedByBusinessId(businessId: UUID): Task[List[Review]]
  def getAverageRating(businessId: UUID): Task[Option[Double]]
  def create(review: Review): Task[UUID]
  def update(review: Review): Task[Int]
  def approve(id: UUID): Task[Int]
  def delete(id: UUID): Task[Int]

object ReviewRepository:
  val layer: ZLayer[PostgresCtx, Nothing, ReviewRepository] =
    ZLayer.fromFunction(new ReviewRepositoryImpl(_))

class ReviewRepositoryImpl(ctx: PostgresCtx) extends ReviewRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[Review]] =
    ZIO.attempt:
      run(query[Review].filter(_.id == lift(id))).headOption

  override def findByBusinessId(businessId: UUID): Task[List[Review]] =
    ZIO.attempt:
      run(query[Review].filter(_.businessId == lift(businessId))).toList

  override def findByUserId(userId: UUID): Task[List[Review]] =
    ZIO.attempt:
      run(query[Review].filter(_.userId == lift(userId))).toList

  override def findByBusinessIdAndApproved(businessId: UUID): Task[List[Review]] =
    ZIO.attempt:
      run(query[Review].filter(r => r.businessId == lift(businessId) && r.isApproved == lift(true))).toList

  override def findApprovedByBusinessId(businessId: UUID): Task[List[Review]] =
    ZIO.attempt:
      run(query[Review].filter(r => r.businessId == lift(businessId) && r.isApproved == lift(true))).toList

  override def getAverageRating(businessId: UUID): Task[Option[Double]] =
    ZIO.attempt:
      val results = run(query[Review]
        .filter(r => r.businessId == lift(businessId) && r.isApproved == lift(true))
        .map(_.rating))
      if results.isEmpty then None
      else Some(results.map(_.toDouble).sum / results.size)

  override def create(review: Review): Task[UUID] =
    ZIO.attempt:
      run(query[Review].insertValue(lift(review)))
      review.id

  override def update(review: Review): Task[Int] =
    ZIO.attempt:
      run(query[Review]
        .filter(_.id == lift(review.id))
        .updateValue(lift(review)))
      1

  override def approve(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[Review]
        .filter(_.id == lift(id))
        .update(_.isApproved -> lift(true)))
      1

  override def delete(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[Review].filter(_.id == lift(id)).delete)
      1
