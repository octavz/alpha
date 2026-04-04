package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import com.alpha.domain.enums.UserRole
import java.util.UUID
import io.getquill.*

trait UserRepository:
  def findById(id: UUID): Task[Option[User]]
  def findByEmail(email: String): Task[Option[User]]
  def findByGoogleId(googleId: String): Task[Option[User]]
  def existsByEmail(email: String): Task[Boolean]
  def create(user: User): Task[UUID]
  def update(user: User): Task[Int]
  def delete(id: UUID): Task[Int]
  def findByRole(role: UserRole): Task[List[User]]
  def findByRegionId(regionId: UUID): Task[List[User]]
  def searchByEmailOrName(searchQuery: String): Task[List[User]]

object UserRepository:
  val layer: ZLayer[PostgresCtx, Nothing, UserRepository] =
    ZLayer.fromFunction(new UserRepositoryImpl(_))

class UserRepositoryImpl(ctx: PostgresCtx) extends UserRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[User]] =
    ZIO.attempt:
      run(query[User].filter(_.id == lift(id))).headOption

  override def findByEmail(email: String): Task[Option[User]] =
    ZIO.attempt:
      run(query[User].filter(_.email == lift(email))).headOption

  override def findByGoogleId(googleId: String): Task[Option[User]] =
    ZIO.attempt:
      run(query[User].filter(_.googleId == lift(Some(googleId)))).headOption

  override def existsByEmail(email: String): Task[Boolean] =
    ZIO.attempt:
      run(query[User].filter(_.email == lift(email)).nonEmpty)

  override def create(user: User): Task[UUID] =
    ZIO.attempt:
      run(query[User].insertValue(lift(user)))
      user.id

  override def update(user: User): Task[Int] =
    ZIO.attempt:
      run(query[User]
        .filter(_.id == lift(user.id))
        .updateValue(lift(user)))
      1

  override def delete(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[User].filter(_.id == lift(id)).delete)
      1

  override def findByRole(role: UserRole): Task[List[User]] =
    ZIO.attempt:
      run(query[User].filter(_.role == lift(role.value))).toList

  override def findByRegionId(regionId: UUID): Task[List[User]] =
    ZIO.attempt:
      run(query[User].filter(_.regionId == lift(Some(regionId)))).toList

  override def searchByEmailOrName(searchQuery: String): Task[List[User]] =
    ZIO.attempt:
      val pattern = "%" + searchQuery + "%"
      run(query[User]
        .filter(u => u.email.like(lift(pattern)) || u.name.exists(_.like(lift(pattern))))).toList
