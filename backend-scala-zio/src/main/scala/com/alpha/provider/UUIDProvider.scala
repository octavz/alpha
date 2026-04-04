package com.alpha.provider

import zio.*
import java.util.UUID

trait UUIDProvider:
  def randomUUID(): UUID

object UUIDProvider:
  val live: ULayer[UUIDProvider] = ZLayer.succeed(new UUIDProvider {
    def randomUUID(): UUID = UUID.randomUUID()
  })
