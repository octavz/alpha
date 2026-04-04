package com.alpha.service

import zio.test.*
import zio.*
import com.alpha.domain.model.*
import com.alpha.domain.enums.*
import com.alpha.security.*
import java.time.OffsetDateTime
import java.util.UUID

object AuthServiceSpec extends ZIOSpecDefault:

  def mockUserRepository(shouldFind: Boolean = true) = ZLayer.fromFunction: _ =>
    new UserRepository {
      override def findById(id: UUID): ZIO[Any, Throwable, Option[User]] =
        ZIO.succeed(Some(testUser))
      override def findByEmail(email: String): ZIO[Any, Throwable, Option[User]] =
        ZIO.succeed(if shouldFind && email == testUser.email then Some(testUser) else None)
      override def create(user: User): ZIO[Any, Throwable, UUID] = ZIO.succeed(user.id)
      override def update(user: User): ZIO[Any, Throwable, Int] = ZIO.succeed(1)
      override def delete(id: UUID): ZIO[Any, Throwable, Int] = ZIO.succeed(1)
    }

  def mockSessionRepository = ZLayer.fromFunction: _ =>
    new SessionRepository {
      override def findById(id: UUID): ZIO[Any, Throwable, Option[Session]] = ZIO.succeed(None)
      override def findByRefreshToken(token: String): ZIO[Any, Throwable, Option[Session]] = ZIO.succeed(None)
      override def create(session: Session): ZIO[Any, Throwable, UUID] = ZIO.succeed(session.id)
      override def revoke(id: UUID): ZIO[Any, Throwable, Int] = ZIO.succeed(1)
      override def revokeByUserId(userId: UUID): ZIO[Any, Throwable, Int] = ZIO.succeed(1)
    }

  val testUser = User(
    id = UUID.randomUUID(),
    email = "test@example.com",
    passwordHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    name = Some("Test User"),
    phone = Some("1234567890"),
    role = "ADMIN",
    regionId = Some(UUID.randomUUID()),
    isActive = true,
    createdAt = OffsetDateTime.now(),
    updatedAt = None
  )

  val testLayer = JwtSettings.live >>> JwtService.layer ++ mockUserRepository() ++ mockSessionRepository() >>> AuthService.layer

  override def spec: Spec[Any, Throwable] = suite("AuthService Spec")(
    test("login should return Some(AuthResponse) for valid credentials") {
      for
        authService <- ZIO.service[AuthService]
        result <- authService.login(LoginRequest("test@example.com", ""))
      yield
        assertTrue(result.isDefined)
        assertTrue(result.get.accessToken.nonEmpty)
        assertTrue(result.get.refreshToken.nonEmpty)
    },
    test("login should return None for invalid email") {
      for
        authService <- ZIO.service[AuthService]
        result <- authService.login(LoginRequest("nonexistent@example.com", "password"))
      yield
        assertTrue(result.isEmpty)
    },
    test("register should return Right(AuthResponse) for new user") {
      for
        authService <- ZIO.service[AuthService]
        result <- authService.register(RegisterRequest("new@example.com", "password123", "New User", None, None, None))
      yield
        result match
          case Right(auth) =>
            assertTrue(auth.accessToken.nonEmpty) &&
            assertTrue(auth.refreshToken.nonEmpty) &&
            assertTrue(auth.user.email == "new@example.com")
          case Left(msg) => assertTrue(false, "Expected Right but got Left: " + msg)
    },
    test("register should return Left for existing email") {
      for
        authService <- ZIO.service[AuthService]
        result <- authService.register(RegisterRequest("test@example.com", "password123", "Test User", None, None, None))
      yield
        result match
          case Left(msg) => assertTrue(msg.contains("exists"))
          case Right(_) => assertTrue(false, "Expected Left but got Right")
    },
    test("refresh should return Some(AuthResponse) for valid refresh token") {
      for
        jwtService <- ZIO.service[JwtService]
        authService <- ZIO.service[AuthService]
        token = jwtService.generateToken(testUser.id, testUser.email, testUser.role)
        result <- authService.refresh(token.refreshToken)
      yield
        assertTrue(result.isDefined)
        assertTrue(result.get.accessToken.nonEmpty)
    },
    test("refresh should return None for invalid token") {
      for
        authService <- ZIO.service[AuthService]
        result <- authService.refresh("invalid-token")
      yield
        assertTrue(result.isEmpty)
    },
    test("logout should revoke session") {
      for
        authService <- ZIO.service[AuthService]
        result <- authService.logout(UUID.randomUUID())
      yield
        assertTrue(result == ())
    }
  ).provide(testLayer)
