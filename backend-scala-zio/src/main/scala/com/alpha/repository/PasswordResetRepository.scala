package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import java.time.OffsetDateTime
import io.getquill.*
import io.getquill.extras.OffsetDateTimeOps

trait PasswordResetRepository:
  def findById(id: UUID): Task[Option[PasswordReset]]
  def findByUserId(userId: UUID): Task[List[PasswordReset]]
  def findByToken(token: String): Task[Option[PasswordReset]]
  def findActiveByUserId(userId: UUID): Task[Option[PasswordReset]]
  def create(reset: PasswordReset): Task[UUID]
  def markAsUsed(id: UUID): Task[Int]
  def deleteExpired: Task[Int]

object PasswordResetRepository:
  val layer: ZLayer[PostgresCtx, Nothing, PasswordResetRepository] =
    ZLayer.fromFunction(new PasswordResetRepositoryImpl(_))

class PasswordResetRepositoryImpl(ctx: PostgresCtx) extends PasswordResetRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[PasswordReset]] =
    ZIO.attempt:
      run(query[PasswordReset].filter(_.id == lift(id))).headOption

  override def findByUserId(userId: UUID): Task[List[PasswordReset]] =
    ZIO.attempt:
      run(query[PasswordReset].filter(_.userId == lift(userId))).toList

  override def findByToken(token: String): Task[Option[PasswordReset]] =
    ZIO.attempt:
      run(query[PasswordReset].filter(_.token == lift(token))).headOption

  override def findActiveByUserId(userId: UUID): Task[Option[PasswordReset]] =
    ZIO.attempt:
      run(query[PasswordReset]
        .filter(r =>
          r.userId == lift(userId) && r.isUsed == lift(false) && r.expiresAt > lift(
            OffsetDateTime.now()))).headOption

  override def create(reset: PasswordReset): Task[UUID] =
    ZIO.attempt:
      run(query[PasswordReset].insertValue(lift(reset)))
      reset.id

  override def markAsUsed(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[PasswordReset]
        .filter(_.id == lift(id))
        .update(_.isUsed -> lift(true)))
      1

  override def deleteExpired: Task[Int] =
    ZIO.attempt:
      run(query[PasswordReset]
        .filter(_.expiresAt < lift(OffsetDateTime.now()))
        .delete)
      0
