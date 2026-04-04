package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import java.time.OffsetDateTime
import io.getquill.*
import io.getquill.extras.OffsetDateTimeOps

trait EmailVerificationRepository:
  def findById(id: UUID): Task[Option[EmailVerification]]
  def findByUserId(userId: UUID): Task[List[EmailVerification]]
  def findByToken(token: String): Task[Option[EmailVerification]]
  def findActiveByUserId(userId: UUID): Task[Option[EmailVerification]]
  def create(verification: EmailVerification): Task[UUID]
  def markAsUsed(id: UUID): Task[Int]
  def deleteExpired: Task[Int]

object EmailVerificationRepository:
  val layer: ZLayer[PostgresCtx, Nothing, EmailVerificationRepository] =
    ZLayer.fromFunction(new EmailVerificationRepositoryImpl(_))

class EmailVerificationRepositoryImpl(ctx: PostgresCtx) extends EmailVerificationRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[EmailVerification]] =
    ZIO.attempt:
      run(query[EmailVerification].filter(_.id == lift(id))).headOption

  override def findByUserId(userId: UUID): Task[List[EmailVerification]] =
    ZIO.attempt:
      run(query[EmailVerification].filter(_.userId == lift(userId))).toList

  override def findByToken(token: String): Task[Option[EmailVerification]] =
    ZIO.attempt:
      run(query[EmailVerification].filter(_.token == lift(token))).headOption

  override def findActiveByUserId(userId: UUID): Task[Option[EmailVerification]] =
    ZIO.attempt:
      run(query[EmailVerification]
        .filter(v =>
          v.userId == lift(userId) && v.isUsed == lift(false) && v.expiresAt > lift(
            java.time.OffsetDateTime.now()))).headOption

  override def create(verification: EmailVerification): Task[UUID] =
    ZIO.attempt:
      run(query[EmailVerification].insertValue(lift(verification)))
      verification.id

  override def markAsUsed(id: UUID): Task[Int] =
    ZIO.attempt:
      run(query[EmailVerification]
        .filter(_.id == lift(id))
        .update(_.isUsed -> lift(true)))
      1

  override def deleteExpired: Task[Int] =
    ZIO.attempt:
      run(query[EmailVerification]
        .filter(_.expiresAt < lift(java.time.OffsetDateTime.now()))
        .delete)
      0
