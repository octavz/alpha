package com.alpha.provider

import zio.*
import java.time.OffsetDateTime

trait TimeProvider:
  def now(): OffsetDateTime

object TimeProvider:
  val live: ULayer[TimeProvider] = ZLayer.succeed(new TimeProvider {
    def now(): OffsetDateTime = OffsetDateTime.now()
  })
