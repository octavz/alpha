package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

class BusinessController(businessService: BusinessService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "businesses" -> handler(handleGetAll),
    Method.GET / "api" / "v1" / "businesses" / string("id") -> handler(handleGetById),
    Method.GET / "api" / "v1" / "businesses" / "slug" / string("slug") -> handler(handleGetBySlug),
    Method.GET / "api" / "v1" / "businesses" / "search" -> handler(handleSearch),
    Method.GET / "api" / "v1" / "businesses" / "my-businesses" -> handler(handleMyBusinesses),
    Method.GET / "api" / "v1" / "businesses" / "region" / string("regionId") -> handler(handleByRegion),
    Method.GET / "api" / "v1" / "businesses" / "category" / string("categoryId") -> handler(handleByCategory),
    Method.GET / "api" / "v1" / "businesses" / "featured" -> handler(handleFeatured),
    Method.POST / "api" / "v1" / "businesses" -> handler(handleCreate),
    Method.PUT / "api" / "v1" / "businesses" / string("id") -> handler(handleUpdate),
    Method.DELETE / "api" / "v1" / "businesses" / string("id") -> handler(handleDelete),
    Method.POST / "api" / "v1" / "businesses" / string("id") / "verify" -> handler(handleVerify)
  )

  private def handleGetAll(request: Request): Task[Response] =
    businessService.getBusinessesByUser(UUID.randomUUID())
      .map(businesses => jsonResponse(ApiResponse.successList(businesses)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      businessService.getBusiness(uuid).map {
        case Some(business) => jsonResponse(ApiResponse.success(business))
        case None => errorResponse("Business not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleGetBySlug(slug: String, req: Request): Task[Response] =
    businessService.getBusinessBySlug(slug).map {
      case Some(business) => jsonResponse(ApiResponse.success(business))
      case None => errorResponse("Business not found", Status.NotFound)
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleSearch(request: Request): Task[Response] =
    request.queryParam("q") match
      case Some(query) =>
        businessService.searchBusinesses(query)
          .map(businesses => jsonResponse(ApiResponse.successList(businesses)))
          .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))
      case None =>
        ZIO.succeed(errorResponse("Missing query parameter 'q'", Status.BadRequest))

  private def handleMyBusinesses(request: Request): Task[Response] =
    (for
      userId <- ZIO.fromOption(request.headers.get("X-User-Id").map(h => UUID.fromString(h.toString))).orElseFail(new Exception("Missing X-User-Id header"))
      businesses <- businessService.getBusinessesByUser(userId)
    yield jsonResponse(ApiResponse.successList(businesses)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByRegion(regionId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(regionId)).flatMap { uuid =>
      businessService.getBusinessesByRegion(uuid)
        .map(businesses => jsonResponse(ApiResponse.successList(businesses)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByCategory(categoryId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(categoryId)).flatMap { uuid =>
      businessService.getBusinessesByCategory(uuid)
        .map(businesses => jsonResponse(ApiResponse.successList(businesses)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleFeatured(request: Request): Task[Response] =
    val limit = request.queryParam("limit").flatMap(_.toIntOption).getOrElse(20)
    businessService.getFeaturedBusinesses(limit)
      .map(businesses => jsonResponse(ApiResponse.successList(businesses)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleCreate(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateBusinessRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      userId <- ZIO.fromOption(request.headers.get("X-User-Id").map(h => UUID.fromString(h.toString))).orElseFail(new Exception("Missing X-User-Id header"))
      created <- businessService.createBusiness(userId, req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdate(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateBusinessRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      updated <- businessService.updateBusiness(uuid, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDelete(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      businessService.deleteBusiness(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleVerify(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      businessService.verifyBusiness(uuid).map(b => jsonResponse(ApiResponse.success(b)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object BusinessController:
  val layer: ZLayer[BusinessService, Nothing, BusinessController] =
    ZLayer.fromFunction(new BusinessController(_))
