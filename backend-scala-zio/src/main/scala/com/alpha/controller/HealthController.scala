package com.alpha.controller

import zio.*
import zio.http.*

class HealthController:

  val routes = Routes(
    Method.GET / "api" / "v1" / "health" -> handler(Response.text("OK"))
  )

object HealthController:
  val layer: ZLayer[Any, Nothing, HealthController] =
    ZLayer.succeed(new HealthController())
