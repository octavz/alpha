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

object ServiceEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[com.alpha.service.ServiceService, Any]] = List(
    endpoint.get.tag("Services").summary("Get service").in(base / "services" / path[UUID]("id"))
      .out(jsonBody[com.alpha.domain.model.Service]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getService(id)).flatMap(ZIO.fromOption(_).orElseFail(
          new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Services").summary("By business").in(base / "services" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[com.alpha.domain.model.Service]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getServicesByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Services").summary("Active by business").in(base / "services" / "business" / path[UUID](
      "businessId") / "active")
      .out(jsonBody[List[com.alpha.domain.model.Service]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getActiveServicesByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.post.tag("Services").summary("Create service").in(base / "services" / "business" / path[UUID](
      "businessId"))
      .in(jsonBody[CreateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service]).errorOut(
        stringBody).zServerLogic {
        case (bid, req) =>
          ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.createService(bid, req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Services").summary("Update service").in(base / "services" / path[UUID]("id"))
      .in(jsonBody[UpdateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service]).errorOut(
        stringBody).zServerLogic {
        case (id, req) =>
          ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.updateService(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Services").summary("Delete service").in(base / "services" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.deleteService(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[com.alpha.service.ServiceService, Routes[Any, Response]] = toRoutes(endpoints)
