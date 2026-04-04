package com.alpha.controller

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.{Response, Routes}
import com.alpha.service.*
import com.alpha.config.*
import com.alpha.provider.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import com.alpha.security.*
import pdi.jwt.*
import java.util.UUID

case class AuthResponse(userId: UUID, email: String, name: Option[String], role: String, accessToken: String, refreshToken: String, sessionId: UUID)

object AuthResponse:
  import zio.json.*
  given JsonEncoder[AuthResponse] = DeriveJsonEncoder.gen
  given JsonDecoder[AuthResponse] = DeriveJsonDecoder.gen

object AuthEndpoints:

  private def encodeToken(userId: UUID, email: String, role: String, secret: String, expirySeconds: Long, issuer: String, now: java.time.Instant, tokenType: String): String =
    val content = s"""{"email":"$email","role":"$role","type":"$tokenType"}"""
    Jwt.encode(
      JwtClaim(subject = Some(userId.toString), issuer = Some(issuer), expiration = Some(now.plusSeconds(expirySeconds).getEpochSecond), issuedAt = Some(now.getEpochSecond), content = content),
      secret, JwtAlgorithm.HS256
    )

  private def buildAuthResponse(user: User, session: UserSession, jwt: JwtSettings, tp: TimeProvider): AuthResponse =
    val now = tp.now().toInstant
    AuthResponse(user.id, user.email, user.name, user.role,
      encodeToken(user.id, user.email, user.role, jwt.accessSecret, 900, jwt.issuer, now, "access"),
      encodeToken(user.id, user.email, user.role, jwt.refreshSecret, 604800, jwt.issuer, now, "refresh"),
      session.id)

  private val base = "api" / "v1"
  private val interp = ZioHttpInterpreter()

  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  // Public endpoints (no auth required)
  val publicEndpoints: List[ZServerEndpoint[AuthService & AppConfig & TimeProvider, Any]] = List(
    endpoint.post.tag("Auth").summary("Register").in(base / "auth" / "register")
      .in(jsonBody[RegisterUserRequest]).out(jsonBody[AuthResponse]).errorOut(stringBody)
      .zServerLogic { req =>
        (for
          user <- ZIO.serviceWithZIO[AuthService](_.register(req))
          session <- ZIO.serviceWithZIO[AuthService](_.login(LoginUserRequest(req.email, req.password), None, None)).map(_._2)
          config <- ZIO.service[AppConfig]
          tp <- ZIO.service[TimeProvider]
        yield buildAuthResponse(user, session, config.jwt, tp)).mapError(_.getMessage)
      },
    endpoint.post.tag("Auth").summary("Login").in(base / "auth" / "login")
      .in(jsonBody[LoginUserRequest]).out(jsonBody[AuthResponse]).errorOut(stringBody)
      .zServerLogic { req =>
        (for
          (user, session) <- ZIO.serviceWithZIO[AuthService](_.login(req, None, None))
          config <- ZIO.service[AppConfig]; tp <- ZIO.service[TimeProvider]
        yield buildAuthResponse(user, session, config.jwt, tp)).mapError(_.getMessage)
      },
    endpoint.post.tag("Auth").summary("Refresh token").in(base / "auth" / "refresh")
      .in(jsonBody[RefreshTokenRequest]).out(jsonBody[Map[String, String]]).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.refreshToken(req)).map { case (a, r) => Map("accessToken" -> a, "refreshToken" -> r) }.mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Verify email").in(base / "auth" / "verify-email")
      .in(jsonBody[VerifyEmailRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.verifyEmail(req)).as("Email verified").mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Forgot password").in(base / "auth" / "forgot-password")
      .in(jsonBody[ForgotPasswordRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.forgotPassword(req)).as("Reset email sent").mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Reset password").in(base / "auth" / "reset-password")
      .in(jsonBody[ResetPasswordRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.resetPassword(req)).as("Password reset").mapError(_.getMessage) }
  )

  // Secure endpoints (JWT auth required)
  val secureEndpoints: List[ZServerEndpoint[AuthService & AppConfig & TimeProvider, Any]] = List(
    BaseEndpoints.secureEndpoint.post.tag("Auth").summary("Logout").in(base / "auth" / "logout")
      .in(header[UUID]("X-Session-Id"))
      .out(statusCode(sttp.model.StatusCode(204)))
      .serverLogic { ctx => sid =>
        ZIO.serviceWithZIO[AuthService](_.logout(sid)).mapError(e => AuthError(e.getMessage, 400))
      },
    BaseEndpoints.secureEndpoint.post.tag("Auth").summary("Change password").in(base / "auth" / "change-password")
      .in(jsonBody[ChangePasswordRequest]).out(stringBody)
      .serverLogic { ctx => req =>
        ZIO.serviceWithZIO[AuthService](_.changePassword(ctx.userId, req)).as("Password changed").mapError(e => AuthError(e.getMessage, 400))
      },
    BaseEndpoints.secureEndpoint.get.tag("Auth").summary("Get me").in(base / "auth" / "me")
      .out(jsonBody[User])
      .serverLogic { ctx => _ =>
        ZIO.serviceWithZIO[AuthService](_.getUser(ctx.userId)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(e => AuthError(e.getMessage, 404))
      },
    BaseEndpoints.secureEndpoint.put.tag("Auth").summary("Update me").in(base / "auth" / "me")
      .in(jsonBody[UpdateProfileRequest]).out(jsonBody[User])
      .serverLogic { ctx => req =>
        ZIO.serviceWithZIO[AuthService](_.updateProfile(ctx.userId, req)).mapError(e => AuthError(e.getMessage, 400))
      }
  )

  val endpoints: List[ZServerEndpoint[AuthService & AppConfig & TimeProvider, Any]] = publicEndpoints ++ secureEndpoints

  val routes: URIO[AuthService & AppConfig & TimeProvider, Routes[Any, Response]] = toRoutes(endpoints)
