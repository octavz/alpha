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

object ServiceEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[com.alpha.service.ServiceService & AppConfig, Any]] = List(
    SecureEndpoints.secureEndpoint.get.tag("Services").summary("Get service").in(base / "services" / path[UUID]("id"))
      .out(jsonBody[com.alpha.domain.model.Service])
      .serverLogic { ctx => id =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getService(id))
          .flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(e => AuthError(e.getMessage, 404))
      },
    SecureEndpoints.secureEndpoint.get.tag("Services").summary("By business").in(
      base / "services" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[com.alpha.domain.model.Service]])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getServicesByBusiness(bid)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Services").summary("Active by business").in(
      base / "services" / "business" / path[UUID](
        "businessId") / "active")
      .out(jsonBody[List[com.alpha.domain.model.Service]])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getActiveServicesByBusiness(bid)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.post.tag("Services").summary("Create service").in(
      base / "services" / "business" / path[UUID](
        "businessId"))
      .in(jsonBody[CreateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service])
      .serverLogic { ctx => tup =>
        val (bid, req) = tup
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.createService(bid, req)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.put.tag("Services").summary("Update service").in(base / "services" / path[UUID](
      "id"))
      .in(jsonBody[UpdateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service])
      .serverLogic { ctx => tup =>
        val (id, req) = tup
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.updateService(id, req)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.delete.tag("Services").summary("Delete service").in(base / "services" / path[UUID](
      "id"))
      .out(statusCode(sttp.model.StatusCode(204)))
      .serverLogic { (ctx: AuthContext) => (id: UUID) =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.deleteService(id)).mapError(e =>
          AuthError(e.getMessage, 400))
      }
  )

  val routes: URIO[com.alpha.service.ServiceService & AppConfig, Routes[Any, Response]] = toRoutes(endpoints)
