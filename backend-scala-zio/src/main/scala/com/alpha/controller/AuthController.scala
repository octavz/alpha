package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.config.*
import com.alpha.provider.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import com.alpha.middleware.*
import com.alpha.service.*
import pdi.jwt.*
import java.util.UUID

case class AuthResponse(user: User, accessToken: String, refreshToken: String, sessionId: UUID)
object AuthResponse:
  given JsonEncoder[AuthResponse] = DeriveJsonEncoder.gen

case class ApiResponse[T](success: Boolean, data: Option[T], error: Option[String])
object ApiResponse:
  given [T: JsonEncoder]: JsonEncoder[ApiResponse[T]] = DeriveJsonEncoder.gen
  given [T: JsonDecoder]: JsonDecoder[ApiResponse[T]] = DeriveJsonDecoder.gen

  def success[T](data: T): ApiResponse[T] = ApiResponse(true, Some(data), None)
  def successList[T](data: List[T]): ApiResponse[List[T]] = ApiResponse(true, Some(data), None)
  def error[T](message: String): ApiResponse[T] = ApiResponse(false, None, Some(message))

class AuthController(
  authService: AuthService,
  appConfig: AppConfig,
  timeProvider: TimeProvider
):

  private def jwtSettings = appConfig.jwt

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  private def encodeToken(userId: UUID, email: String, role: String, secret: String, expirySeconds: Long, issuer: String, now: java.time.Instant, tokenType: String): String =
    val content = s"""{"email":"$email","role":"$role","type":"$tokenType"}"""
    Jwt.encode(
      JwtClaim(
        subject = Some(userId.toString),
        issuer = Some(issuer),
        expiration = Some(now.plusSeconds(expirySeconds).getEpochSecond),
        issuedAt = Some(now.getEpochSecond),
        content = content
      ),
      secret,
      JwtAlgorithm.HS256
    )

  private def buildAuthResponse(user: User, session: UserSession): AuthResponse =
    val now = timeProvider.now().toInstant
    val accessToken = encodeToken(user.id, user.email, user.role, jwtSettings.accessSecret, 900, jwtSettings.issuer, now, "access")
    val refreshToken = encodeToken(user.id, user.email, user.role, jwtSettings.refreshSecret, 604800, jwtSettings.issuer, now, "refresh")
    AuthResponse(user, accessToken, refreshToken, session.id)

  val routes = Routes(
    Method.POST / "api" / "v1" / "auth" / "register" -> handler(handleRegister),
    Method.POST / "api" / "v1" / "auth" / "login" -> handler(handleLogin),
    Method.POST / "api" / "v1" / "auth" / "refresh" -> handler(handleRefreshToken),
    Method.POST / "api" / "v1" / "auth" / "logout" -> handler(handleLogout),
    Method.POST / "api" / "v1" / "auth" / "verify-email" -> handler(handleVerifyEmail),
    Method.POST / "api" / "v1" / "auth" / "forgot-password" -> handler(handleForgotPassword),
    Method.POST / "api" / "v1" / "auth" / "reset-password" -> handler(handleResetPassword),
    Method.POST / "api" / "v1" / "auth" / "change-password" -> handler(handleChangePassword),
    Method.GET / "api" / "v1" / "auth" / "me" -> handler(handleGetMe),
    Method.PUT / "api" / "v1" / "auth" / "me" -> handler(handleUpdateMe)
  )

  private def handleRegister(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[RegisterUserRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      user <- authService.register(req)
      session <- authService.login(LoginUserRequest(req.email, req.password), request.headers.get("User-Agent").map(_.toString), request.remoteAddress.map(_.toString)).map(_._2)
    yield jsonResponse(ApiResponse.success(buildAuthResponse(user, session))))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleLogin(request: Request): Task[Response] =
    val userAgent = request.headers.get("User-Agent").map(_.toString)
    val ipAddress = request.remoteAddress.map(_.toString)
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[LoginUserRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      (user, session) <- authService.login(req, userAgent, ipAddress)
    yield jsonResponse(ApiResponse.success(buildAuthResponse(user, session))))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.Unauthorized)))

  private def handleRefreshToken(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[RefreshTokenRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      (accessToken, newRefreshToken) <- authService.refreshToken(req)
      response = ApiResponse.success(Map("accessToken" -> accessToken, "refreshToken" -> newRefreshToken))
    yield jsonResponse(response))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.Unauthorized)))

  private def handleLogout(request: Request): Task[Response] =
    (for
      sessionId <- ZIO.fromOption(request.headers.get("X-Session-Id").map(h => UUID.fromString(h.toString))).orElseFail(new Exception("Missing X-Session-Id header"))
      _ <- authService.logout(sessionId)
    yield Response.status(Status.NoContent))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleVerifyEmail(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[VerifyEmailRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      _ <- authService.verifyEmail(req)
    yield jsonResponse(ApiResponse.success("Email verified successfully")))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleForgotPassword(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[ForgotPasswordRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      _ <- authService.forgotPassword(req)
    yield jsonResponse(ApiResponse.success("Password reset email sent")))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleResetPassword(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[ResetPasswordRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      _ <- authService.resetPassword(req)
    yield jsonResponse(ApiResponse.success("Password reset successfully")))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleChangePassword(request: Request): Task[Response] =
    (for
      userId <- ZIO.fromOption(request.headers.get("X-User-Id").map(h => UUID.fromString(h.toString))).orElseFail(new Exception("Missing X-User-Id header"))
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[ChangePasswordRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      _ <- authService.changePassword(userId, req)
    yield jsonResponse(ApiResponse.success("Password changed successfully")))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleGetMe(request: Request): Task[Response] =
    (for
      userId <- ZIO.fromOption(request.headers.get("X-User-Id").map(h => UUID.fromString(h.toString))).orElseFail(new Exception("Missing X-User-Id header"))
      userOpt <- authService.getUser(userId)
      response <- userOpt match
        case Some(user) => ZIO.succeed(jsonResponse(ApiResponse.success(user)))
        case None => ZIO.succeed(errorResponse("User not found", Status.NotFound))
    yield response)
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdateMe(request: Request): Task[Response] =
    (for
      userId <- ZIO.fromOption(request.headers.get("X-User-Id").map(h => UUID.fromString(h.toString))).orElseFail(new Exception("Missing X-User-Id header"))
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateProfileRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      updated <- authService.updateProfile(userId, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object AuthController:
  val layer: ZLayer[AuthService & AppConfig & TimeProvider, Nothing, AuthController] =
    ZLayer.fromFunction(new AuthController(_, _, _))
