package com.alpha.service

import zio.*

trait EmailService:
  def sendVerificationEmail(email: String, token: String): Task[Unit]
  def sendPasswordResetEmail(email: String, token: String): Task[Unit]
  def sendWelcomeEmail(email: String, name: String): Task[Unit]

object EmailService:
  val live: ZLayer[Any, Nothing, EmailService] =
    ZLayer.succeed(new EmailServiceImpl)

class EmailServiceImpl extends EmailService:
  override def sendVerificationEmail(email: String, token: String): Task[Unit] =
    ZIO.logInfo(s"[EMAIL] Verification email to $email with token $token")

  override def sendPasswordResetEmail(email: String, token: String): Task[Unit] =
    ZIO.logInfo(s"[EMAIL] Password reset email to $email with token $token")

  override def sendWelcomeEmail(email: String, name: String): Task[Unit] =
    ZIO.logInfo(s"[EMAIL] Welcome email to $email for $name")
