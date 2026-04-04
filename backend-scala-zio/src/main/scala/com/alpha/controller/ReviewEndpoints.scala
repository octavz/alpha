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

object ReviewEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[ReviewService, Any]] = List(
    endpoint.get.tag("Reviews").summary("Get review").in(base / "reviews" / path[UUID]("id"))
      .out(jsonBody[Review]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[ReviewService](_.getReview(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("By business").in(base / "reviews" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[ReviewService](_.getReviewsByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("Approved by business").in(base / "reviews" / "business" / path[UUID](
      "businessId") / "approved")
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[ReviewService](_.getApprovedReviewsByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("By user").in(base / "reviews" / "user" / path[UUID]("userId"))
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { uid =>
        ZIO.serviceWithZIO[ReviewService](_.getReviewsByUser(uid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("Average rating").in(base / "reviews" / "business" / path[UUID](
      "businessId") / "average-rating")
      .out(jsonBody[Double]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[ReviewService](_.getAverageRating(bid)).map(_.getOrElse(0.0)).mapError(_.getMessage)
      },
    endpoint.post.tag("Reviews").summary("Create review").in(base / "reviews")
      .in(jsonBody[CreateReviewRequest]).out(jsonBody[Review]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[ReviewService](_.createReview(req)).mapError(_.getMessage)
      },
    endpoint.post.tag("Reviews").summary("Approve review").in(base / "reviews" / path[UUID]("id") / "approve")
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[ReviewService](_.approveReview(id)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Reviews").summary("Delete review").in(base / "reviews" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[ReviewService](_.deleteReview(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[ReviewService, Routes[Any, Response]] = toRoutes(endpoints)
