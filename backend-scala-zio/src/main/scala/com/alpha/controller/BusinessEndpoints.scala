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

object BusinessEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[BusinessService, Any]] = List(
    endpoint.get.tag("Businesses").summary("List businesses").in(base / "businesses")
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { _ =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByUser(UUID.randomUUID())).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Get business").in(base / "businesses" / path[UUID]("id"))
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessService](_.getBusiness(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Get business by slug").in(base / "businesses" / "slug" / path[String](
      "slug"))
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { slug =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessBySlug(slug)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Search businesses").in(base / "businesses" / "search")
      .in(query[String]("q")).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { q =>
        ZIO.serviceWithZIO[BusinessService](_.searchBusinesses(q)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("My businesses").in(base / "businesses" / "my-businesses")
      .in(header[UUID]("X-User-Id")).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { uid =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByUser(uid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("By region").in(base / "businesses" / "region" / path[UUID]("regionId"))
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { rid =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByRegion(rid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("By category").in(base / "businesses" / "category" / path[UUID](
      "categoryId"))
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { cid =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByCategory(cid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Featured").in(base / "businesses" / "featured")
      .in(query[Option[Int]]("limit").default(Some(20))).out(jsonBody[List[Business]]).errorOut(
        stringBody).zServerLogic { lim =>
        ZIO.serviceWithZIO[BusinessService](_.getFeaturedBusinesses(lim.getOrElse(20))).mapError(_.getMessage)
      },
    endpoint.post.tag("Businesses").summary("Create business").in(base / "businesses")
      .in(header[UUID]("X-User-Id")).in(jsonBody[CreateBusinessRequest]).out(jsonBody[Business]).errorOut(
        stringBody).zServerLogic {
        case (uid, req) => ZIO.serviceWithZIO[BusinessService](_.createBusiness(uid, req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Businesses").summary("Update business").in(base / "businesses" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessRequest]).out(jsonBody[Business]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[BusinessService](_.updateBusiness(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Businesses").summary("Delete business").in(base / "businesses" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessService](_.deleteBusiness(id)).mapError(_.getMessage)
      },
    endpoint.post.tag("Businesses").summary("Verify business").in(base / "businesses" / path[UUID]("id") / "verify")
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessService](_.verifyBusiness(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[BusinessService, Routes[Any, Response]] = toRoutes(endpoints)
