package com.alpha.dto

import zio.json.*

case class ApiError(
  code: String,
  message: String,
  details: Option[List[FieldError]] = None
)

case class FieldError(
  field: String,
  message: String
)

object ApiError:
  given JsonEncoder[FieldError] = DeriveJsonEncoder.gen
  given JsonDecoder[FieldError] = DeriveJsonDecoder.gen
  given JsonEncoder[ApiError]   = DeriveJsonEncoder.gen
  given JsonDecoder[ApiError]   = DeriveJsonDecoder.gen

  def badRequest(message: String): ApiError =
    ApiError("BAD_REQUEST", message)

  def validationError(errors: List[com.alpha.validation.ValidationError]): ApiError =
    val fieldErrors = errors.collect {
      case com.alpha.validation.FieldValidationError(f, m) => FieldError(f, m)
    }
    val generalMsg  = errors.collect {
      case com.alpha.validation.GeneralValidationError(m) => m
    }.headOption.getOrElse("Validation failed")
    ApiError("VALIDATION_ERROR", generalMsg, if fieldErrors.nonEmpty then Some(fieldErrors) else None)

  def unauthorized(message: String): ApiError =
    ApiError("UNAUTHORIZED", message)

  def forbidden(message: String): ApiError =
    ApiError("FORBIDDEN", message)

  def notFound(message: String): ApiError =
    ApiError("NOT_FOUND", message)

  def internalError(message: String): ApiError =
    ApiError("INTERNAL_ERROR", message)
