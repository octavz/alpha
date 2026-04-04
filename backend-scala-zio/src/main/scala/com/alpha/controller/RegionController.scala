package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

class RegionController(regionService: RegionService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "regions" -> handler(handleGetAll()),
    Method.GET / "api" / "v1" / "regions" / string("id") -> handler(handleGetById),
    Method.GET / "api" / "v1" / "regions" / "code" / string("code") -> handler(handleGetByCode),
    Method.GET / "api" / "v1" / "regions" / "search" -> handler(handleSearch),
    Method.POST / "api" / "v1" / "regions" -> handler(handleCreate),
    Method.PUT / "api" / "v1" / "regions" / string("id") -> handler(handleUpdate),
    Method.DELETE / "api" / "v1" / "regions" / string("id") -> handler(handleDelete)
  )

  private def handleGetAll(): Task[Response] =
    regionService.getAllRegions
      .map(regions => jsonResponse(ApiResponse.successList(regions)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      regionService.getRegionById(uuid).map {
        case Some(region) => jsonResponse(ApiResponse.success(region))
        case None => errorResponse("Region not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleGetByCode(code: String, req: Request): Task[Response] =
    regionService.getRegionByCode(code).map {
      case Some(region) => jsonResponse(ApiResponse.success(region))
      case None => errorResponse("Region not found", Status.NotFound)
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleSearch(request: Request): Task[Response] =
    request.queryParam("q") match
      case Some(query) =>
        regionService.searchRegions(query)
          .map(regions => jsonResponse(ApiResponse.successList(regions)))
          .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))
      case None =>
        ZIO.succeed(errorResponse("Missing query parameter 'q'", Status.BadRequest))

  private def handleCreate(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateRegionRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      created <- regionService.createRegion(req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdate(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateRegionRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      updated <- regionService.updateRegion(uuid, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDelete(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      regionService.deleteRegion(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object RegionController:
  val layer: ZLayer[RegionService, Nothing, RegionController] =
    ZLayer.fromFunction(new RegionController(_))
