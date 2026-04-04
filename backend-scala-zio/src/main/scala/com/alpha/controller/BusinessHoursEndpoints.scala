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

object BusinessHoursEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[BusinessHoursService, Any]] = List(
    endpoint.get.tag("Business Hours").summary("Get hours").in(base / "business-hours" / path[UUID]("id"))
      .out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessHoursService](_.getHours(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Business Hours").summary("By business").in(base / "business-hours" / "business" / path[UUID](
      "businessId"))
      .out(jsonBody[List[BusinessHours]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Business Hours").summary("By business and day").in(
      base / "business-hours" / "business" / path[UUID]("businessId") / "day" / path[Int]("dayOfWeek"))
      .out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic {
        case (bid, dow) => ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusinessAndDay(bid, dow)).flatMap(
            ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.post.tag("Business Hours").summary("Create hours").in(base / "business-hours")
      .in(jsonBody[CreateBusinessHoursRequest]).out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[BusinessHoursService](_.createHours(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Business Hours").summary("Update hours").in(base / "business-hours" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessHoursRequest]).out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[BusinessHoursService](_.updateHours(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Business Hours").summary("Delete hours").in(base / "business-hours" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessHoursService](_.deleteHours(id)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Business Hours").summary("Delete all by business").in(
      base / "business-hours" / "business" / path[UUID]("businessId"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[BusinessHoursService](_.deleteAllByBusiness(bid)).mapError(_.getMessage)
      }
  )

  val routes: URIO[BusinessHoursService, Routes[Any, Response]] = toRoutes(endpoints)
