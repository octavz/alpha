package com.alpha.dto

import zio.json.*
import java.util.UUID
import java.time.{LocalDate, LocalTime}

case class RegisterUserRequest(
  email: String,
  password: String,
  name: Option[String] = None,
  phone: Option[String] = None,
  role: Option[String] = None,
  regionId: Option[UUID] = None
)
object RegisterUserRequest:
  given JsonDecoder[RegisterUserRequest] = DeriveJsonDecoder.gen

case class LoginUserRequest(
  email: String,
  password: String
)
object LoginUserRequest:
  given JsonDecoder[LoginUserRequest] = DeriveJsonDecoder.gen

case class RefreshTokenRequest(
  refreshToken: String
)
object RefreshTokenRequest:
  given JsonDecoder[RefreshTokenRequest] = DeriveJsonDecoder.gen

case class VerifyEmailRequest(
  token: String
)
object VerifyEmailRequest:
  given JsonDecoder[VerifyEmailRequest] = DeriveJsonDecoder.gen

case class ForgotPasswordRequest(
  email: String
)
object ForgotPasswordRequest:
  given JsonDecoder[ForgotPasswordRequest] = DeriveJsonDecoder.gen

case class ResetPasswordRequest(
  token: String,
  newPassword: String
)
object ResetPasswordRequest:
  given JsonDecoder[ResetPasswordRequest] = DeriveJsonDecoder.gen

case class ChangePasswordRequest(
  currentPassword: String,
  newPassword: String
)
object ChangePasswordRequest:
  given JsonDecoder[ChangePasswordRequest] = DeriveJsonDecoder.gen

case class UpdateProfileRequest(
  name: Option[String] = None,
  phone: Option[String] = None,
  avatarUrl: Option[String] = None
)
object UpdateProfileRequest:
  given JsonDecoder[UpdateProfileRequest] = DeriveJsonDecoder.gen

case class CreateBusinessRequest(
  name: String,
  slug: String,
  description: Option[String] = None,
  email: Option[String] = None,
  phone: Option[String] = None,
  website: Option[String] = None,
  addressLine1: Option[String] = None,
  addressLine2: Option[String] = None,
  city: Option[String] = None,
  state: Option[String] = None,
  zipCode: Option[String] = None,
  country: Option[String] = None,
  latitude: Option[BigDecimal] = None,
  longitude: Option[BigDecimal] = None,
  categoryId: UUID,
  regionId: UUID
)
object CreateBusinessRequest:
  given JsonDecoder[CreateBusinessRequest] = DeriveJsonDecoder.gen

case class UpdateBusinessRequest(
  name: Option[String] = None,
  description: Option[String] = None,
  email: Option[String] = None,
  phone: Option[String] = None,
  website: Option[String] = None,
  addressLine1: Option[String] = None,
  addressLine2: Option[String] = None,
  city: Option[String] = None,
  state: Option[String] = None,
  zipCode: Option[String] = None,
  country: Option[String] = None,
  latitude: Option[BigDecimal] = None,
  longitude: Option[BigDecimal] = None,
  categoryId: Option[UUID] = None,
  regionId: Option[UUID] = None,
  logoUrl: Option[String] = None,
  coverImageUrl: Option[String] = None
)
object UpdateBusinessRequest:
  given JsonDecoder[UpdateBusinessRequest] = DeriveJsonDecoder.gen

case class CreateCategoryRequest(
  name: String,
  slug: String,
  description: Option[String] = None,
  icon: Option[String] = None,
  parentId: Option[UUID] = None,
  sortOrder: Int = 0
)
object CreateCategoryRequest:
  given JsonDecoder[CreateCategoryRequest] = DeriveJsonDecoder.gen

case class UpdateCategoryRequest(
  name: Option[String] = None,
  description: Option[String] = None,
  icon: Option[String] = None,
  parentId: Option[UUID] = None,
  sortOrder: Option[Int] = None,
  isActive: Option[Boolean] = None
)
object UpdateCategoryRequest:
  given JsonDecoder[UpdateCategoryRequest] = DeriveJsonDecoder.gen

case class CreateRegionRequest(
  name: String,
  code: String,
  country: String,
  timezone: String
)
object CreateRegionRequest:
  given JsonDecoder[CreateRegionRequest] = DeriveJsonDecoder.gen

case class UpdateRegionRequest(
  name: Option[String] = None,
  code: Option[String] = None,
  country: Option[String] = None,
  timezone: Option[String] = None,
  isActive: Option[Boolean] = None
)
object UpdateRegionRequest:
  given JsonDecoder[UpdateRegionRequest] = DeriveJsonDecoder.gen

case class CreateAppointmentRequest(
  businessId: UUID,
  userId: Option[UUID] = None,
  serviceId: Option[UUID] = None,
  appointmentDate: LocalDate,
  startTime: LocalTime,
  endTime: LocalTime,
  servicePointNumber: Option[Int] = None,
  customerName: String,
  customerEmail: String,
  customerPhone: Option[String] = None,
  customerNotes: Option[String] = None
)
object CreateAppointmentRequest:
  given JsonDecoder[CreateAppointmentRequest] = DeriveJsonDecoder.gen

case class UpdateAppointmentRequest(
  status: Option[String] = None,
  customerNotes: Option[String] = None
)
object UpdateAppointmentRequest:
  given JsonDecoder[UpdateAppointmentRequest] = DeriveJsonDecoder.gen

case class CancelAppointmentRequest(
  reason: Option[String] = None
)
object CancelAppointmentRequest:
  given JsonDecoder[CancelAppointmentRequest] = DeriveJsonDecoder.gen

case class CreateReviewRequest(
  businessId: UUID,
  userId: UUID,
  appointmentId: Option[UUID] = None,
  rating: Int,
  title: Option[String] = None,
  comment: Option[String] = None
)
object CreateReviewRequest:
  given JsonDecoder[CreateReviewRequest] = DeriveJsonDecoder.gen

case class CreateServiceRequest(
  name: String,
  description: Option[String] = None,
  durationMinutes: Int,
  price: Option[BigDecimal] = None,
  isActive: Boolean = true,
  sortOrder: Int = 0
)
object CreateServiceRequest:
  given JsonDecoder[CreateServiceRequest] = DeriveJsonDecoder.gen

case class UpdateServiceRequest(
  name: Option[String] = None,
  description: Option[String] = None,
  durationMinutes: Option[Int] = None,
  price: Option[BigDecimal] = None,
  isActive: Option[Boolean] = None,
  sortOrder: Option[Int] = None
)
object UpdateServiceRequest:
  given JsonDecoder[UpdateServiceRequest] = DeriveJsonDecoder.gen

case class CreateBusinessHoursRequest(
  businessId: UUID,
  dayOfWeek: Int,
  openTime: Option[String] = None,
  closeTime: Option[String] = None,
  isClosed: Boolean = false
)
object CreateBusinessHoursRequest:
  given JsonDecoder[CreateBusinessHoursRequest] = DeriveJsonDecoder.gen

case class UpdateBusinessHoursRequest(
  openTime: Option[String] = None,
  closeTime: Option[String] = None,
  isClosed: Option[Boolean] = None
)
object UpdateBusinessHoursRequest:
  given JsonDecoder[UpdateBusinessHoursRequest] = DeriveJsonDecoder.gen
