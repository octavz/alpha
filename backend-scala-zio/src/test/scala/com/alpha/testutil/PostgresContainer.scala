package com.alpha.testutil

import zio.*
import zio.pool.*
import zio.postgres.*
import com.dimafeng.testcontainers.postgresql.PostgreSQLContainer

object PostgresContainer:

  def layer: ZLayer[Any, Throwable, Postgres] = ZLayer.scoped {
    for
      container <- ZIO.attempt {
                     val c = PostgreSQLContainer(
                       dockerImageName = "postgres:16-alpine",
                       databaseName = "alpha",
                       username = "alpha_user",
                       password = "alpha_password"
                     )
                     c.start()
                     c
                   }
      _         <- ZIO.attemptBlocking {
                     Thread.sleep(2000)
                   }
      pool      <- Pool.fromConnection(
                     PostgreSQLConnection(
                       s"jdbc:postgresql://localhost:${container.getFirstMappedPort}/alpha",
                       "alpha_user",
                       "alpha_password"
                     ).Scoped,
                     1,
                     10,
                     30000,
                     1800000
                   )
    yield Postgres(pool)
  }
