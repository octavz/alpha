package com.alpha.security

import zio.*
import zio.json.*
import pdi.jwt.*
import com.alpha.config.AppConfig
import java.util.UUID

case class AuthToken(value: String)

case class AuthContext(userId: UUID, email: String, role: String)

case class AuthError(message: String, code: Int)

object AuthError:
  given JsonCodec[AuthError] = DeriveJsonCodec.gen

object JwtAuth:

  def authenticate(token: AuthToken): ZIO[AppConfig, AuthError, AuthContext] =
    for
      config <- ZIO.service[AppConfig]
      jwt     = config.jwt
      claim  <- ZIO.fromEither(Jwt.decode(token.value, jwt.accessSecret, Seq(JwtAlgorithm.HS256)).toEither)
                  .mapError(_ => AuthError("Invalid or expired token", 401))
      userId <- ZIO.fromOption(claim.subject.map(UUID.fromString))
                  .mapError(_ => AuthError("Missing user ID in token", 401))
      email  <- ZIO.fromOption(claim.content.fromJson[Map[String, String]].toOption.flatMap(_.get("email")))
                  .mapError(_ => AuthError("Missing email in token", 401))
      role    = claim.content.fromJson[Map[String, String]].toOption.flatMap(_.get("role")).getOrElse("CUSTOMER")
    yield AuthContext(userId, email, role)
