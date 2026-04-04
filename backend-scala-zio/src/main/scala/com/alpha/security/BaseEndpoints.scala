package com.alpha.security

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.{auth, endpoint}
import zio.*
import com.alpha.config.AppConfig

object BaseEndpoints:

  val secureEndpoint: ZPartialServerEndpoint[AppConfig, AuthToken, AuthContext, Unit, AuthError, Unit, Any] =
    endpoint
      .securityIn(auth.bearer[String]().mapTo[AuthToken])
      .errorOut(jsonBody[AuthError])
      .zServerSecurityLogic[AppConfig, AuthContext](JwtAuth.authenticate)
