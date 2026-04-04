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

object BusinessHoursEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[BusinessHoursService & AppConfig, Any]] = List(
    SecureEndpoints.secureEndpoint.get.tag("Business Hours").summary("Get hours").in(
      base / "business-hours" / path[UUID]("id"))
      .out(jsonBody[BusinessHours])
      .serverLogic { ctx => id =>
        ZIO.serviceWithZIO[BusinessHoursService](_.getHours(id))
          .flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(e => AuthError(e.getMessage, 404))
      },
    SecureEndpoints.secureEndpoint.get.tag("Business Hours").summary("By business").in(
      base / "business-hours" / "business" / path[UUID](
        "businessId"))
      .out(jsonBody[List[BusinessHours]])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusiness(bid)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Business Hours").summary("By business and day").in(
      base / "business-hours" / "business" / path[UUID]("businessId") / "day" / path[Int]("dayOfWeek"))
      .out(jsonBody[BusinessHours])
      .serverLogic { ctx => params =>
        val (bid, dow) = params
        ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusinessAndDay(bid, dow))
          .flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(e => AuthError(e.getMessage, 404))
      },
    SecureEndpoints.secureEndpoint.post.tag("Business Hours").summary("Create hours").in(base / "business-hours")
      .in(jsonBody[CreateBusinessHoursRequest]).out(jsonBody[BusinessHours])
      .serverLogic { ctx => req =>
        ZIO.serviceWithZIO[BusinessHoursService](_.createHours(req)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.put.tag("Business Hours").summary("Update hours").in(
      base / "business-hours" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessHoursRequest]).out(jsonBody[BusinessHours])
      .serverLogic { ctx => tup =>
        val (id, req) = tup
        ZIO.serviceWithZIO[BusinessHoursService](_.updateHours(id, req)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.delete.tag("Business Hours").summary("Delete hours").in(
      base / "business-hours" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204)))
      .serverLogic { (ctx: AuthContext) => (id: UUID) =>
        ZIO.serviceWithZIO[BusinessHoursService](_.deleteHours(id)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.delete.tag("Business Hours").summary("Delete all by business").in(
      base / "business-hours" / "business" / path[UUID]("businessId"))
      .out(statusCode(sttp.model.StatusCode(204)))
      .serverLogic { (ctx: AuthContext) => (bid: UUID) =>
        ZIO.serviceWithZIO[BusinessHoursService](_.deleteAllByBusiness(bid)).mapError(e => AuthError(e.getMessage, 400))
      }
  )

  val routes: URIO[BusinessHoursService & AppConfig, Routes[Any, Response]] = toRoutes(endpoints)
