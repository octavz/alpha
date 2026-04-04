package com.alpha.repository

import zio.*
import com.alpha.domain.model.UserSession
import java.util.UUID
import io.getquill.*
import io.getquill.extras.OffsetDateTimeOps

trait SessionRepository:
  def findById(id: UUID): Task[Option[UserSession]]
  def findByUserId(userId: UUID): Task[List[UserSession]]
  def findByRefreshToken(token: String): Task[Option[UserSession]]
  def findByToken(token: String): Task[Option[UserSession]]
  def create(session: UserSession): Task[UUID]
  def revoke(id: UUID): Task[Int]
  def revokeByUserId(userId: UUID): Task[Int]
  def deleteExpired: Task[Int]

object SessionRepository:
  val layer: ZLayer[PostgresCtx, Nothing, SessionRepository] = 
    ZLayer.fromFunction(new SessionRepositoryImpl(_))

class SessionRepositoryImpl(ctx: PostgresCtx) extends SessionRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[UserSession]] = 
    ZIO.attempt:
      run(query[UserSession].filter(_.id == lift(id))).headOption

  override def findByUserId(userId: UUID): Task[List[UserSession]] = 
    ZIO.attempt:
      run(query[UserSession].filter(s => s.userId == lift(userId) && s.isRevoked == lift(false))).toList

  override def findByRefreshToken(token: String): Task[Option[UserSession]] = 
    ZIO.attempt:
      run(query[UserSession].filter(_.refreshToken == lift(token))).headOption

  override def findByToken(token: String): Task[Option[UserSession]] = 
    ZIO.attempt:
      run(query[UserSession].filter(_.token == lift(token))).headOption

  override def create(session: UserSession): Task[UUID] = 
    ZIO.attempt:
      run(query[UserSession].insertValue(lift(session)))
      session.id

  override def revoke(id: UUID): Task[Int] = 
    ZIO.attempt:
      run(query[UserSession]
        .filter(_.id == lift(id))
        .update(_.isRevoked -> lift(true)))
      1

  override def revokeByUserId(userId: UUID): Task[Int] = 
    ZIO.attempt:
      run(query[UserSession]
        .filter(_.userId == lift(userId))
        .update(_.isRevoked -> lift(true)))
      1

  override def deleteExpired: Task[Int] = 
    ZIO.attempt:
      run(query[UserSession]
        .filter(_.expiresAt < lift(java.time.OffsetDateTime.now()))
        .delete)
      0
