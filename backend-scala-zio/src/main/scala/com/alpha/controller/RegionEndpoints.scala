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

object RegionEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[RegionService, Any]] = List(
    endpoint.get.tag("Regions").summary("List regions").in(base / "regions")
      .out(jsonBody[List[Region]]).errorOut(stringBody).zServerLogic { _ =>
        ZIO.serviceWithZIO[RegionService](_.getAllRegions).mapError(_.getMessage)
      },
    endpoint.get.tag("Regions").summary("Get region").in(base / "regions" / path[UUID]("id"))
      .out(jsonBody[Region]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[RegionService](_.getRegionById(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Regions").summary("Get region by code").in(base / "regions" / "code" / path[String]("code"))
      .out(jsonBody[Region]).errorOut(stringBody).zServerLogic { code =>
        ZIO.serviceWithZIO[RegionService](_.getRegionByCode(code)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Regions").summary("Search regions").in(base / "regions" / "search")
      .in(query[String]("q")).out(jsonBody[List[Region]]).errorOut(stringBody).zServerLogic { q =>
        ZIO.serviceWithZIO[RegionService](_.searchRegions(q)).mapError(_.getMessage)
      },
    endpoint.post.tag("Regions").summary("Create region").in(base / "regions")
      .in(jsonBody[CreateRegionRequest]).out(jsonBody[Region]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[RegionService](_.createRegion(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Regions").summary("Update region").in(base / "regions" / path[UUID]("id"))
      .in(jsonBody[UpdateRegionRequest]).out(jsonBody[Region]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[RegionService](_.updateRegion(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Regions").summary("Delete region").in(base / "regions" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[RegionService](_.deleteRegion(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[RegionService, Routes[Any, Response]] = toRoutes(endpoints)
