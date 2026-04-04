package com.alpha.testutil

import zio.*
import zio.test.TestAspect.*
import com.dimafeng.testcontainers.postgresql.PostgreSQLContainer
import com.alpha.config.DatabaseConfig
import com.alpha.config.PoolConfig

object TestContainer extends ZIOSpecDefault:

  private val container: ZLayer[Any, Throwable, PostgreSQLContainer] = ZLayer.scoped {
    ZIO.attempt {
      val c = PostgreSQLContainer(
        dockerImageName = "postgres:16-alpine",
        databaseName = "alpha",
        username = "alpha_user",
        password = "alpha_password"
      )
      c.start()
      c
    }
  }

  val dbConfigLayer: ZLayer[PostgreSQLContainer, Throwable, DatabaseConfig] = ZLayer.fromFunction { container =>
    DatabaseConfig(
      url = s"jdbc:postgresql://localhost:${container.getFirstMappedPort}/alpha",
      username = "alpha_user",
      password = "alpha_password",
      connectionPool = PoolConfig(
        initialSize = 1,
        maxSize = 10,
        idleTimeout = 30000,
        maxLifetime = 1800000
      )
    )
  }

  def postgresLayer: ZLayer[PostgreSQLContainer, Throwable, zio.postgres.Postgres] = ZLayer.scoped {
    for
      container <- ZIO.service[PostgreSQLContainer]
      pool      <- zio.pool.Pool.fromConnection(
                     zio.postgres.PostgreSQLConnection(
                       s"jdbc:postgresql://localhost:${container.getFirstMappedPort}/alpha",
                       "alpha_user",
                       "alpha_password"
                     ).Scoped,
                     1,
                     10,
                     30000,
                     1800000
                   )
    yield zio.postgres.Postgres(pool)
  }

  val sharedLayer: ZLayer[Any, Throwable, PostgreSQLContainer & zio.postgres.Postgres & DatabaseConfig] =
    container >>> (postgresLayer ++ dbConfigLayer)
