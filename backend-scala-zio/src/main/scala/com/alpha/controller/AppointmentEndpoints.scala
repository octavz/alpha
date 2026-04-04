package com.alpha.controller

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.{Response, Routes}
import com.alpha.service.*
import com.alpha.config.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.security.*
import java.util.UUID
import java.time.LocalDate

object AppointmentEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[AppointmentService & AppConfig, Any]] = List(
    SecureEndpoints.secureEndpoint.get.tag("Appointments").summary("List appointments").in(base / "appointments")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId"))
      .out(jsonBody[List[Appointment]])
      .serverLogic { ctx => params =>
        val (bid, uid) = params
        val result     = (bid, uid) match
          case (Some(b), _) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(b))
          case (_, Some(u)) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByUser(u))
          case _            => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByUser(ctx.userId))
        result.mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Appointments").summary("Get appointment").in(
      base / "appointments" / path[UUID]("id"))
      .out(jsonBody[Appointment])
      .serverLogic { ctx => id =>
        ZIO.serviceWithZIO[AppointmentService](_.getAppointment(id))
          .flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(e => AuthError(e.getMessage, 404))
      },
    SecureEndpoints.secureEndpoint.get.tag("Appointments").summary("Search appointments").in(
      base / "appointments" / "search")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).in(query[Option[String]]("status")).in(
        query[Option[LocalDate]]("date"))
      .out(jsonBody[List[Appointment]])
      .serverLogic { ctx => params =>
        val (b, u, s, d) = params
        ZIO.serviceWithZIO[AppointmentService](_.searchAppointments(b, u, s, d)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Appointments").summary("By business").in(
      base / "appointments" / "business" / path[UUID](
        "businessId"))
      .out(jsonBody[List[Appointment]])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(bid)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Appointments").summary("Availability").in(
      base / "appointments" / "business" / path[UUID](
        "businessId") / "availability")
      .in(query[LocalDate]("date")).in(query[Option[UUID]]("serviceId"))
      .out(jsonBody[List[AvailabilitySlot]])
      .serverLogic { ctx => params =>
        val (bid, dt, sid) = params
        ZIO.serviceWithZIO[AppointmentService](_.getAvailability(bid, dt, sid)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.post.tag("Appointments").summary("Create appointment").in(base / "appointments")
      .in(jsonBody[CreateAppointmentRequest]).out(jsonBody[Appointment])
      .serverLogic { ctx => req =>
        ZIO.serviceWithZIO[AppointmentService](_.createAppointment(req)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.put.tag("Appointments").summary("Update appointment").in(
      base / "appointments" / path[UUID]("id"))
      .in(jsonBody[UpdateAppointmentRequest]).out(jsonBody[Appointment])
      .serverLogic { ctx => tup =>
        val (id, req) = tup
        ZIO.serviceWithZIO[AppointmentService](_.updateAppointment(id, req)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.post.tag("Appointments").summary("Cancel appointment").in(
      base / "appointments" / path[UUID](
        "id") / "cancel")
      .in(jsonBody[CancelAppointmentRequest]).out(jsonBody[Appointment])
      .serverLogic { ctx => tup =>
        val (id, req) = tup
        ZIO.serviceWithZIO[AppointmentService](_.cancelAppointment(id, req)).mapError(e => AuthError(e.getMessage, 400))
      }
  )

  val routes: URIO[AppointmentService & AppConfig, Routes[Any, Response]] = toRoutes(endpoints)
