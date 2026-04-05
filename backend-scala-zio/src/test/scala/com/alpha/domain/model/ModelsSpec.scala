package com.alpha.domain.model

import zio.test.*
import zio.json.*
import java.time.{LocalDate, LocalTime, OffsetDateTime}
import java.util.UUID

object ModelsSpec extends ZIOSpecDefault:

  private val now = OffsetDateTime.parse("2024-01-15T10:30:00Z")
  private val id1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
  private val id2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
  private val id3 = UUID.fromString("33333333-3333-3333-3333-333333333333")

  override def spec = suite("ModelsSpec")(
    suite("User")(
      test("encodes and decodes full user") {
        val user    = User(
          id = id1,
          email = "test@example.com",
          passwordHash = "hashed_pw",
          name = Some("Test User"),
          phone = Some("+1234567890"),
          role = "CUSTOMER",
          regionId = Some(id2),
          isActive = true,
          isBanned = false,
          emailVerified = true,
          googleId = None,
          avatarUrl = Some("https://example.com/avatar.jpg"),
          createdAt = now,
          updatedAt = Some(now)
        )
        val json    = user.toJson
        val decoded = json.fromJson[User]
        assertTrue(decoded == Right(user))
      },
      test("encodes and decodes user with all None fields") {
        val user    = User(
          id = id1,
          email = "minimal@example.com",
          passwordHash = "hash",
          name = None,
          phone = None,
          role = "ADMIN",
          regionId = None,
          isActive = true,
          isBanned = false,
          emailVerified = false,
          googleId = None,
          avatarUrl = None,
          createdAt = now,
          updatedAt = None
        )
        val decoded = user.toJson.fromJson[User]
        assertTrue(decoded == Right(user))
      },
      test("decoded user has correct email") {
        val user    = User(
          id = id1,
          email = "user@test.com",
          passwordHash = "h",
          name = None,
          phone = None,
          role = "CUSTOMER",
          regionId = None,
          isActive = true,
          isBanned = false,
          emailVerified = false,
          googleId = None,
          avatarUrl = None,
          createdAt = now,
          updatedAt = None
        )
        val decoded = user.toJson.fromJson[User]
        assertTrue(decoded.map(_.email) == Right("user@test.com"))
      }
    ),
    suite("Business")(
      test("encodes and decodes full business") {
        val business = Business(
          id = id1,
          userId = id2,
          name = "Test Business",
          slug = "test-business",
          description = Some("A test business"),
          email = Some("biz@test.com"),
          phone = Some("1234567890"),
          website = Some("https://test.com"),
          addressLine1 = Some("123 Main St"),
          addressLine2 = Some("Suite 100"),
          city = Some("Test City"),
          state = Some("TS"),
          zipCode = Some("12345"),
          country = Some("US"),
          latitude = Some(BigDecimal("40.7128")),
          longitude = Some(BigDecimal("-74.0060")),
          categoryId = id3,
          regionId = id1,
          verificationStatus = "APPROVED",
          isVerified = true,
          isActive = true,
          isFeatured = false,
          logoUrl = Some("https://test.com/logo.png"),
          coverImageUrl = Some("https://test.com/cover.png"),
          servicePointsCount = 5,
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded  = business.toJson.fromJson[Business]
        assertTrue(decoded == Right(business))
      },
      test("encodes and decodes business with minimal fields") {
        val business = Business(
          id = id1,
          userId = id2,
          name = "Minimal Biz",
          slug = "minimal-biz",
          description = None,
          email = None,
          phone = None,
          website = None,
          addressLine1 = None,
          addressLine2 = None,
          city = None,
          state = None,
          zipCode = None,
          country = None,
          latitude = None,
          longitude = None,
          categoryId = id3,
          regionId = id1,
          verificationStatus = "PENDING",
          isVerified = false,
          isActive = true,
          isFeatured = false,
          logoUrl = None,
          coverImageUrl = None,
          servicePointsCount = 0,
          createdAt = now,
          updatedAt = None
        )
        val decoded  = business.toJson.fromJson[Business]
        assertTrue(decoded == Right(business))
      }
    ),
    suite("Category")(
      test("encodes and decodes category with parent") {
        val category = Category(
          id = id1,
          name = "Restaurants",
          slug = "restaurants",
          description = Some("Dining places"),
          icon = Some("restaurant"),
          parentId = Some(id2),
          sortOrder = 1,
          isActive = true,
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded  = category.toJson.fromJson[Category]
        assertTrue(decoded == Right(category))
      },
      test("encodes and decodes root category") {
        val category = Category(
          id = id1,
          name = "Root",
          slug = "root",
          description = None,
          icon = None,
          parentId = None,
          sortOrder = 0,
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        val decoded  = category.toJson.fromJson[Category]
        assertTrue(decoded == Right(category))
      }
    ),
    suite("Region")(
      test("encodes and decodes region") {
        val region  = Region(
          id = id1,
          name = "New York",
          code = "NY",
          country = "US",
          timezone = "America/New_York",
          isActive = true,
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded = region.toJson.fromJson[Region]
        assertTrue(decoded == Right(region))
      },
      test("encodes and decodes inactive region") {
        val region  = Region(
          id = id1,
          name = "Old Region",
          code = "OR",
          country = "US",
          timezone = "UTC",
          isActive = false,
          createdAt = now,
          updatedAt = None
        )
        val decoded = region.toJson.fromJson[Region]
        assertTrue(decoded == Right(region))
      }
    ),
    suite("BusinessHours")(
      test("encodes and decodes open hours") {
        val hours   = BusinessHours(
          id = id1,
          businessId = id2,
          dayOfWeek = 1,
          openTime = Some("09:00"),
          closeTime = Some("17:00"),
          isClosed = false
        )
        val decoded = hours.toJson.fromJson[BusinessHours]
        assertTrue(decoded == Right(hours))
      },
      test("encodes and decodes closed day") {
        val hours   = BusinessHours(
          id = id1,
          businessId = id2,
          dayOfWeek = 0,
          openTime = None,
          closeTime = None,
          isClosed = true
        )
        val decoded = hours.toJson.fromJson[BusinessHours]
        assertTrue(decoded == Right(hours))
      }
    ),
    suite("Review")(
      test("encodes and decodes full review") {
        val review  = Review(
          id = id1,
          businessId = id2,
          userId = id3,
          appointmentId = Some(id1),
          rating = 5,
          title = Some("Great service"),
          comment = Some("Highly recommended"),
          isApproved = true,
          isFeatured = false,
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded = review.toJson.fromJson[Review]
        assertTrue(decoded == Right(review))
      },
      test("encodes and decodes review without appointment") {
        val review  = Review(
          id = id1,
          businessId = id2,
          userId = id3,
          appointmentId = None,
          rating = 3,
          title = None,
          comment = Some("Average"),
          isApproved = false,
          isFeatured = false,
          createdAt = now,
          updatedAt = None
        )
        val decoded = review.toJson.fromJson[Review]
        assertTrue(decoded == Right(review))
      }
    ),
    suite("Appointment")(
      test("encodes and decodes full appointment") {
        val appt    = Appointment(
          id = id1,
          businessId = id2,
          userId = Some(id3),
          serviceId = Some(id1),
          appointmentDate = LocalDate.of(2024, 6, 15),
          startTime = LocalTime.of(10, 0),
          endTime = LocalTime.of(11, 0),
          servicePointNumber = Some(1),
          customerName = "John Doe",
          customerEmail = "john@example.com",
          customerPhone = Some("1234567890"),
          customerNotes = Some("Please be on time"),
          status = "CONFIRMED",
          cancelledAt = None,
          cancelledReason = None,
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded = appt.toJson.fromJson[Appointment]
        assertTrue(decoded == Right(appt))
      },
      test("encodes and decodes cancelled appointment") {
        val appt    = Appointment(
          id = id1,
          businessId = id2,
          userId = None,
          serviceId = None,
          appointmentDate = LocalDate.of(2024, 6, 15),
          startTime = LocalTime.of(14, 0),
          endTime = LocalTime.of(15, 0),
          servicePointNumber = None,
          customerName = "Jane Smith",
          customerEmail = "jane@example.com",
          customerPhone = None,
          customerNotes = None,
          status = "CANCELLED",
          cancelledAt = Some(now),
          cancelledReason = Some("Customer request"),
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded = appt.toJson.fromJson[Appointment]
        assertTrue(decoded == Right(appt))
      }
    ),
    suite("AvailabilitySlot")(
      test("encodes and decodes available slot") {
        val slot    = AvailabilitySlot(
          startTime = LocalTime.of(9, 0),
          endTime = LocalTime.of(10, 0),
          servicePointNumber = Some(1),
          isAvailable = true
        )
        val decoded = slot.toJson.fromJson[AvailabilitySlot]
        assertTrue(decoded == Right(slot))
      },
      test("encodes and decodes unavailable slot without service point") {
        val slot    = AvailabilitySlot(
          startTime = LocalTime.of(12, 0),
          endTime = LocalTime.of(13, 0),
          servicePointNumber = None,
          isAvailable = false
        )
        val decoded = slot.toJson.fromJson[AvailabilitySlot]
        assertTrue(decoded == Right(slot))
      }
    ),
    suite("Service")(
      test("encodes and decodes service with price") {
        val service = Service(
          id = id1,
          businessId = id2,
          name = "Haircut",
          description = Some("Standard haircut"),
          durationMinutes = 30,
          price = Some(BigDecimal("25.00")),
          isActive = true,
          sortOrder = 1,
          createdAt = now,
          updatedAt = Some(now)
        )
        val decoded = service.toJson.fromJson[Service]
        assertTrue(decoded == Right(service))
      },
      test("encodes and decodes service without price") {
        val service = Service(
          id = id1,
          businessId = id2,
          name = "Consultation",
          description = None,
          durationMinutes = 60,
          price = None,
          isActive = true,
          sortOrder = 0,
          createdAt = now,
          updatedAt = None
        )
        val decoded = service.toJson.fromJson[Service]
        assertTrue(decoded == Right(service))
      }
    ),
    suite("EmailVerification")(
      test("encodes and decodes email verification") {
        val ev      = EmailVerification(
          id = id1,
          userId = id2,
          token = "abc123",
          expiresAt = now.plusDays(1),
          isUsed = false,
          createdAt = now
        )
        val decoded = ev.toJson.fromJson[EmailVerification]
        assertTrue(decoded == Right(ev))
      },
      test("encodes and decodes used email verification") {
        val ev      = EmailVerification(
          id = id1,
          userId = id2,
          token = "used-token",
          expiresAt = now,
          isUsed = true,
          createdAt = now.minusDays(1)
        )
        val decoded = ev.toJson.fromJson[EmailVerification]
        assertTrue(decoded == Right(ev))
      }
    ),
    suite("PasswordReset")(
      test("encodes and decodes password reset") {
        val pr      = PasswordReset(
          id = id1,
          userId = id2,
          token = "reset-token-xyz",
          expiresAt = now.plusHours(1),
          isUsed = false,
          createdAt = now
        )
        val decoded = pr.toJson.fromJson[PasswordReset]
        assertTrue(decoded == Right(pr))
      },
      test("encodes and decodes used password reset") {
        val pr      = PasswordReset(
          id = id1,
          userId = id2,
          token = "old-reset",
          expiresAt = now.minusHours(1),
          isUsed = true,
          createdAt = now.minusDays(1)
        )
        val decoded = pr.toJson.fromJson[PasswordReset]
        assertTrue(decoded == Right(pr))
      }
    ),
    suite("UserSession")(
      test("encodes and decodes active session") {
        val session = UserSession(
          id = id1,
          userId = id2,
          refreshToken = "refresh-token-abc",
          token = "access-token-xyz",
          userAgent = Some("Mozilla/5.0"),
          ipAddress = Some("192.168.1.1"),
          expiresAt = now.plusDays(7),
          isRevoked = false,
          createdAt = now
        )
        val decoded = session.toJson.fromJson[UserSession]
        assertTrue(decoded == Right(session))
      },
      test("encodes and decodes revoked session") {
        val session = UserSession(
          id = id1,
          userId = id2,
          refreshToken = "old-refresh",
          token = "old-access",
          userAgent = None,
          ipAddress = None,
          expiresAt = now,
          isRevoked = true,
          createdAt = now.minusDays(7)
        )
        val decoded = session.toJson.fromJson[UserSession]
        assertTrue(decoded == Right(session))
      }
    )
  )
