package com.alpha.domain

import zio.test.*
import com.alpha.domain.model.*
import com.alpha.domain.enums.*
import java.time.OffsetDateTime
import java.util.UUID

object DomainSpec extends ZIOSpecDefault:

  override def spec: Spec[Any, Nothing] = suite("Domain Models Spec")(
    suite("User")(
      test("User should create with all fields") {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        val user = User(
          id = id,
          email = "test@example.com",
          passwordHash = "hash123",
          name = Some("Test User"),
          phone = Some("1234567890"),
          role = "ADMIN",
          regionId = Some(UUID.randomUUID()),
          isActive = true,
          createdAt = now,
          updatedAt = Some(now)
        )
        assertTrue(user.id == id) &&
        assertTrue(user.email == "test@example.com") &&
        assertTrue(user.passwordHash == "hash123") &&
        assertTrue(user.name.contains("Test User")) &&
        assertTrue(user.phone.contains("1234567890")) &&
        assertTrue(user.role == "ADMIN") &&
        assertTrue(user.isActive) &&
        assertTrue(user.createdAt == now)
      },
      test("User should allow optional fields to be None") {
        val now = OffsetDateTime.now()
        val user = User(
          id = UUID.randomUUID(),
          email = "test@example.com",
          passwordHash = "hash123",
          name = None,
          phone = None,
          role = "CUSTOMER",
          regionId = None,
          isActive = true,
          createdAt = now,
          updatedAt = None
        )
        assertTrue(user.name.isEmpty) &&
        assertTrue(user.phone.isEmpty) &&
        assertTrue(user.regionId.isEmpty) &&
        assertTrue(user.updatedAt.isEmpty)
      }
    ),
    suite("Business")(
      test("Business should create with all fields") {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        val business = Business(
          id = id,
          userId = UUID.randomUUID(),
          name = "Test Business",
          slug = "test-business",
          description = Some("A test business"),
          email = Some("biz@test.com"),
          phone = Some("555-1234"),
          website = Some("http://test.com"),
          addressLine1 = Some("123 Main St"),
          addressLine2 = None,
          city = Some("Test City"),
          state = Some("TS"),
          zipCode = Some("12345"),
          country = Some("USA"),
          latitude = Some(BigDecimal(40.7128)),
          longitude = Some(BigDecimal(-74.0060)),
          categoryId = UUID.randomUUID(),
          regionId = UUID.randomUUID(),
          verificationStatus = "PENDING",
          isActive = true,
          isFeatured = false,
          logoUrl = None,
          coverImageUrl = None,
          servicePointsCount = 0,
          createdAt = now,
          updatedAt = None
        )
        assertTrue(business.id == id) &&
        assertTrue(business.name == "Test Business") &&
        assertTrue(business.slug == "test-business") &&
        assertTrue(business.verificationStatus == "PENDING") &&
        assertTrue(business.isActive) &&
        assertTrue(!business.isFeatured)
      }
    ),
    suite("Category")(
      test("Category should create with all fields") {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        val category = Category(
          id = id,
          name = "Restaurants",
          slug = "restaurants",
          description = Some("Food places"),
          icon = Some("restaurant-icon"),
          parentId = None,
          sortOrder = 1,
          isActive = true,
          createdAt = now
        )
        assertTrue(category.id == id) &&
        assertTrue(category.name == "Restaurants") &&
        assertTrue(category.slug == "restaurants") &&
        assertTrue(category.sortOrder == 1) &&
        assertTrue(category.isActive)
      }
    ),
    suite("Region")(
      test("Region should create with all fields") {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        val region = Region(
          id = id,
          name = "New York",
          code = "NY",
          country = "USA",
          timezone = "America/New_York",
          isActive = true,
          createdAt = now
        )
        assertTrue(region.id == id) &&
        assertTrue(region.name == "New York") &&
        assertTrue(region.code == "NY") &&
        assertTrue(region.country == "USA") &&
        assertTrue(region.timezone == "America/New_York")
      }
    ),
    suite("Appointment")(
      test("Appointment should create with all fields") {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        val appointment = Appointment(
          id = id,
          businessId = UUID.randomUUID(),
          userId = Some(UUID.randomUUID()),
          serviceId = None,
          appointmentDate = java.time.LocalDate.now(),
          startTime = java.time.LocalTime.of(9, 0),
          endTime = java.time.LocalTime.of(10, 0),
          servicePointNumber = Some(1),
          customerName = "John Doe",
          customerEmail = "john@example.com",
          customerPhone = Some("555-1234"),
          customerNotes = None,
          status = "PENDING",
          createdAt = now,
          updatedAt = None
        )
        assertTrue(appointment.id == id) &&
        assertTrue(appointment.customerName == "John Doe") &&
        assertTrue(appointment.status == "PENDING") &&
        assertTrue(appointment.servicePointNumber.contains(1))
      }
    ),
    suite("Session")(
      test("Session should create with all fields") {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        val session = Session(
          id = id,
          userId = UUID.randomUUID(),
          refreshToken = "token123",
          expiresAt = now.plusDays(7),
          isRevoked = false,
          createdAt = now
        )
        assertTrue(session.id == id) &&
        assertTrue(session.refreshToken == "token123") &&
        assertTrue(!session.isRevoked) &&
        assertTrue(session.expiresAt.isAfter(now))
      }
    ),
    suite("Enums")(
      test("UserRole should have correct values") {
        assertTrue(UserRole.ADMIN.value == "ADMIN") &&
        assertTrue(UserRole.CUSTOMER.value == "CUSTOMER") &&
        assertTrue(UserRole.BUSINESS_OWNER.value == "BUSINESS_OWNER")
      },
      test("VerificationStatus should have correct values") {
        assertTrue(VerificationStatus.PENDING.value == "PENDING") &&
        assertTrue(VerificationStatus.VERIFIED.value == "VERIFIED") &&
        assertTrue(VerificationStatus.REJECTED.value == "REJECTED")
      },
      test("AppointmentStatus should have correct values") {
        assertTrue(AppointmentStatus.PENDING.value == "PENDING") &&
        assertTrue(AppointmentStatus.CONFIRMED.value == "CONFIRMED") &&
        assertTrue(AppointmentStatus.COMPLETED.value == "COMPLETED") &&
        assertTrue(AppointmentStatus.CANCELLED.value == "CANCELLED")
      }
    )
  )
