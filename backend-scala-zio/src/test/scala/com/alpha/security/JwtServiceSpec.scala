package com.alpha.security

import zio.test.*
import zio.*
import java.util.UUID

object JwtServiceSpec extends ZIOSpecDefault:

  val testJwtSettings = JwtSettings(
    secret = "test-secret-key-for-jwt-tokens-must-be-at-least-256-bits",
    accessSecret = "test-access-secret-key-for-jwt-tokens-must-be-at-least-256-bits-long",
    refreshSecret = "test-refresh-secret-key-for-jwt-tokens-must-be-at-least-256-bits-long",
    accessExpiry = "15m",
    refreshExpiry = "7d",
    issuer = "test-issuer"
  )

  val testLayer = JwtSettings.live >>> JwtService.layer

  override def spec: Spec[Any, Throwable] = suite("JwtService Spec")(
    test("generateToken should create valid access and refresh tokens") {
      for
        jwtService <- ZIO.service[JwtService]
        userId = UUID.randomUUID()
        email = "test@example.com"
        role = "ADMIN"
        token <- ZIO.succeed(jwtService.generateToken(userId, email, role))
      yield
        assertTrue(token.accessToken.nonEmpty) &&
        assertTrue(token.refreshToken.nonEmpty) &&
        assertTrue(token.expiresAt > 0)
    },
    test("verifyAccessToken should validate access token") {
      for
        jwtService <- ZIO.service[JwtService]
        userId = UUID.randomUUID()
        email = "test@example.com"
        role = "ADMIN"
        token <- ZIO.succeed(jwtService.generateToken(userId, email, role))
        verified <- ZIO.fromTry(jwtService.verifyAccessToken(token.accessToken))
      yield
        assertTrue(verified.sub == userId.toString) &&
        assertTrue(verified.email == email) &&
        assertTrue(verified.role == role)
    },
    test("verifyRefreshToken should validate refresh token") {
      for
        jwtService <- ZIO.service[JwtService]
        userId = UUID.randomUUID()
        email = "test@example.com"
        role = "ADMIN"
        token <- ZIO.succeed(jwtService.generateToken(userId, email, role))
        verified <- ZIO.fromTry(jwtService.verifyRefreshToken(token.refreshToken))
      yield
        assertTrue(verified.sub == userId.toString) &&
        assertTrue(verified.email == email) &&
        assertTrue(verified.role == role)
    },
    test("verifyAccessToken should fail with invalid token") {
      for
        jwtService <- ZIO.service[JwtService]
        result <- ZIO.fromTry(jwtService.verifyAccessToken("invalid-token")).flip
      yield
        assertTrue(result != null)
    },
    test("generateToken should create different access and refresh tokens") {
      for
        jwtService <- ZIO.service[JwtService]
        userId = UUID.randomUUID()
        email = "test@example.com"
        role = "ADMIN"
        token <- ZIO.succeed(jwtService.generateToken(userId, email, role))
      yield
        assertTrue(token.accessToken != token.refreshToken)
    },
    test("verifyRefreshToken should fail with access token") {
      for
        jwtService <- ZIO.service[JwtService]
        userId = UUID.randomUUID()
        email = "test@example.com"
        role = "ADMIN"
        token <- ZIO.succeed(jwtService.generateToken(userId, email, role))
        result <- ZIO.fromTry(jwtService.verifyRefreshToken(token.accessToken)).flip
      yield
        assertTrue(result != null)
    }
  ).provide(testLayer)
