package com.alpha.config

import zio.*
import zio.config.magnolia.*
import zio.config.typesafe.TypesafeConfigProvider
import io.getquill.*
import com.alpha.repository.PostgresCtx
import com.typesafe.config.ConfigFactory

case class AppConfig(
  app: AppSettings,
  database: DatabaseSettings,
  flyway: FlywaySettings,
  jwt: JwtSettings,
  server: ServerSettings
)

object AppConfig:
  val live: ZLayer[Any, Throwable, AppConfig] =
    ZLayer.fromZIO {
      ZIO.attempt {
        val hoconString = ConfigFactory.load().root().render()
        TypesafeConfigProvider.fromHoconString(hoconString).load(deriveConfig[AppConfig])
      }.flatten
    }

case class AppSettings(
  name: String,
  version: String,
  cors: CorsSettings
)

case class CorsSettings(
  allowedOrigins: String,
  allowedMethods: String,
  allowedHeaders: String,
  allowCredentials: Boolean,
  maxAge: Long
)

case class DatabaseSettings(
  url: String,
  username: String,
  password: String,
  driver: String,
  hikari: HikariSettings
)

case class HikariSettings(
  maximumPoolSize: Int,
  minimumIdle: Int,
  connectionTimeout: Int,
  idleTimeout: Int,
  maxLifetime: Int
)

case class FlywaySettings(
  enabled: Boolean,
  baselineOnMigrate: Boolean,
  baselineVersion: String,
  locations: String,
  validateOnMigrate: Boolean,
  cleanDisabled: Boolean
)

case class JwtSettings(
  secret: String,
  accessSecret: String,
  refreshSecret: String,
  accessExpiry: String,
  refreshExpiry: String,
  issuer: String
)

case class ServerSettings(
  host: String,
  port: Int
)

object DatabaseConfig:
  val postgresLayer: ZLayer[AppConfig, Throwable, PostgresCtx] = ZLayer.scoped {
    for
      config <- ZIO.service[AppConfig]
      db      = config.database
      hikari  = db.hikari
      ds     <- ZIO.attempt {
                  val ds = new com.zaxxer.hikari.HikariDataSource()
                  ds.setJdbcUrl(db.url)
                  ds.setUsername(db.username)
                  ds.setPassword(db.password)
                  ds.setDriverClassName(db.driver)
                  ds.setMaximumPoolSize(hikari.maximumPoolSize)
                  ds.setMinimumIdle(hikari.minimumIdle)
                  ds.setConnectionTimeout(hikari.connectionTimeout.toLong)
                  ds.setIdleTimeout(hikari.idleTimeout.toLong)
                  ds.setMaxLifetime(hikari.maxLifetime.toLong)
                  ds.setPoolName("alpha-pool")
                  ds
                }
      _      <- ZIO.logInfo(s"HikariCP pool initialized: max=${hikari.maximumPoolSize}, min=${hikari.minimumIdle}")
      _      <- ZIO.logInfo(s"Connecting to PostgreSQL: ${db.url}")
    yield PostgresCtx(ds)
  }
