package com.alpha.validation

import zio.*

sealed trait ValidationError:
  def field: String
  def message: String

case class FieldValidationError(field: String, message: String) extends ValidationError
case class GeneralValidationError(message: String)              extends ValidationError:
  def field: String = ""

object Validation:

  def validateEmail(email: String): Either[ValidationError, String] =
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".r
    if email.isEmpty then Left(FieldValidationError("email", "Email is required"))
    else if !emailRegex.matches(email) then Left(FieldValidationError("email", "Invalid email format"))
    else Right(email)

  def validatePassword(password: String): Either[ValidationError, String] =
    if password.isEmpty then Left(FieldValidationError("password", "Password is required"))
    else if password.length < 8 then Left(FieldValidationError("password", "Password must be at least 8 characters"))
    else if !password.exists(_.isUpper) then
      Left(FieldValidationError("password", "Password must contain an uppercase letter"))
    else if !password.exists(_.isLower) then
      Left(FieldValidationError("password", "Password must contain a lowercase letter"))
    else if !password.exists(_.isDigit) then Left(FieldValidationError("password", "Password must contain a digit"))
    else Right(password)

  def validateRequired[T](value: Option[T], field: String): Either[ValidationError, T] =
    value.toRight(FieldValidationError(field, s"$field is required"))

  def validateMinLength(value: String, min: Int, field: String): Either[ValidationError, String] =
    if value.length < min then Left(FieldValidationError(field, s"$field must be at least $min characters"))
    else Right(value)

  def validateMaxLength(value: String, max: Int, field: String): Either[ValidationError, String] =
    if value.length > max then Left(FieldValidationError(field, s"$field must be at most $max characters"))
    else Right(value)

  def validateRange[T](value: T, min: T, max: T, field: String)(using ord: Ordering[T]): Either[ValidationError, T] =
    if ord.lt(value, min) then Left(FieldValidationError(field, s"$field must be at least $min"))
    else if ord.gt(value, max) then Left(FieldValidationError(field, s"$field must be at most $max"))
    else Right(value)

  def validateAll(errors: List[Either[ValidationError, ?]]): Either[List[ValidationError], Unit] =
    val failures = errors.collect { case Left(e) => e }
    if failures.isEmpty then Right(())
    else Left(failures)
