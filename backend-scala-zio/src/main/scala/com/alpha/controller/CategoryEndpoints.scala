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

object CategoryEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[CategoryService, Any]] = List(
    endpoint.get.tag("Categories").summary("List categories").in(base / "categories")
      .out(jsonBody[List[Category]]).errorOut(stringBody).zServerLogic { _ =>
        ZIO.serviceWithZIO[CategoryService](_.getAllCategories).mapError(_.getMessage)
      },
    endpoint.get.tag("Categories").summary("Get category").in(base / "categories" / path[UUID]("id"))
      .out(jsonBody[Category]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[CategoryService](_.getCategory(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.post.tag("Categories").summary("Create category").in(base / "categories")
      .in(jsonBody[CreateCategoryRequest]).out(jsonBody[Category]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[CategoryService](_.createCategory(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Categories").summary("Update category").in(base / "categories" / path[UUID]("id"))
      .in(jsonBody[UpdateCategoryRequest]).out(jsonBody[Category]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[CategoryService](_.updateCategory(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Categories").summary("Delete category").in(base / "categories" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[CategoryService](_.deleteCategory(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[CategoryService, Routes[Any, Response]] = toRoutes(endpoints)
