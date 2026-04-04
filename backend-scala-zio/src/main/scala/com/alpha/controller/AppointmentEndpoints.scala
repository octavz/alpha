package com.alpha.controller

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.{Response, Routes}
import com.alpha.service.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import java.util.UUID

object AppointmentEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[AppointmentService, Any]] = List(
    endpoint.get.tag("Appointments").summary("List appointments").in(base / "appointments")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).out(
        jsonBody[List[Appointment]]).errorOut(stringBody)
      .zServerLogic {
        case (bid, uid) =>
          val r: ZIO[AppointmentService, Throwable, List[Appointment]] = (bid, uid) match
            case (Some(b), _) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(b))
            case (_, Some(u)) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByUser(u))
            case _            => ZIO.succeed(List.empty[Appointment])
          r.mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Get appointment").in(base / "appointments" / path[UUID]("id"))
      .out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[AppointmentService](_.getAppointment(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Search appointments").in(base / "appointments" / "search")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).in(query[Option[String]]("status")).in(
        query[Option[java.time.LocalDate]]("date"))
      .out(jsonBody[List[Appointment]]).errorOut(stringBody).zServerLogic {
        case (b, u, s, d) =>
          ZIO.serviceWithZIO[AppointmentService](_.searchAppointments(b, u, s, d)).mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("By business").in(base / "appointments" / "business" / path[UUID](
      "businessId"))
      .out(jsonBody[List[Appointment]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Availability").in(base / "appointments" / "business" / path[UUID](
      "businessId") / "availability")
      .in(query[java.time.LocalDate]("date")).in(query[Option[UUID]]("serviceId")).out(
        jsonBody[List[AvailabilitySlot]]).errorOut(stringBody)
      .zServerLogic {
        case (bid, dt, sid) =>
          ZIO.serviceWithZIO[AppointmentService](_.getAvailability(bid, dt, sid)).mapError(_.getMessage)
      },
    endpoint.post.tag("Appointments").summary("Create appointment").in(base / "appointments")
      .in(jsonBody[CreateAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[AppointmentService](_.createAppointment(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Appointments").summary("Update appointment").in(base / "appointments" / path[UUID]("id"))
      .in(jsonBody[UpdateAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[AppointmentService](_.updateAppointment(id, req)).mapError(_.getMessage)
      },
    endpoint.post.tag("Appointments").summary("Cancel appointment").in(base / "appointments" / path[UUID](
      "id") / "cancel")
      .in(jsonBody[CancelAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[AppointmentService](_.cancelAppointment(id, req)).mapError(_.getMessage)
      }
  )

  val routes: URIO[AppointmentService, Routes[Any, Response]] = toRoutes(endpoints)
