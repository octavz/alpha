package com.alpha.config

import zio.test.*
import zio.*

object ConfigSpec extends ZIOSpecDefault:

  override def spec: Spec[Any, Nothing] = suite("Config Spec")(
    test("DatabaseConfig should be created from function") {
      for
        dbConfig <- DatabaseConfig.fromConfig()
      yield
        assertTrue(dbConfig.url.contains("jdbc:postgresql"))
    },
    test("DatabaseConfig should contain pool configuration") {
      for
        dbConfig <- DatabaseConfig.fromConfig()
      yield
        assertTrue(dbConfig.connectionPool.initialSize > 0) &&
        assertTrue(dbConfig.connectionPool.maxSize > 0)
    },
    test("JwtSettings should be loadable from config") {
      for
        jwtSettings <- ZIO.service[JwtSettings]
      yield
        assertTrue(jwtSettings.accessSecret.nonEmpty) &&
        assertTrue(jwtSettings.refreshSecret.nonEmpty) &&
        assertTrue(jwtSettings.accessExpiry.contains("m"))
    },
    test("AppConfig should be loadable") {
      for
        appConfig <- ZIO.service[AppConfig]
      yield
        assertTrue(appConfig.app.name == "alpha-backend") &&
        assertTrue(appConfig.server.port > 0) &&
        assertTrue(appConfig.database.url.nonEmpty)
    }
  ).provide(
    AppConfig.live,
    JwtSettings.live,
    DatabaseConfig.live
  )
