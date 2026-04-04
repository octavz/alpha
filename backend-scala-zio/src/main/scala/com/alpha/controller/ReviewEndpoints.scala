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

object ReviewEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[ReviewService & AppConfig, Any]] = List(
    SecureEndpoints.secureEndpoint.get.tag("Reviews").summary("Get review").in(base / "reviews" / path[UUID]("id"))
      .out(jsonBody[Review])
      .serverLogic { ctx => id =>
        ZIO.serviceWithZIO[ReviewService](_.getReview(id))
          .flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(e => AuthError(e.getMessage, 404))
      },
    SecureEndpoints.secureEndpoint.get.tag("Reviews").summary("By business").in(
      base / "reviews" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[Review]])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[ReviewService](_.getReviewsByBusiness(bid)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Reviews").summary("Approved by business").in(
      base / "reviews" / "business" / path[UUID](
        "businessId") / "approved")
      .out(jsonBody[List[Review]])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[ReviewService](_.getApprovedReviewsByBusiness(bid)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Reviews").summary("By user").in(base / "reviews" / "user" / path[UUID](
      "userId"))
      .out(jsonBody[List[Review]])
      .serverLogic { ctx => uid =>
        ZIO.serviceWithZIO[ReviewService](_.getReviewsByUser(uid)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.get.tag("Reviews").summary("Average rating").in(
      base / "reviews" / "business" / path[UUID](
        "businessId") / "average-rating")
      .out(jsonBody[Double])
      .serverLogic { ctx => bid =>
        ZIO.serviceWithZIO[ReviewService](_.getAverageRating(bid)).map(_.getOrElse(0.0)).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.post.tag("Reviews").summary("Create review").in(base / "reviews")
      .in(jsonBody[CreateReviewRequest]).out(jsonBody[Review])
      .serverLogic { ctx => req =>
        ZIO.serviceWithZIO[ReviewService](_.createReview(req.copy(userId = ctx.userId))).mapError(e =>
          AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.post.tag("Reviews").summary("Approve review").in(base / "reviews" / path[UUID](
      "id") / "approve")
      .out(statusCode(sttp.model.StatusCode(204)))
      .serverLogic { (ctx: AuthContext) => (id: UUID) =>
        SecureEndpoints.isAdmin(ctx) *>
          ZIO.serviceWithZIO[ReviewService](_.approveReview(id)).mapError(e => AuthError(e.getMessage, 400))
      },
    SecureEndpoints.secureEndpoint.delete.tag("Reviews").summary("Delete review").in(base / "reviews" / path[UUID](
      "id"))
      .out(statusCode(sttp.model.StatusCode(204)))
      .serverLogic { (ctx: AuthContext) => (id: UUID) =>
        SecureEndpoints.isAdmin(ctx) *>
          ZIO.serviceWithZIO[ReviewService](_.deleteReview(id)).mapError(e => AuthError(e.getMessage, 400))
      }
  )

  val routes: URIO[ReviewService & AppConfig, Routes[Any, Response]] = toRoutes(endpoints)
