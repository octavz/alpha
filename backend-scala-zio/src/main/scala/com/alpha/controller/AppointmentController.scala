package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID
import java.time.LocalDate

class AppointmentController(appointmentService: AppointmentService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "appointments" -> handler(handleGetAll),
    Method.GET / "api" / "v1" / "appointments" / string("id") -> handler(handleGetById),
    Method.GET / "api" / "v1" / "appointments" / "search" -> handler(handleSearch),
    Method.GET / "api" / "v1" / "appointments" / "business" / string("businessId") -> handler(handleByBusiness),
    Method.GET / "api" / "v1" / "appointments" / "business" / string("businessId") / "availability" -> handler(handleAvailability),
    Method.POST / "api" / "v1" / "appointments" -> handler(handleCreate),
    Method.PUT / "api" / "v1" / "appointments" / string("id") -> handler(handleUpdate),
    Method.POST / "api" / "v1" / "appointments" / string("id") / "cancel" -> handler(handleCancel)
  )

  private def handleGetAll(request: Request): Task[Response] =
    val businessId = request.queryParam("businessId").map(UUID.fromString)
    val userId = request.queryParam("userId").map(UUID.fromString)

    (businessId, userId) match
      case (Some(bId), _) =>
        appointmentService.getAppointmentsByBusiness(bId)
          .map(appointments => jsonResponse(ApiResponse.successList(appointments)))
      case (_, Some(uId)) =>
        appointmentService.getAppointmentsByUser(uId)
          .map(appointments => jsonResponse(ApiResponse.successList(appointments)))
      case _ =>
        ZIO.succeed(errorResponse("Missing businessId or userId parameter", Status.BadRequest))

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      appointmentService.getAppointment(uuid).map {
        case Some(appointment) => jsonResponse(ApiResponse.success(appointment))
        case None => errorResponse("Appointment not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleSearch(request: Request): Task[Response] =
    val businessId = request.queryParam("businessId").map(UUID.fromString)
    val userId = request.queryParam("userId").map(UUID.fromString)
    val status = request.queryParam("status")
    val date = request.queryParam("date").flatMap(d => scala.util.Try(LocalDate.parse(d)).toOption)

    appointmentService.searchAppointments(businessId, userId, status, date)
      .map(appointments => jsonResponse(ApiResponse.successList(appointments)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleByBusiness(businessId: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(businessId)).flatMap { uuid =>
      appointmentService.getAppointmentsByBusiness(uuid)
        .map(appointments => jsonResponse(ApiResponse.successList(appointments)))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleAvailability(businessId: String, request: Request): Task[Response] =
    (for
      uuid <- ZIO.attempt(UUID.fromString(businessId))
      date <- ZIO.fromOption(request.queryParam("date").flatMap(d => scala.util.Try(LocalDate.parse(d)).toOption))
        .orElseFail(new Exception("Missing required query parameter 'date'"))
      serviceId = request.queryParam("serviceId").map(UUID.fromString)
      slots <- appointmentService.getAvailability(uuid, date, serviceId)
    yield jsonResponse(ApiResponse.successList(slots)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleCreate(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateAppointmentRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      created <- appointmentService.createAppointment(req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdate(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateAppointmentRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      updated <- appointmentService.updateAppointment(uuid, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleCancel(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- if body.isEmpty then ZIO.succeed(CancelAppointmentRequest(None))
             else ZIO.fromEither(JsonDecoder[CancelAppointmentRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      cancelled <- appointmentService.cancelAppointment(uuid, req)
    yield jsonResponse(ApiResponse.success(cancelled)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object AppointmentController:
  val layer: ZLayer[AppointmentService, Nothing, AppointmentController] =
    ZLayer.fromFunction(new AppointmentController(_))
