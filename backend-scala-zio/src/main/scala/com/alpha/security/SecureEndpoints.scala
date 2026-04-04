package com.alpha.security

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.{auth, endpoint}
import zio.*
import com.alpha.config.AppConfig
import java.util.UUID

object SecureEndpoints:

  val secureEndpoint: ZPartialServerEndpoint[AppConfig, AuthToken, AuthContext, Unit, AuthError, Unit, Any] =
    endpoint
      .securityIn(auth.bearer[String]().mapTo[AuthToken])
      .errorOut(jsonBody[AuthError])
      .zServerSecurityLogic[AppConfig, AuthContext](JwtAuth.authenticate)

  def isAdmin(ctx: AuthContext): ZIO[Any, AuthError, Unit] =
    if ctx.role == "ADMIN" then ZIO.unit
    else ZIO.fail(AuthError("Forbidden: admin access required", 403))

  def isOwnerOrAdmin(ctx: AuthContext, resourceOwnerId: UUID): ZIO[Any, AuthError, Unit] =
    if ctx.role == "ADMIN" || ctx.userId == resourceOwnerId then ZIO.unit
    else ZIO.fail(AuthError("Forbidden: you don't own this resource", 403))
