package com.alpha.dto

import zio.test.*
import zio.json.*
import com.alpha.validation.*
import com.alpha.dto.ApiError.{given_JsonDecoder_FieldError, given_JsonEncoder_FieldError}
import com.alpha.dto.ApiError.{given_JsonDecoder_FieldError, given_JsonEncoder_FieldError}

object ApiErrorSpec extends ZIOSpecDefault:

  override def spec = suite("ApiErrorSpec")(
    suite("factory methods")(
      test("badRequest creates error with BAD_REQUEST code") {
        val error = ApiError.badRequest("Invalid input")
        assertTrue(error.code == "BAD_REQUEST" && error.message == "Invalid input" && error.details.isEmpty)
      },
      test("unauthorized creates error with UNAUTHORIZED code") {
        val error = ApiError.unauthorized("Token expired")
        assertTrue(error.code == "UNAUTHORIZED" && error.message == "Token expired")
      },
      test("forbidden creates error with FORBIDDEN code") {
        val error = ApiError.forbidden("Insufficient permissions")
        assertTrue(error.code == "FORBIDDEN" && error.message == "Insufficient permissions")
      },
      test("notFound creates error with NOT_FOUND code") {
        val error = ApiError.notFound("Resource not found")
        assertTrue(error.code == "NOT_FOUND" && error.message == "Resource not found")
      },
      test("internalError creates error with INTERNAL_ERROR code") {
        val error = ApiError.internalError("Database connection failed")
        assertTrue(error.code == "INTERNAL_ERROR" && error.message == "Database connection failed")
      }
    ),
    suite("validationError")(
      test("converts FieldValidationErrors to details") {
        val errors   = List(
          FieldValidationError("email", "Email is required"),
          FieldValidationError("password", "Password too short")
        )
        val apiError = ApiError.validationError(errors)
        assertTrue(
          apiError.code == "VALIDATION_ERROR",
          apiError.message == "Validation failed",
          apiError.details.isDefined,
          apiError.details.get.length == 2,
          apiError.details.get.head.field == "email",
          apiError.details.get.head.message == "Email is required",
          apiError.details.get(1).field == "password",
          apiError.details.get(1).message == "Password too short"
        )
      },
      test("uses first error message when only GeneralValidationErrors") {
        val errors   = List(
          GeneralValidationError("Something went wrong")
        )
        val apiError = ApiError.validationError(errors)
        assertTrue(
          apiError.code == "VALIDATION_ERROR",
          apiError.message == "Something went wrong",
          apiError.details.isEmpty
        )
      },
      test("returns no details when only GeneralValidationErrors mixed") {
        val errors   = List(
          GeneralValidationError("General error")
        )
        val apiError = ApiError.validationError(errors)
        assertTrue(apiError.details.isEmpty)
      },
      test("returns both details and general message for mixed errors") {
        val errors: List[ValidationError] = List(
          FieldValidationError("name", "Name is required"),
          GeneralValidationError("Additional issue")
        )
        val apiError                      = ApiError.validationError(errors)
        assertTrue(
          apiError.code == "VALIDATION_ERROR",
          apiError.details.isDefined,
          apiError.details.get.length == 1,
          apiError.details.get.head.field == "name"
        )
      }
    ),
    suite("JSON encode/decode")(
      test("encodes ApiError without details") {
        val error = ApiError("NOT_FOUND", "User not found")
        val json  = error.toJson
        assertTrue(json.contains("NOT_FOUND"))
        assertTrue(json.contains("User not found"))
      },
      test("encodes ApiError with details") {
        val error = ApiError(
          "VALIDATION_ERROR",
          "Validation failed",
          Some(List(FieldError("email", "Required"), FieldError("password", "Too short")))
        )
        val json  = error.toJson
        assertTrue(json.contains("VALIDATION_ERROR"))
        assertTrue(json.contains("email"))
        assertTrue(json.contains("password"))
      },
      test("decodes ApiError without details") {
        val json    = """{"code":"BAD_REQUEST","message":"Invalid input"}"""
        val decoded = json.fromJson[ApiError]
        assertTrue(decoded.map(_.code) == Right("BAD_REQUEST"))
        assertTrue(decoded.map(_.message) == Right("Invalid input"))
        assertTrue(decoded.map(_.details) == Right(None))
      },
      test("decodes ApiError with details") {
        val json    =
          """{"code":"VALIDATION_ERROR","message":"Validation failed","details":[{"field":"email","message":"Required"}]}"""
        val decoded = json.fromJson[ApiError]
        assertTrue(decoded.map(_.code) == Right("VALIDATION_ERROR"))
        assertTrue(decoded.map(_.details.isDefined) == Right(true))
        assertTrue(decoded.exists(_.details.exists(_.head.field == "email")))
      },
      test("round-trip encodes and decodes ApiError") {
        val error   = ApiError(
          "INTERNAL_ERROR",
          "Server error",
          None
        )
        val decoded = error.toJson.fromJson[ApiError]
        assertTrue(decoded == Right(error))
      },
      test("round-trip encodes and decodes ApiError with details") {
        val error   = ApiError(
          "VALIDATION_ERROR",
          "Bad data",
          Some(List(FieldError("field1", "err1"), FieldError("field2", "err2")))
        )
        val decoded = error.toJson.fromJson[ApiError]
        assertTrue(decoded == Right(error))
      }
    ),
    suite("FieldError")(
      test("encodes FieldError") {
        val fe   = FieldError("email", "Invalid format")
        val json = fe.toJson
        assertTrue(json.contains("email"))
        assertTrue(json.contains("Invalid format"))
      },
      test("decodes FieldError") {
        val json    = """{"field":"name","message":"Required"}"""
        val decoded = json.fromJson[FieldError]
        assertTrue(decoded.map(_.field) == Right("name"))
        assertTrue(decoded.map(_.message) == Right("Required"))
      },
      test("round-trip FieldError") {
        val fe      = FieldError("phone", "Invalid phone number")
        val decoded = fe.toJson.fromJson[FieldError]
        assertTrue(decoded == Right(fe))
      }
    )
  )
