package com.alpha.config

import io.getquill.*
import com.alpha.domain.enums.*

type PostgresCtx = PostgresJdbcContext[SnakeCase]

given MappedEncoding[UserRole, String] = MappedEncoding[UserRole, String](_.value)
given MappedEncoding[String, UserRole] = MappedEncoding[String, UserRole](s =>
  UserRole.values.find(_.value == s).getOrElse(throw new Exception(s"Unknown role: $s")))

given MappedEncoding[VerificationStatus, String] = MappedEncoding[VerificationStatus, String](_.value)
given MappedEncoding[String, VerificationStatus] = MappedEncoding[String, VerificationStatus](s =>
  VerificationStatus.values.find(_.value == s).getOrElse(throw new Exception(s"Unknown status: $s")))

given MappedEncoding[AppointmentStatus, String] = MappedEncoding[AppointmentStatus, String](_.value)
given MappedEncoding[String, AppointmentStatus] = MappedEncoding[String, AppointmentStatus](s =>
  AppointmentStatus.values.find(_.value == s).getOrElse(throw new Exception(s"Unknown status: $s")))
