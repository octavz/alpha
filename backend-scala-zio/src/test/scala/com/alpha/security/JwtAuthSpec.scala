package com.alpha.security

import zio.test.*
import zio.json.*
import zio.*
import com.alpha.config.*
import java.util.UUID

object JwtAuthSpec extends ZIOSpecDefault:

  private val userId = UUID.fromString("12345678-1234-1234-1234-123456789abc")

  override def spec = suite("JwtAuthSpec")(
    suite("AuthToken")(
      test("wraps token string value") {
        val token = AuthToken("some.jwt.token")
        assertTrue(token.value == "some.jwt.token")
      },
      test("supports empty string") {
        val token = AuthToken("")
        assertTrue(token.value == "")
      }
    ),
    suite("AuthContext")(
      test("holds userId, email, and role") {
        val ctx = AuthContext(userId, "user@example.com", "ADMIN")
        assertTrue(ctx.userId == userId)
        assertTrue(ctx.email == "user@example.com")
        assertTrue(ctx.role == "ADMIN")
      },
      test("supports different roles") {
        val ctx = AuthContext(userId, "customer@example.com", "CUSTOMER")
        assertTrue(ctx.role == "CUSTOMER")
      }
    ),
    suite("AuthError")(
      test("holds message and code") {
        val error = AuthError("Token expired", 401)
        assertTrue(error.message == "Token expired" && error.code == 401)
      },
      test("supports 403 forbidden code") {
        val error = AuthError("Insufficient permissions", 403)
        assertTrue(error.code == 403)
      },
      test("JSON encodes AuthError") {
        val error = AuthError("Unauthorized access", 401)
        val json  = error.toJson
        assertTrue(json.contains("Unauthorized access"))
        assertTrue(json.contains("401"))
      },
      test("JSON decodes AuthError") {
        val json    = """{"message":"Token expired","code":401}"""
        val decoded = json.fromJson[AuthError]
        assertTrue(decoded.map(_.message) == Right("Token expired"))
        assertTrue(decoded.map(_.code) == Right(401))
      },
      test("round-trip encode/decode AuthError") {
        val error   = AuthError("Invalid token", 401)
        val decoded = error.toJson.fromJson[AuthError]
        assertTrue(decoded == Right(error))
      }
    ),
    suite("authenticate")(
      test("rejects invalid token with AuthError") {
        val testConfig = AppConfig(
          app = AppSettings(
            name = "test",
            version = "1.0",
            cors = CorsSettings("http://localhost", "GET,POST", "*", true, 3600)
          ),
          database = DatabaseSettings(
            url = "jdbc:postgresql://localhost:5433/test",
            username = "test",
            password = "test",
            driver = "org.postgresql.Driver",
            hikari = HikariSettings(5, 1, 30000, 600000, 1800000)
          ),
          flyway = FlywaySettings(
            enabled = true,
            baselineOnMigrate = true,
            baselineVersion = "1",
            locations = "db/migration",
            validateOnMigrate = true,
            cleanDisabled = true
          ),
          jwt = JwtSettings(
            secret = "test-secret-key",
            accessSecret = "test-access-secret-key",
            refreshSecret = "test-refresh-secret-key",
            accessExpiry = "15m",
            refreshExpiry = "7d",
            issuer = "alpha"
          ),
          server = ServerSettings(host = "localhost", port = 3000)
        )

        val result = JwtAuth.authenticate(AuthToken("invalid-token"))
          .provide(ZLayer.succeed(testConfig))
          .either

        for {
          res <- result
        } yield assertTrue(res.isLeft)
      },
      test("rejects empty token with AuthError") {
        val testConfig = AppConfig(
          app = AppSettings(
            name = "test",
            version = "1.0",
            cors = CorsSettings("http://localhost", "GET,POST", "*", true, 3600)
          ),
          database = DatabaseSettings(
            url = "jdbc:postgresql://localhost:5433/test",
            username = "test",
            password = "test",
            driver = "org.postgresql.Driver",
            hikari = HikariSettings(5, 1, 30000, 600000, 1800000)
          ),
          flyway = FlywaySettings(
            enabled = true,
            baselineOnMigrate = true,
            baselineVersion = "1",
            locations = "db/migration",
            validateOnMigrate = true,
            cleanDisabled = true
          ),
          jwt = JwtSettings(
            secret = "test-secret-key",
            accessSecret = "test-access-secret-key",
            refreshSecret = "test-refresh-secret-key",
            accessExpiry = "15m",
            refreshExpiry = "7d",
            issuer = "alpha"
          ),
          server = ServerSettings(host = "localhost", port = 3000)
        )

        val result = JwtAuth.authenticate(AuthToken(""))
          .provide(ZLayer.succeed(testConfig))
          .either

        for {
          res <- result
        } yield assertTrue(res.isLeft)
      }
    )
  )
