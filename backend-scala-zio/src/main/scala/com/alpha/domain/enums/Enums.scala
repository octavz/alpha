package com.alpha.domain.enums

import sttp.tapir.Schema
import sttp.tapir.Codec
import sttp.tapir.CodecFormat
import zio.json.*

enum UserRole(val value: String):
  case ADMIN          extends UserRole("ADMIN")
  case CUSTOMER       extends UserRole("CUSTOMER")
  case BUSINESS_ADMIN extends UserRole("BUSINESS_ADMIN")
  case BUSINESS_STAFF extends UserRole("BUSINESS_STAFF")

object UserRole:
  given JsonEncoder[UserRole] = JsonEncoder[String].contramap(_.value)
  given JsonDecoder[UserRole] = JsonDecoder[String].mapOrFail { s =>
    values.find(_.value == s).toRight(s"Unknown role: $s")
  }
  given Schema[UserRole]      = Schema.string

enum VerificationStatus(val value: String):
  case PENDING  extends VerificationStatus("PENDING")
  case APPROVED extends VerificationStatus("APPROVED")
  case REJECTED extends VerificationStatus("REJECTED")

object VerificationStatus:
  given JsonEncoder[VerificationStatus] = JsonEncoder[String].contramap(_.value)
  given JsonDecoder[VerificationStatus] = JsonDecoder[String].mapOrFail { s =>
    values.find(_.value == s).toRight(s"Unknown status: $s")
  }
  given Schema[VerificationStatus]      = Schema.string

enum AppointmentStatus(val value: String):
  case PENDING   extends AppointmentStatus("PENDING")
  case CONFIRMED extends AppointmentStatus("CONFIRMED")
  case COMPLETED extends AppointmentStatus("COMPLETED")
  case CANCELLED extends AppointmentStatus("CANCELLED")
  case NO_SHOW   extends AppointmentStatus("NO_SHOW")

object AppointmentStatus:
  given JsonEncoder[AppointmentStatus] = JsonEncoder[String].contramap(_.value)
  given JsonDecoder[AppointmentStatus] = JsonDecoder[String].mapOrFail { s =>
    values.find(_.value == s).toRight(s"Unknown status: $s")
  }
  given Schema[AppointmentStatus]      = Schema.string
