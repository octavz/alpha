package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

class BusinessHoursController(businessHoursService: BusinessHoursService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "business-hours" / string("id") -> handler(handleGetById),
    Method.GET / "api" / "v1" / "business-hours" / "business" / string("businessId") -> handler(handleByBusiness),
    Method.GET / "api" / "v1" / "business-hours" / "business" / string("businessId") / "day" / string("dayOfWeek") -> handler(handleByBusinessAndDay),
    Method.POST / "api" / "v1" / "business-hours" -> handler(handleCreate),
    Method.PUT / "api" / "v1" / "business-hours" / string("id") -> handler(handleUpdate),
    Method.DELETE / "api" / "v1" / "business-hours" / string("id") -> handler(handleDelete),
    Method.DELETE / "api" / "v1" / "business-hours" / "business" / string("businessId") -> handler(handleDeleteByBusiness)
  )

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      businessHoursService.getHours(uuid).map {
        case Some(hours) => jsonResponse(ApiResponse.success(hours))
        case None => errorResponse("Business hours not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      businessHoursService.getHoursByBusiness(uuid)
        .map(hours => jsonResponse(ApiResponse.successList(hours)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByBusinessAndDay(businessId: String, dayOfWeek: String, req: Request): Task[Response] =
    (for
      uuid <- ZIO.attempt(UUID.fromString(businessId))
      day <- ZIO.attempt(dayOfWeek.toInt)
      hoursOpt <- businessHoursService.getHoursByBusinessAndDay(uuid, day)
    yield hoursOpt match
      case Some(hours) => jsonResponse(ApiResponse.success(hours))
      case None => errorResponse("Business hours not found", Status.NotFound)
    ).catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleCreate(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateBusinessHoursRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      created <- businessHoursService.createHours(req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdate(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateBusinessHoursRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      updated <- businessHoursService.updateHours(uuid, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDelete(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      businessHoursService.deleteHours(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDeleteByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      businessHoursService.deleteAllByBusiness(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object BusinessHoursController:
  val layer: ZLayer[BusinessHoursService, Nothing, BusinessHoursController] =
    ZLayer.fromFunction(new BusinessHoursController(_))
