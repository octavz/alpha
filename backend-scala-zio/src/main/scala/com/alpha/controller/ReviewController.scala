package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

class ReviewController(reviewService: ReviewService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "reviews" -> handler(handleGetAll()),
    Method.GET / "api" / "v1" / "reviews" / string("id") -> handler(handleGetById),
    Method.GET / "api" / "v1" / "reviews" / "business" / string("businessId") -> handler(handleByBusiness),
    Method.GET / "api" / "v1" / "reviews" / "business" / string("businessId") / "approved" -> handler(handleApprovedByBusiness),
    Method.GET / "api" / "v1" / "reviews" / "user" / string("userId") -> handler(handleByUser),
    Method.GET / "api" / "v1" / "reviews" / "business" / string("businessId") / "average-rating" -> handler(handleAverageRating),
    Method.POST / "api" / "v1" / "reviews" -> handler(handleCreate),
    Method.POST / "api" / "v1" / "reviews" / string("id") / "approve" -> handler(handleApprove),
    Method.DELETE / "api" / "v1" / "reviews" / string("id") -> handler(handleDelete)
  )

  private def handleGetAll(): Task[Response] =
    ZIO.succeed(errorResponse("Use /reviews/business/{id} or /reviews/user/{id} instead", Status.BadRequest))

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      reviewService.getReview(uuid).map {
        case Some(review) => jsonResponse(ApiResponse.success(review))
        case None => errorResponse("Review not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      reviewService.getReviewsByBusiness(uuid)
        .map(reviews => jsonResponse(ApiResponse.successList(reviews)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleApprovedByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      reviewService.getApprovedReviewsByBusiness(uuid)
        .map(reviews => jsonResponse(ApiResponse.successList(reviews)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByUser(userId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(userId)).flatMap { uuid =>
      reviewService.getReviewsByUser(uuid)
        .map(reviews => jsonResponse(ApiResponse.successList(reviews)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleAverageRating(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      reviewService.getAverageRating(uuid).map {
        case Some(rating) => jsonResponse(ApiResponse.success(rating))
        case None => jsonResponse(ApiResponse.success(0.0))
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleCreate(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateReviewRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      created <- reviewService.createReview(req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleApprove(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      reviewService.approveReview(uuid).as(jsonResponse(ApiResponse.success("Review approved")))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDelete(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      reviewService.deleteReview(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object ReviewController:
  val layer: ZLayer[ReviewService, Nothing, ReviewController] =
    ZLayer.fromFunction(new ReviewController(_))
