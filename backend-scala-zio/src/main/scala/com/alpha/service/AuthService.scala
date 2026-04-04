package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.enums.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID
import java.time.OffsetDateTime

trait AuthService:
  def register(req: RegisterUserRequest): Task[User]
  def login(req: LoginUserRequest, userAgent: Option[String], ipAddress: Option[String]): Task[(User, UserSession)]
  def refreshToken(req: RefreshTokenRequest): Task[(String, String)]
  def logout(sessionId: UUID): Task[Unit]
  def verifyEmail(req: VerifyEmailRequest): Task[Unit]
  def forgotPassword(req: ForgotPasswordRequest): Task[Unit]
  def resetPassword(req: ResetPasswordRequest): Task[Unit]
  def getUser(id: UUID): Task[Option[User]]
  def updateProfile(userId: UUID, req: UpdateProfileRequest): Task[User]
  def changePassword(userId: UUID, req: ChangePasswordRequest): Task[Unit]

object AuthService:
  val layer: ZLayer[
    UserRepository & SessionRepository & EmailVerificationRepository & PasswordResetRepository & TimeProvider & UUIDProvider,
    Nothing,
    AuthService] =
    ZLayer.fromFunction(new AuthServiceImpl(_, _, _, _, _, _))

class AuthServiceImpl(
  userRepo: UserRepository,
  sessionRepo: SessionRepository,
  emailVerificationRepo: EmailVerificationRepository,
  passwordResetRepo: PasswordResetRepository,
  timeProvider: TimeProvider,
  uuidProvider: UUIDProvider
) extends AuthService:

  private def hashPassword(password: String): String                 = BCrypt.hashpw(password, BCrypt.gensalt())
  private def checkPassword(password: String, hash: String): Boolean = BCrypt.checkpw(password, hash)

  override def register(req: RegisterUserRequest): Task[User] =
    for
      exists <- userRepo.existsByEmail(req.email)
      _      <- ZIO.fail(new Exception("Email already exists")).when(exists)
      user    = User(
                  id = uuidProvider.randomUUID(),
                  email = req.email,
                  passwordHash = hashPassword(req.password),
                  name = req.name,
                  phone = req.phone,
                  role = req.role.flatMap(r => UserRole.values.find(_.value == r)).map(_.value).getOrElse("CUSTOMER"),
                  regionId = req.regionId,
                  isActive = true,
                  isBanned = false,
                  emailVerified = false,
                  googleId = None,
                  avatarUrl = None,
                  createdAt = timeProvider.now(),
                  updatedAt = None
                )
      id     <- userRepo.create(user)
    yield user.copy(id = id)

  override def login(
    req: LoginUserRequest,
    userAgent: Option[String],
    ipAddress: Option[String]): Task[(User, UserSession)] =
    for
      userOpt   <- userRepo.findByEmail(req.email)
      user      <- ZIO.fromOption(userOpt).orElseFail(new Exception("Invalid credentials"))
      _         <- ZIO.fail(new Exception("Account is banned")).when(user.isBanned)
      _         <- ZIO.fail(new Exception("Invalid credentials")).when(!checkPassword(req.password, user.passwordHash))
      session    = UserSession(
                     id = uuidProvider.randomUUID(),
                     userId = user.id,
                     refreshToken = uuidProvider.randomUUID().toString,
                     token = uuidProvider.randomUUID().toString,
                     userAgent = userAgent,
                     ipAddress = ipAddress,
                     expiresAt = timeProvider.now().plusDays(7),
                     isRevoked = false,
                     createdAt = timeProvider.now()
                   )
      sessionId <- sessionRepo.create(session)
    yield (user, session.copy(id = sessionId))

  override def refreshToken(req: RefreshTokenRequest): Task[(String, String)] =
    for
      sessionOpt     <- sessionRepo.findByRefreshToken(req.refreshToken)
      session        <- ZIO.fromOption(sessionOpt).orElseFail(new Exception("Invalid refresh token"))
      _              <- ZIO.fail(new Exception("Session revoked")).when(session.isRevoked)
      _              <- ZIO.fail(new Exception("Session expired")).when(session.expiresAt.isBefore(timeProvider.now()))
      userOpt        <- userRepo.findById(session.userId)
      user           <- ZIO.fromOption(userOpt).orElseFail(new Exception("User not found"))
      newAccessToken  = s"access-${user.id}-${uuidProvider.randomUUID()}"
      newRefreshToken = uuidProvider.randomUUID().toString
    yield (newAccessToken, newRefreshToken)

  override def logout(sessionId: UUID): Task[Unit] =
    sessionRepo.revoke(sessionId).unit

  override def verifyEmail(req: VerifyEmailRequest): Task[Unit] =
    for
      verificationOpt <- emailVerificationRepo.findByToken(req.token)
      verification    <- ZIO.fromOption(verificationOpt).orElseFail(new Exception("Invalid verification token"))
      _               <- ZIO.fail(new Exception("Token expired")).when(verification.expiresAt.isBefore(timeProvider.now()))
      _               <- ZIO.fail(new Exception("Token already used")).when(verification.isUsed)
      _               <- emailVerificationRepo.markAsUsed(verification.id)
      userOpt         <- userRepo.findById(verification.userId)
      user            <- ZIO.fromOption(userOpt).orElseFail(new Exception("User not found"))
      _               <- userRepo.update(user.copy(emailVerified = true, updatedAt = Some(timeProvider.now())))
    yield ()

  override def forgotPassword(req: ForgotPasswordRequest): Task[Unit] =
    for
      userOpt <- userRepo.findByEmail(req.email)
      user    <- ZIO.fromOption(userOpt).orElseFail(new Exception("User not found"))
      reset    = PasswordReset(
                   id = uuidProvider.randomUUID(),
                   userId = user.id,
                   token = uuidProvider.randomUUID().toString,
                   expiresAt = timeProvider.now().plusHours(1),
                   isUsed = false,
                   createdAt = timeProvider.now()
                 )
      _       <- passwordResetRepo.create(reset)
    yield ()

  override def resetPassword(req: ResetPasswordRequest): Task[Unit] =
    for
      resetOpt      <- passwordResetRepo.findByToken(req.token)
      reset         <- ZIO.fromOption(resetOpt).orElseFail(new Exception("Invalid reset token"))
      _             <- ZIO.fail(new Exception("Token expired")).when(reset.expiresAt.isBefore(timeProvider.now()))
      _             <- ZIO.fail(new Exception("Token already used")).when(reset.isUsed)
      hashedPassword = hashPassword(req.newPassword)
      userOpt       <- userRepo.findById(reset.userId)
      user          <- ZIO.fromOption(userOpt).orElseFail(new Exception("User not found"))
      _             <- userRepo.update(user.copy(passwordHash = hashedPassword, updatedAt = Some(timeProvider.now())))
      _             <- passwordResetRepo.markAsUsed(reset.id)
    yield ()

  override def getUser(id: UUID): Task[Option[User]] =
    userRepo.findById(id)

  override def updateProfile(userId: UUID, req: UpdateProfileRequest): Task[User] =
    for
      userOpt <- userRepo.findById(userId)
      user    <- ZIO.fromOption(userOpt).orElseFail(new Exception("User not found"))
      updated  = user.copy(
                   name = req.name.orElse(user.name),
                   phone = req.phone.orElse(user.phone),
                   avatarUrl = req.avatarUrl.orElse(user.avatarUrl),
                   updatedAt = Some(timeProvider.now()))
      _       <- userRepo.update(updated)
    yield updated

  override def changePassword(userId: UUID, req: ChangePasswordRequest): Task[Unit] =
    for
      userOpt       <- userRepo.findById(userId)
      user          <- ZIO.fromOption(userOpt).orElseFail(new Exception("User not found"))
      _             <- ZIO.fail(new Exception("Current password is incorrect")).when(!checkPassword(
                         req.currentPassword,
                         user.passwordHash))
      hashedPassword = hashPassword(req.newPassword)
      _             <- userRepo.update(user.copy(passwordHash = hashedPassword, updatedAt = Some(timeProvider.now())))
    yield ()
