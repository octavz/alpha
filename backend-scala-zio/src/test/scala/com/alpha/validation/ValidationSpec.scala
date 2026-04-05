package com.alpha.validation

import zio.test.*

object ValidationSpec extends ZIOSpecDefault:

  override def spec = suite("ValidationSpec")(
    suite("validateEmail")(
      test("accepts valid email") {
        val result = Validation.validateEmail("test@example.com")
        assertTrue(result == Right("test@example.com"))
      },
      test("accepts email with subdomain") {
        val result = Validation.validateEmail("user@mail.example.com")
        assertTrue(result.isRight)
      },
      test("accepts email with plus sign") {
        val result = Validation.validateEmail("user+tag@example.com")
        assertTrue(result.isRight)
      },
      test("accepts email with hyphens") {
        val result = Validation.validateEmail("first-last@example.com")
        assertTrue(result.isRight)
      },
      test("rejects empty email") {
        val result = Validation.validateEmail("")
        assertTrue(result == Left(FieldValidationError("email", "Email is required")))
      },
      test("rejects email without @") {
        val result = Validation.validateEmail("invalid-email")
        assertTrue(result == Left(FieldValidationError("email", "Invalid email format")))
      },
      test("rejects email without domain") {
        val result = Validation.validateEmail("user@")
        assertTrue(result.isLeft)
      },
      test("rejects email without local part") {
        val result = Validation.validateEmail("@example.com")
        assertTrue(result.isLeft)
      }
    ),
    suite("validatePassword")(
      test("accepts valid password with all requirements") {
        val result = Validation.validatePassword("Password1")
        assertTrue(result == Right("Password1"))
      },
      test("rejects empty password") {
        val result = Validation.validatePassword("")
        assertTrue(result == Left(FieldValidationError("password", "Password is required")))
      },
      test("rejects password shorter than 8 characters") {
        val result = Validation.validatePassword("Short1")
        assertTrue(result == Left(FieldValidationError("password", "Password must be at least 8 characters")))
      },
      test("rejects password without uppercase") {
        val result = Validation.validatePassword("password1")
        assertTrue(result == Left(FieldValidationError("password", "Password must contain an uppercase letter")))
      },
      test("rejects password without lowercase") {
        val result = Validation.validatePassword("PASSWORD1")
        assertTrue(result == Left(FieldValidationError("password", "Password must contain a lowercase letter")))
      },
      test("rejects password without digit") {
        val result = Validation.validatePassword("Password")
        assertTrue(result == Left(FieldValidationError("password", "Password must contain a digit")))
      },
      test("accepts password with special characters") {
        val result = Validation.validatePassword("P@ssw0rd!")
        assertTrue(result.isRight)
      },
      test("accepts exactly 8 character password meeting all requirements") {
        val result = Validation.validatePassword("Abcdefg1")
        assertTrue(result.isRight)
      }
    ),
    suite("validateRequired")(
      test("returns value when Some") {
        val result = Validation.validateRequired(Some("value"), "field")
        assertTrue(result == Right("value"))
      },
      test("returns error when None") {
        val result = Validation.validateRequired(None, "name")
        assertTrue(result == Left(FieldValidationError("name", "name is required")))
      },
      test("works with integer values") {
        val result = Validation.validateRequired(Some(42), "count")
        assertTrue(result == Right(42))
      }
    ),
    suite("validateMinLength")(
      test("accepts string meeting minimum length") {
        val result = Validation.validateMinLength("hello", 3, "name")
        assertTrue(result == Right("hello"))
      },
      test("accepts string exactly at minimum length") {
        val result = Validation.validateMinLength("abc", 3, "name")
        assertTrue(result == Right("abc"))
      },
      test("rejects string below minimum length") {
        val result = Validation.validateMinLength("ab", 3, "name")
        assertTrue(result == Left(FieldValidationError("name", "name must be at least 3 characters")))
      },
      test("accepts empty string when min is 0") {
        val result = Validation.validateMinLength("", 0, "name")
        assertTrue(result == Right(""))
      }
    ),
    suite("validateMaxLength")(
      test("accepts string within maximum length") {
        val result = Validation.validateMaxLength("hi", 10, "name")
        assertTrue(result == Right("hi"))
      },
      test("accepts string exactly at maximum length") {
        val result = Validation.validateMaxLength("abc", 3, "name")
        assertTrue(result == Right("abc"))
      },
      test("rejects string exceeding maximum length") {
        val result = Validation.validateMaxLength("abcdef", 3, "name")
        assertTrue(result == Left(FieldValidationError("name", "name must be at most 3 characters")))
      }
    ),
    suite("validateRange")(
      test("accepts value within range") {
        val result = Validation.validateRange(5, 1, 10, "age")
        assertTrue(result == Right(5))
      },
      test("accepts value at minimum boundary") {
        val result = Validation.validateRange(1, 1, 10, "age")
        assertTrue(result == Right(1))
      },
      test("accepts value at maximum boundary") {
        val result = Validation.validateRange(10, 1, 10, "age")
        assertTrue(result == Right(10))
      },
      test("rejects value below minimum") {
        val result = Validation.validateRange(0, 1, 10, "age")
        assertTrue(result == Left(FieldValidationError("age", "age must be at least 1")))
      },
      test("rejects value above maximum") {
        val result = Validation.validateRange(11, 1, 10, "age")
        assertTrue(result == Left(FieldValidationError("age", "age must be at most 10")))
      },
      test("works with BigDecimal values") {
        val result = Validation.validateRange(BigDecimal("5.5"), BigDecimal("1.0"), BigDecimal("10.0"), "price")
        assertTrue(result.isRight)
      }
    ),
    suite("validateAll")(
      test("returns Right when all validations pass") {
        val results = List(
          Right("a"),
          Right("b"),
          Right("c")
        )
        val result = Validation.validateAll(results)
        assertTrue(result == Right(()))
      },
      test("returns Left with all errors when validations fail") {
        val results: List[Either[ValidationError, String]] = List(
          Left(FieldValidationError("field1", "error1")),
          Right("ok"),
          Left(FieldValidationError("field2", "error2"))
        )
        val result = Validation.validateAll(results)
        assertTrue(result.isLeft)
        assertTrue(result.swap.getOrElse(Nil).length == 2)
      },
      test("returns Right for empty list") {
        val results = List.empty[Either[ValidationError, String]]
        val result = Validation.validateAll(results)
        assertTrue(result == Right(()))
      },
      test("returns Left with single error") {
        val results: List[Either[ValidationError, String]] = List(
          Left(FieldValidationError("email", "required"))
        )
        val result = Validation.validateAll(results)
        assertTrue(result.swap.getOrElse(Nil).head.field == "email")
      }
    ),
    suite("ValidationError types")(
      test("FieldValidationError has correct field and message") {
        val error = FieldValidationError("email", "Invalid format")
        assertTrue(error.field == "email" && error.message == "Invalid format")
      },
      test("GeneralValidationError has empty field") {
        val error = GeneralValidationError("Something went wrong")
        assertTrue(error.field == "" && error.message == "Something went wrong")
      }
    )
  )
