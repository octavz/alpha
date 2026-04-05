package com.alpha.dto

import zio.test.*
import zio.json.*
import java.util.UUID
import java.time.{LocalDate, LocalTime}

object DtoModelsSpec extends ZIOSpecDefault:

  private val uuid1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
  private val uuid2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")

  override def spec = suite("DtoModelsSpec")(
    suite("RegisterUserRequest")(
      test("round-trip encode/decode with all fields") {
        val req = RegisterUserRequest(
          email = "new@example.com", password = "SecurePass1",
          name = Some("New User"), phone = Some("1234567890"),
          role = Some("CUSTOMER"), regionId = Some(uuid1)
        )
        assertTrue(req.toJson.fromJson[RegisterUserRequest] == Right(req))
      },
      test("round-trip encode/decode with minimal fields") {
        val req = RegisterUserRequest(email = "min@example.com", password = "Password1")
        assertTrue(req.toJson.fromJson[RegisterUserRequest] == Right(req))
      }
    ),
    suite("LoginUserRequest")(
      test("round-trip encode/decode") {
        val req = LoginUserRequest(email = "user@example.com", password = "Password1")
        assertTrue(req.toJson.fromJson[LoginUserRequest] == Right(req))
      }
    ),
    suite("RefreshTokenRequest")(
      test("round-trip encode/decode") {
        val req = RefreshTokenRequest(refreshToken = "some-refresh-token")
        assertTrue(req.toJson.fromJson[RefreshTokenRequest] == Right(req))
      }
    ),
    suite("VerifyEmailRequest")(
      test("round-trip encode/decode") {
        val req = VerifyEmailRequest(token = "verification-token-abc")
        assertTrue(req.toJson.fromJson[VerifyEmailRequest] == Right(req))
      }
    ),
    suite("ForgotPasswordRequest")(
      test("round-trip encode/decode") {
        val req = ForgotPasswordRequest(email = "user@example.com")
        assertTrue(req.toJson.fromJson[ForgotPasswordRequest] == Right(req))
      }
    ),
    suite("ResetPasswordRequest")(
      test("round-trip encode/decode") {
        val req = ResetPasswordRequest(token = "reset-token", newPassword = "NewPass1")
        assertTrue(req.toJson.fromJson[ResetPasswordRequest] == Right(req))
      }
    ),
    suite("ChangePasswordRequest")(
      test("round-trip encode/decode") {
        val req = ChangePasswordRequest(currentPassword = "OldPass1", newPassword = "NewPass1")
        assertTrue(req.toJson.fromJson[ChangePasswordRequest] == Right(req))
      }
    ),
    suite("UpdateProfileRequest")(
      test("round-trip encode/decode with all fields") {
        val req = UpdateProfileRequest(
          name = Some("Updated Name"),
          phone = Some("9876543210"),
          avatarUrl = Some("https://example.com/new-avatar.jpg")
        )
        assertTrue(req.toJson.fromJson[UpdateProfileRequest] == Right(req))
      },
      test("round-trip encode/decode with all None") {
        val req = UpdateProfileRequest(name = None, phone = None, avatarUrl = None)
        assertTrue(req.toJson.fromJson[UpdateProfileRequest] == Right(req))
      }
    ),
    suite("CreateBusinessRequest")(
      test("round-trip encode/decode with all fields") {
        val req = CreateBusinessRequest(
          name = "New Business", slug = Some("new-business"),
          description = Some("A new business"), email = Some("biz@new.com"),
          phone = Some("1234567890"), website = Some("https://new.com"),
          addressLine1 = Some("123 Main St"), addressLine2 = Some("Suite 1"),
          city = Some("New City"), state = Some("NS"), zipCode = Some("12345"),
          country = Some("US"), latitude = Some(BigDecimal("40.0")),
          longitude = Some(BigDecimal("-73.0")), categoryId = uuid1,
          regionId = uuid2
        )
        assertTrue(req.toJson.fromJson[CreateBusinessRequest] == Right(req))
      },
      test("round-trip encode/decode with required fields only") {
        val req = CreateBusinessRequest(
          name = "Minimal Business", categoryId = uuid1, regionId = uuid2
        )
        assertTrue(req.toJson.fromJson[CreateBusinessRequest] == Right(req))
      }
    ),
    suite("UpdateBusinessRequest")(
      test("round-trip encode/decode with all fields") {
        val req = UpdateBusinessRequest(
          name = Some("Updated Business"), description = Some("Updated desc"),
          email = Some("updated@biz.com"), phone = Some("0987654321"),
          website = Some("https://updated.com"), addressLine1 = Some("456 Oak Ave"),
          addressLine2 = Some("Floor 2"), city = Some("Updated City"),
          state = Some("US"), zipCode = Some("54321"), country = Some("US"),
          latitude = Some(BigDecimal("41.0")), longitude = Some(BigDecimal("-72.0")),
          categoryId = Some(uuid1), regionId = Some(uuid2),
          logoUrl = Some("https://updated.com/logo.png"),
          coverImageUrl = Some("https://updated.com/cover.png")
        )
        assertTrue(req.toJson.fromJson[UpdateBusinessRequest] == Right(req))
      },
      test("round-trip encode/decode with single field") {
        val req = UpdateBusinessRequest(name = Some("New Name"))
        assertTrue(req.toJson.fromJson[UpdateBusinessRequest] == Right(req))
      }
    ),
    suite("CreateCategoryRequest")(
      test("round-trip encode/decode with all fields") {
        val req = CreateCategoryRequest(
          name = "New Category", slug = Some("new-category"),
          description = Some("Category description"), icon = Some("icon-star"),
          parentId = Some(uuid1), sortOrder = 5
        )
        assertTrue(req.toJson.fromJson[CreateCategoryRequest] == Right(req))
      },
      test("round-trip encode/decode with minimal fields") {
        val req = CreateCategoryRequest(name = "Minimal Category")
        assertTrue(req.toJson.fromJson[CreateCategoryRequest] == Right(req))
      }
    ),
    suite("UpdateCategoryRequest")(
      test("round-trip encode/decode with all fields") {
        val req = UpdateCategoryRequest(
          name = Some("Updated Category"), description = Some("Updated desc"),
          icon = Some("new-icon"), parentId = Some(uuid1),
          sortOrder = Some(10), isActive = Some(false)
        )
        assertTrue(req.toJson.fromJson[UpdateCategoryRequest] == Right(req))
      },
      test("round-trip encode/decode with all None") {
        val req = UpdateCategoryRequest()
        assertTrue(req.toJson.fromJson[UpdateCategoryRequest] == Right(req))
      }
    ),
    suite("CreateRegionRequest")(
      test("round-trip encode/decode") {
        val req = CreateRegionRequest(
          name = "New Region", code = "NR", country = "US", timezone = "America/Chicago"
        )
        assertTrue(req.toJson.fromJson[CreateRegionRequest] == Right(req))
      }
    ),
    suite("UpdateRegionRequest")(
      test("round-trip encode/decode with all fields") {
        val req = UpdateRegionRequest(
          name = Some("Updated Region"), code = Some("UR"),
          country = Some("CA"), timezone = Some("America/Toronto"),
          isActive = Some(true)
        )
        assertTrue(req.toJson.fromJson[UpdateRegionRequest] == Right(req))
      },
      test("round-trip encode/decode with all None") {
        val req = UpdateRegionRequest()
        assertTrue(req.toJson.fromJson[UpdateRegionRequest] == Right(req))
      }
    ),
    suite("CreateAppointmentRequest")(
      test("round-trip encode/decode with all fields") {
        val req = CreateAppointmentRequest(
          businessId = uuid1, userId = Some(uuid2),
          serviceId = Some(uuid1), appointmentDate = LocalDate.of(2024, 7, 20),
          startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 30),
          servicePointNumber = Some(2), customerName = "John Doe",
          customerEmail = "john@example.com", customerPhone = Some("1234567890"),
          customerNotes = Some("Please arrive early")
        )
        assertTrue(req.toJson.fromJson[CreateAppointmentRequest] == Right(req))
      },
      test("round-trip encode/decode with required fields only") {
        val req = CreateAppointmentRequest(
          businessId = uuid1, appointmentDate = LocalDate.of(2024, 7, 20),
          startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
          customerName = "Jane", customerEmail = "jane@example.com"
        )
        assertTrue(req.toJson.fromJson[CreateAppointmentRequest] == Right(req))
      }
    ),
    suite("UpdateAppointmentRequest")(
      test("round-trip encode/decode with both fields") {
        val req = UpdateAppointmentRequest(
          status = Some("CONFIRMED"), customerNotes = Some("Updated notes")
        )
        assertTrue(req.toJson.fromJson[UpdateAppointmentRequest] == Right(req))
      },
      test("round-trip encode/decode with all None") {
        val req = UpdateAppointmentRequest()
        assertTrue(req.toJson.fromJson[UpdateAppointmentRequest] == Right(req))
      }
    ),
    suite("CancelAppointmentRequest")(
      test("round-trip encode/decode with reason") {
        val req = CancelAppointmentRequest(reason = Some("Customer cancelled"))
        assertTrue(req.toJson.fromJson[CancelAppointmentRequest] == Right(req))
      },
      test("round-trip encode/decode without reason") {
        val req = CancelAppointmentRequest()
        assertTrue(req.toJson.fromJson[CancelAppointmentRequest] == Right(req))
      }
    ),
    suite("CreateReviewRequest")(
      test("round-trip encode/decode with all fields") {
        val req = CreateReviewRequest(
          businessId = uuid1, userId = uuid2,
          appointmentId = Some(uuid1), rating = 5,
          title = Some("Excellent"), comment = Some("Great experience")
        )
        assertTrue(req.toJson.fromJson[CreateReviewRequest] == Right(req))
      },
      test("round-trip encode/decode with required fields only") {
        val req = CreateReviewRequest(
          businessId = uuid1, userId = uuid2, rating = 4
        )
        assertTrue(req.toJson.fromJson[CreateReviewRequest] == Right(req))
      }
    ),
    suite("CreateServiceRequest")(
      test("round-trip encode/decode with all fields") {
        val req = CreateServiceRequest(
          name = "Premium Service", description = Some("Top tier service"),
          durationMinutes = 90, price = Some(BigDecimal("99.99")),
          isActive = true, sortOrder = 1
        )
        assertTrue(req.toJson.fromJson[CreateServiceRequest] == Right(req))
      },
      test("round-trip encode/decode with required fields only") {
        val req = CreateServiceRequest(name = "Basic Service", durationMinutes = 30)
        assertTrue(req.toJson.fromJson[CreateServiceRequest] == Right(req))
      }
    ),
    suite("UpdateServiceRequest")(
      test("round-trip encode/decode with all fields") {
        val req = UpdateServiceRequest(
          name = Some("Updated Service"), description = Some("Updated desc"),
          durationMinutes = Some(60), price = Some(BigDecimal("50.00")),
          isActive = Some(false), sortOrder = Some(5)
        )
        assertTrue(req.toJson.fromJson[UpdateServiceRequest] == Right(req))
      },
      test("round-trip encode/decode with all None") {
        val req = UpdateServiceRequest()
        assertTrue(req.toJson.fromJson[UpdateServiceRequest] == Right(req))
      }
    ),
    suite("CreateBusinessHoursRequest")(
      test("round-trip encode/decode with open hours") {
        val req = CreateBusinessHoursRequest(
          businessId = uuid1, dayOfWeek = 1,
          openTime = Some("09:00"), closeTime = Some("17:00"),
          isClosed = false
        )
        assertTrue(req.toJson.fromJson[CreateBusinessHoursRequest] == Right(req))
      },
      test("round-trip encode/decode for closed day") {
        val req = CreateBusinessHoursRequest(
          businessId = uuid1, dayOfWeek = 0, isClosed = true
        )
        assertTrue(req.toJson.fromJson[CreateBusinessHoursRequest] == Right(req))
      }
    ),
    suite("UpdateBusinessHoursRequest")(
      test("round-trip encode/decode with all fields") {
        val req = UpdateBusinessHoursRequest(
          openTime = Some("08:00"), closeTime = Some("18:00"),
          isClosed = Some(false)
        )
        assertTrue(req.toJson.fromJson[UpdateBusinessHoursRequest] == Right(req))
      },
      test("round-trip encode/decode with all None") {
        val req = UpdateBusinessHoursRequest()
        assertTrue(req.toJson.fromJson[UpdateBusinessHoursRequest] == Right(req))
      }
    )
  )
