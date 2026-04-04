package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID

trait ReviewService:
  def getReview(id: UUID): Task[Option[Review]]
  def getReviewsByBusiness(businessId: UUID): Task[List[Review]]
  def getApprovedReviewsByBusiness(businessId: UUID): Task[List[Review]]
  def getReviewsByUser(userId: UUID): Task[List[Review]]
  def getAverageRating(businessId: UUID): Task[Option[Double]]
  def createReview(req: CreateReviewRequest): Task[Review]
  def approveReview(id: UUID): Task[Unit]
  def deleteReview(id: UUID): Task[Unit]

object ReviewService:
  val layer: ZLayer[ReviewRepository & TimeProvider & UUIDProvider, Nothing, ReviewService] = 
    ZLayer.fromFunction(new ReviewServiceImpl(_, _, _))

class ReviewServiceImpl(reviewRepo: ReviewRepository, timeProvider: TimeProvider, uuidProvider: UUIDProvider) extends ReviewService:

  override def getReview(id: UUID): Task[Option[Review]] =
    reviewRepo.findById(id)

  override def getReviewsByBusiness(businessId: UUID): Task[List[Review]] =
    reviewRepo.findByBusinessId(businessId)

  override def getApprovedReviewsByBusiness(businessId: UUID): Task[List[Review]] =
    reviewRepo.findApprovedByBusinessId(businessId)

  override def getReviewsByUser(userId: UUID): Task[List[Review]] =
    reviewRepo.findByUserId(userId)

  override def getAverageRating(businessId: UUID): Task[Option[Double]] =
    reviewRepo.getAverageRating(businessId)

  override def createReview(req: CreateReviewRequest): Task[Review] =
    val review = Review(
      id = uuidProvider.randomUUID(),
      businessId = req.businessId,
      userId = req.userId,
      appointmentId = req.appointmentId,
      rating = req.rating,
      title = req.title,
      comment = req.comment,
      isApproved = false,
      isFeatured = false,
      createdAt = timeProvider.now(),
      updatedAt = None
    )
    for
      id <- reviewRepo.create(review)
    yield review.copy(id = id)

  override def approveReview(id: UUID): Task[Unit] =
    reviewRepo.approve(id).unit

  override def deleteReview(id: UUID): Task[Unit] =
    reviewRepo.delete(id).unit
