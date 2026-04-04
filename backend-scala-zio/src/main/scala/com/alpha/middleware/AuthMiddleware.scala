package com.alpha.middleware

import zio.*
import zio.http.*
import zio.json.*
import pdi.jwt.*
import com.alpha.config.AppConfig
import java.util.UUID

case class AuthContext(userId: UUID, email: String, role: String)

object AuthMiddleware:

  private val bearerPrefix = "Bearer "

  def extractToken(headers: Headers): Option[String] =
    headers.get("Authorization").flatMap { value =>
      if value.startsWith(bearerPrefix) then Some(value.substring(bearerPrefix.length))
      else None
    }

  def verifyToken(token: String, secret: String): Either[Throwable, JwtClaim] =
    Jwt.decode(token, secret, Seq(JwtAlgorithm.HS256)).toEither

  def authenticate(req: Request): ZIO[AppConfig, Throwable, AuthContext] =
    for
      config <- ZIO.service[AppConfig]
      jwt = config.jwt
      token <- ZIO.fromOption(extractToken(req.headers))
        .orElseFail(new Exception("Missing authorization header"))
      claim <- ZIO.fromEither(verifyToken(token, jwt.accessSecret))
        .orElseFail(new Exception("Invalid or expired token"))
      userId <- ZIO.fromOption(claim.subject.map(UUID.fromString))
        .orElseFail(new Exception("Missing user ID in token"))
      email <- ZIO.fromOption(claim.content.fromJson[Map[String, String]].toOption.flatMap(_.get("email")))
        .orElseFail(new Exception("Missing email in token"))
      role = claim.content.fromJson[Map[String, String]].toOption.flatMap(_.get("role")).getOrElse("user")
    yield AuthContext(userId, email, role)

  def requireAuth(handler: AuthContext => ZIO[Any, Throwable, Response]): Handler[AppConfig, Throwable, Request, Response] =
    Handler.fromFunctionZIO { (req: Request) =>
      authenticate(req).flatMap(handler).catchAll { error =>
        ZIO.succeed(Response.text(s"Unauthorized: ${error.getMessage}").status(Status.Unauthorized))
      }
    }

  def requireRole(allowedRoles: Set[String])(handler: AuthContext => ZIO[Any, Throwable, Response]): Handler[AppConfig, Throwable, Request, Response] =
    Handler.fromFunctionZIO { (req: Request) =>
      authenticate(req).flatMap { ctx =>
        if allowedRoles.contains(ctx.role) then handler(ctx)
        else ZIO.succeed(Response.text("Forbidden: insufficient permissions").status(Status.Forbidden))
      }.catchAll { error =>
        ZIO.succeed(Response.text(s"Unauthorized: ${error.getMessage}").status(Status.Unauthorized))
      }
    }
