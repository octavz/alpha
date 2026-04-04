package com.alpha.domain.model

import com.alpha.domain.enums.*
import java.time.{LocalDate, LocalTime, OffsetDateTime}
import java.util.UUID
import zio.json.*

trait BaseEntity:
  val id: UUID
  val createdAt: OffsetDateTime
  val updatedAt: Option[OffsetDateTime]

case class User(
  id: UUID,
  email: String,
  passwordHash: String,
  name: Option[String],
  phone: Option[String],
  role: String,
  regionId: Option[UUID],
  isActive: Boolean,
  isBanned: Boolean,
  emailVerified: Boolean,
  googleId: Option[String],
  avatarUrl: Option[String],
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
) extends BaseEntity

object User:
  given JsonEncoder[User] = DeriveJsonEncoder.gen
  given JsonDecoder[User] = DeriveJsonDecoder.gen

case class Business(
  id: UUID,
  userId: UUID,
  name: String,
  slug: String,
  description: Option[String],
  email: Option[String],
  phone: Option[String],
  website: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  city: Option[String],
  state: Option[String],
  zipCode: Option[String],
  country: Option[String],
  latitude: Option[BigDecimal],
  longitude: Option[BigDecimal],
  categoryId: UUID,
  regionId: UUID,
  verificationStatus: String,
  isVerified: Boolean,
  isActive: Boolean,
  isFeatured: Boolean,
  logoUrl: Option[String],
  coverImageUrl: Option[String],
  servicePointsCount: Int,
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
) extends BaseEntity

object Business:
  given JsonEncoder[Business] = DeriveJsonEncoder.gen
  given JsonDecoder[Business] = DeriveJsonDecoder.gen

case class Category(
  id: UUID,
  name: String,
  slug: String,
  description: Option[String],
  icon: Option[String],
  parentId: Option[UUID],
  sortOrder: Int,
  isActive: Boolean,
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
)

object Category:
  given JsonEncoder[Category] = DeriveJsonEncoder.gen
  given JsonDecoder[Category] = DeriveJsonDecoder.gen

case class Region(
  id: UUID,
  name: String,
  code: String,
  country: String,
  timezone: String,
  isActive: Boolean,
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
)

object Region:
  given JsonEncoder[Region] = DeriveJsonEncoder.gen
  given JsonDecoder[Region] = DeriveJsonDecoder.gen

case class BusinessHours(
  id: UUID,
  businessId: UUID,
  dayOfWeek: Int,
  openTime: Option[String],
  closeTime: Option[String],
  isClosed: Boolean
)

object BusinessHours:
  given JsonEncoder[BusinessHours] = DeriveJsonEncoder.gen
  given JsonDecoder[BusinessHours] = DeriveJsonDecoder.gen

case class Review(
  id: UUID,
  businessId: UUID,
  userId: UUID,
  appointmentId: Option[UUID],
  rating: Int,
  title: Option[String],
  comment: Option[String],
  isApproved: Boolean,
  isFeatured: Boolean,
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
) extends BaseEntity

object Review:
  given JsonEncoder[Review] = DeriveJsonEncoder.gen
  given JsonDecoder[Review] = DeriveJsonDecoder.gen

case class Appointment(
  id: UUID,
  businessId: UUID,
  userId: Option[UUID],
  serviceId: Option[UUID],
  appointmentDate: LocalDate,
  startTime: LocalTime,
  endTime: LocalTime,
  servicePointNumber: Option[Int],
  customerName: String,
  customerEmail: String,
  customerPhone: Option[String],
  customerNotes: Option[String],
  status: String,
  cancelledAt: Option[OffsetDateTime],
  cancelledReason: Option[String],
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
) extends BaseEntity

object Appointment:
  given JsonEncoder[Appointment] = DeriveJsonEncoder.gen
  given JsonDecoder[Appointment] = DeriveJsonDecoder.gen

case class AvailabilitySlot(
  startTime: LocalTime,
  endTime: LocalTime,
  servicePointNumber: Option[Int],
  isAvailable: Boolean
)

object AvailabilitySlot:
  given JsonEncoder[AvailabilitySlot] = DeriveJsonEncoder.gen
  given JsonDecoder[AvailabilitySlot] = DeriveJsonDecoder.gen

case class Service(
  id: UUID,
  businessId: UUID,
  name: String,
  description: Option[String],
  durationMinutes: Int,
  price: Option[BigDecimal],
  isActive: Boolean,
  sortOrder: Int,
  createdAt: OffsetDateTime,
  updatedAt: Option[OffsetDateTime]
) extends BaseEntity

object Service:
  given JsonEncoder[Service] = DeriveJsonEncoder.gen
  given JsonDecoder[Service] = DeriveJsonDecoder.gen

case class EmailVerification(
  id: UUID,
  userId: UUID,
  token: String,
  expiresAt: OffsetDateTime,
  isUsed: Boolean,
  createdAt: OffsetDateTime
)

object EmailVerification:
  given JsonEncoder[EmailVerification] = DeriveJsonEncoder.gen
  given JsonDecoder[EmailVerification] = DeriveJsonDecoder.gen

case class PasswordReset(
  id: UUID,
  userId: UUID,
  token: String,
  expiresAt: OffsetDateTime,
  isUsed: Boolean,
  createdAt: OffsetDateTime
)

object PasswordReset:
  given JsonEncoder[PasswordReset] = DeriveJsonEncoder.gen
  given JsonDecoder[PasswordReset] = DeriveJsonDecoder.gen

case class UserSession(
  id: UUID,
  userId: UUID,
  refreshToken: String,
  token: String,
  userAgent: Option[String],
  ipAddress: Option[String],
  expiresAt: OffsetDateTime,
  isRevoked: Boolean,
  createdAt: OffsetDateTime
)

object UserSession:
  given JsonEncoder[UserSession] = DeriveJsonEncoder.gen
  given JsonDecoder[UserSession] = DeriveJsonDecoder.gen
