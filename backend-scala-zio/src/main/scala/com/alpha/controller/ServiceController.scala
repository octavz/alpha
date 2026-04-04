package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

class ServiceController(serviceService: ServiceService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "services" / string("id") -> handler(handleGetById),
    Method.GET / "api" / "v1" / "services" / "business" / string("businessId") -> handler(handleByBusiness),
    Method.GET / "api" / "v1" / "services" / "business" / string("businessId") / "active" -> handler(handleActiveByBusiness),
    Method.POST / "api" / "v1" / "services" / "business" / string("businessId") -> handler(handleCreate),
    Method.PUT / "api" / "v1" / "services" / string("id") -> handler(handleUpdate),
    Method.DELETE / "api" / "v1" / "services" / string("id") -> handler(handleDelete)
  )

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      serviceService.getService(uuid).map {
        case Some(service) => jsonResponse(ApiResponse.success(service))
        case None => errorResponse("Service not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      serviceService.getServicesByBusiness(uuid)
        .map(services => jsonResponse(ApiResponse.successList(services)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleActiveByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      serviceService.getActiveServicesByBusiness(uuid)
        .map(services => jsonResponse(ApiResponse.successList(services)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleCreate(businessId: String, request: Request): Task[Response] =
    (for
      uuid <- ZIO.attempt(UUID.fromString(businessId))
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateServiceRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      created <- serviceService.createService(uuid, req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdate(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateServiceRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      updated <- serviceService.updateService(uuid, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDelete(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      serviceService.deleteService(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object ServiceController:
  val layer: ZLayer[ServiceService, Nothing, ServiceController] =
    ZLayer.fromFunction(new ServiceController(_))
