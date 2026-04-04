package com.alpha.controller

import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.{Response, Routes}

object HealthEndpoint:
  private val base   = "api" / "v1"
  private val interp = ZioHttpInterpreter()

  val routes: Routes[Any, Response] =
    interp.toHttp(List(
      endpoint.get.tag("Health").summary("Health check").in(base / "health").out(stringBody).zServerLogic[Any](_ =>
        ZIO.succeed("OK"))
    ))
