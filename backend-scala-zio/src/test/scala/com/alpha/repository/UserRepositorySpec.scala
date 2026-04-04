package com.alpha.repository

import zio.*
import zio.test.*
import zio.test.TestAspect.*
import com.alpha.testutil.PostgresContainer
import com.alpha.domain.model.*
import java.time.OffsetDateTime
import java.util.UUID

object UserRepositorySpec extends ZIOSpecDefault:

  override def spec = suite("UserRepositorySpec")(
    test("findById should return user when exists") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        id = UUID.randomUUID()
        now = OffsetDateTime.now()
        user = User(
          id = id,
          email = "test@example.com",
          passwordHash = "hash123",
          name = Some("Test User"),
          phone = Some("1234567890"),
          role = "ADMIN",
          regionId = Some(UUID.randomUUID()),
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        _ <- repo.create(user)
        result <- repo.findById(id)
      yield
        assertTrue(result.isDefined) &&
        assertTrue(result.get.email == "test@example.com") &&
        assertTrue(result.get.role == "ADMIN")
    },
    test("findById should return None when not exists") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        result <- repo.findById(UUID.randomUUID())
      yield
        assertTrue(result.isEmpty)
    },
    test("findByEmail should return user when exists") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        id = UUID.randomUUID()
        now = OffsetDateTime.now()
        user = User(
          id = id,
          email = "findme@example.com",
          passwordHash = "hash123",
          name = Some("Find Me"),
          phone = None,
          role = "CUSTOMER",
          regionId = None,
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        _ <- repo.create(user)
        result <- repo.findByEmail("findme@example.com")
      yield
        assertTrue(result.isDefined) &&
        assertTrue(result.get.name.contains("Find Me"))
    },
    test("findByEmail should return None when not exists") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        result <- repo.findByEmail("nonexistent@example.com")
      yield
        assertTrue(result.isEmpty)
    },
    test("create should insert and return id") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        id = UUID.randomUUID()
        now = OffsetDateTime.now()
        user = User(
          id = id,
          email = "create@example.com",
          passwordHash = "hash",
          name = None,
          phone = None,
          role = "USER",
          regionId = None,
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        result <- repo.create(user)
      yield
        assertTrue(result == id)
    },
    test("update should modify existing user") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        id = UUID.randomUUID()
        now = OffsetDateTime.now()
        user = User(
          id = id,
          email = "update@example.com",
          passwordHash = "hash",
          name = Some("Original"),
          phone = None,
          role = "USER",
          regionId = None,
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        _ <- repo.create(user)
        updatedUser = user.copy(name = Some("Updated"), updatedAt = Some(OffsetDateTime.now()))
        _ <- repo.update(updatedUser)
        result <- repo.findById(id)
      yield
        assertTrue(result.isDefined) &&
        assertTrue(result.get.name.contains("Updated"))
    },
    test("delete should remove user") {
      for
        postgres <- ZIO.service[Postgres]
        repo = UserRepositoryImpl(postgres)
        id = UUID.randomUUID()
        now = OffsetDateTime.now()
        user = User(
          id = id,
          email = "delete@example.com",
          passwordHash = "hash",
          name = Some("Delete Me"),
          phone = None,
          role = "USER",
          regionId = None,
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        _ <- repo.create(user)
        _ <- repo.delete(id)
        result <- repo.findById(id)
      yield
        assertTrue(result.isEmpty)
    }
  ).provideCustomLayerShared(PostgresContainer.layer) @@ sequential
