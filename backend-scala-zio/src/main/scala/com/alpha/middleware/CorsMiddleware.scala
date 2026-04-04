package com.alpha.middleware

import zio.*
import zio.http.*

object CorsMiddleware:

  val cors: Middleware[Any] =
    Middleware.cors(Middleware.CorsConfig(
      allowedOrigin = _ => Some(Header.AccessControlAllowOrigin.All),
      allowedMethods =
        Header.AccessControlAllowMethods(Method.GET, Method.POST, Method.PUT, Method.DELETE, Method.OPTIONS),
      allowedHeaders = Header.AccessControlAllowHeaders("Content-Type", "Authorization"),
      allowCredentials = Header.AccessControlAllowCredentials.Allow,
      maxAge = Some(Header.AccessControlMaxAge(zio.Duration.fromSeconds(3600)))
    ))
