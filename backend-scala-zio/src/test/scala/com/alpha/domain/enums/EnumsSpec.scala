package com.alpha.domain.enums

import zio.test.*
import zio.json.*

object EnumsSpec extends ZIOSpecDefault:

  override def spec = suite("EnumsSpec")(
    suite("UserRole")(
      test("toJson encodes ADMIN role") {
        val json = UserRole.ADMIN.toJson
        assertTrue(json == "\"ADMIN\"")
      },
      test("toJson encodes CUSTOMER role") {
        val json = UserRole.CUSTOMER.toJson
        assertTrue(json == "\"CUSTOMER\"")
      },
      test("toJson encodes BUSINESS_ADMIN role") {
        val json = UserRole.BUSINESS_ADMIN.toJson
        assertTrue(json == "\"BUSINESS_ADMIN\"")
      },
      test("toJson encodes BUSINESS_STAFF role") {
        val json = UserRole.BUSINESS_STAFF.toJson
        assertTrue(json == "\"BUSINESS_STAFF\"")
      },
      test("fromJson decodes ADMIN role") {
        val result = "\"ADMIN\"".fromJson[UserRole]
        assertTrue(result == Right(UserRole.ADMIN))
      },
      test("fromJson decodes CUSTOMER role") {
        val result = "\"CUSTOMER\"".fromJson[UserRole]
        assertTrue(result == Right(UserRole.CUSTOMER))
      },
      test("fromJson decodes BUSINESS_ADMIN role") {
        val result = "\"BUSINESS_ADMIN\"".fromJson[UserRole]
        assertTrue(result == Right(UserRole.BUSINESS_ADMIN))
      },
      test("fromJson decodes BUSINESS_STAFF role") {
        val result = "\"BUSINESS_STAFF\"".fromJson[UserRole]
        assertTrue(result == Right(UserRole.BUSINESS_STAFF))
      },
      test("fromJson fails on unknown role") {
        val result = "\"UNKNOWN\"".fromJson[UserRole]
        assertTrue(result.isLeft)
      },
      test("value returns correct string for ADMIN") {
        assertTrue(UserRole.ADMIN.value == "ADMIN")
      },
      test("value returns correct string for CUSTOMER") {
        assertTrue(UserRole.CUSTOMER.value == "CUSTOMER")
      },
      test("value returns correct string for BUSINESS_ADMIN") {
        assertTrue(UserRole.BUSINESS_ADMIN.value == "BUSINESS_ADMIN")
      },
      test("value returns correct string for BUSINESS_STAFF") {
        assertTrue(UserRole.BUSINESS_STAFF.value == "BUSINESS_STAFF")
      },
      test("round-trip encode/decode for all roles") {
        val roles = List(UserRole.ADMIN, UserRole.CUSTOMER, UserRole.BUSINESS_ADMIN, UserRole.BUSINESS_STAFF)
        assertTrue(
          UserRole.ADMIN.toJson.fromJson[UserRole] == Right(UserRole.ADMIN),
          UserRole.CUSTOMER.toJson.fromJson[UserRole] == Right(UserRole.CUSTOMER),
          UserRole.BUSINESS_ADMIN.toJson.fromJson[UserRole] == Right(UserRole.BUSINESS_ADMIN),
          UserRole.BUSINESS_STAFF.toJson.fromJson[UserRole] == Right(UserRole.BUSINESS_STAFF)
        )
      }
    ),
    suite("VerificationStatus")(
      test("toJson encodes PENDING status") {
        val json = VerificationStatus.PENDING.toJson
        assertTrue(json == "\"PENDING\"")
      },
      test("toJson encodes APPROVED status") {
        val json = VerificationStatus.APPROVED.toJson
        assertTrue(json == "\"APPROVED\"")
      },
      test("toJson encodes REJECTED status") {
        val json = VerificationStatus.REJECTED.toJson
        assertTrue(json == "\"REJECTED\"")
      },
      test("fromJson decodes PENDING status") {
        val result = "\"PENDING\"".fromJson[VerificationStatus]
        assertTrue(result == Right(VerificationStatus.PENDING))
      },
      test("fromJson decodes APPROVED status") {
        val result = "\"APPROVED\"".fromJson[VerificationStatus]
        assertTrue(result == Right(VerificationStatus.APPROVED))
      },
      test("fromJson decodes REJECTED status") {
        val result = "\"REJECTED\"".fromJson[VerificationStatus]
        assertTrue(result == Right(VerificationStatus.REJECTED))
      },
      test("fromJson fails on unknown status") {
        val result = "\"UNKNOWN\"".fromJson[VerificationStatus]
        assertTrue(result.isLeft)
      },
      test("value returns correct string for PENDING") {
        assertTrue(VerificationStatus.PENDING.value == "PENDING")
      },
      test("value returns correct string for APPROVED") {
        assertTrue(VerificationStatus.APPROVED.value == "APPROVED")
      },
      test("value returns correct string for REJECTED") {
        assertTrue(VerificationStatus.REJECTED.value == "REJECTED")
      },
      test("round-trip encode/decode for all statuses") {
        assertTrue(
          VerificationStatus.PENDING.toJson.fromJson[VerificationStatus] == Right(VerificationStatus.PENDING),
          VerificationStatus.APPROVED.toJson.fromJson[VerificationStatus] == Right(VerificationStatus.APPROVED),
          VerificationStatus.REJECTED.toJson.fromJson[VerificationStatus] == Right(VerificationStatus.REJECTED)
        )
      }
    ),
    suite("AppointmentStatus")(
      test("toJson encodes PENDING status") {
        val json = AppointmentStatus.PENDING.toJson
        assertTrue(json == "\"PENDING\"")
      },
      test("toJson encodes CONFIRMED status") {
        val json = AppointmentStatus.CONFIRMED.toJson
        assertTrue(json == "\"CONFIRMED\"")
      },
      test("toJson encodes COMPLETED status") {
        val json = AppointmentStatus.COMPLETED.toJson
        assertTrue(json == "\"COMPLETED\"")
      },
      test("toJson encodes CANCELLED status") {
        val json = AppointmentStatus.CANCELLED.toJson
        assertTrue(json == "\"CANCELLED\"")
      },
      test("toJson encodes NO_SHOW status") {
        val json = AppointmentStatus.NO_SHOW.toJson
        assertTrue(json == "\"NO_SHOW\"")
      },
      test("fromJson decodes PENDING status") {
        val result = "\"PENDING\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.PENDING))
      },
      test("fromJson decodes CONFIRMED status") {
        val result = "\"CONFIRMED\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.CONFIRMED))
      },
      test("fromJson decodes COMPLETED status") {
        val result = "\"COMPLETED\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.COMPLETED))
      },
      test("fromJson decodes CANCELLED status") {
        val result = "\"CANCELLED\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.CANCELLED))
      },
      test("fromJson decodes NO_SHOW status") {
        val result = "\"NO_SHOW\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.NO_SHOW))
      },
      test("fromJson fails on unknown status") {
        val result = "\"UNKNOWN\"".fromJson[AppointmentStatus]
        assertTrue(result.isLeft)
      },
      test("value returns correct string for PENDING") {
        assertTrue(AppointmentStatus.PENDING.value == "PENDING")
      },
      test("value returns correct string for CONFIRMED") {
        assertTrue(AppointmentStatus.CONFIRMED.value == "CONFIRMED")
      },
      test("value returns correct string for COMPLETED") {
        assertTrue(AppointmentStatus.COMPLETED.value == "COMPLETED")
      },
      test("value returns correct string for CANCELLED") {
        assertTrue(AppointmentStatus.CANCELLED.value == "CANCELLED")
      },
      test("value returns correct string for NO_SHOW") {
        assertTrue(AppointmentStatus.NO_SHOW.value == "NO_SHOW")
      },
      test("round-trip encode/decode for all statuses") {
        assertTrue(
          AppointmentStatus.PENDING.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.PENDING),
          AppointmentStatus.CONFIRMED.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.CONFIRMED),
          AppointmentStatus.COMPLETED.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.COMPLETED),
          AppointmentStatus.CANCELLED.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.CANCELLED),
          AppointmentStatus.NO_SHOW.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.NO_SHOW)
        )
      }
    )
  )
      }
    ),
    suite("VerificationStatus")(
      test("toJson encodes PENDING status") {
        val json = VerificationStatus.PENDING.toJson
        assertTrue(json == "\"PENDING\"")
      },
      test("toJson encodes APPROVED status") {
        val json = VerificationStatus.APPROVED.toJson
        assertTrue(json == "\"APPROVED\"")
      },
      test("toJson encodes REJECTED status") {
        val json = VerificationStatus.REJECTED.toJson
        assertTrue(json == "\"REJECTED\"")
      },
      test("fromJson decodes PENDING status") {
        val result = "\"PENDING\"".fromJson[VerificationStatus]
        assertTrue(result == Right(VerificationStatus.PENDING))
      },
      test("fromJson decodes APPROVED status") {
        val result = "\"APPROVED\"".fromJson[VerificationStatus]
        assertTrue(result == Right(VerificationStatus.APPROVED))
      },
      test("fromJson decodes REJECTED status") {
        val result = "\"REJECTED\"".fromJson[VerificationStatus]
        assertTrue(result == Right(VerificationStatus.REJECTED))
      },
      test("fromJson fails on unknown status") {
        val result = "\"UNKNOWN\"".fromJson[VerificationStatus]
        assertTrue(result.isLeft)
      },
      test("value returns correct string for PENDING") {
        assertTrue(VerificationStatus.PENDING.value == "PENDING")
      },
      test("value returns correct string for APPROVED") {
        assertTrue(VerificationStatus.APPROVED.value == "APPROVED")
      },
      test("value returns correct string for REJECTED") {
        assertTrue(VerificationStatus.REJECTED.value == "REJECTED")
      },
      test("round-trip encode/decode for all statuses") {
        assertTrue(
          VerificationStatus.PENDING.toJson.fromJson[VerificationStatus] == Right(VerificationStatus.PENDING),
          VerificationStatus.APPROVED.toJson.fromJson[VerificationStatus] == Right(VerificationStatus.APPROVED),
          VerificationStatus.REJECTED.toJson.fromJson[VerificationStatus] == Right(VerificationStatus.REJECTED)
        )
      }
    ),
    suite("AppointmentStatus")(
      test("toJson encodes PENDING status") {
        val json = AppointmentStatus.PENDING.toJson
        assertTrue(json == "\"PENDING\"")
      },
      test("toJson encodes CONFIRMED status") {
        val json = AppointmentStatus.CONFIRMED.toJson
        assertTrue(json == "\"CONFIRMED\"")
      },
      test("toJson encodes COMPLETED status") {
        val json = AppointmentStatus.COMPLETED.toJson
        assertTrue(json == "\"COMPLETED\"")
      },
      test("toJson encodes CANCELLED status") {
        val json = AppointmentStatus.CANCELLED.toJson
        assertTrue(json == "\"CANCELLED\"")
      },
      test("toJson encodes NO_SHOW status") {
        val json = AppointmentStatus.NO_SHOW.toJson
        assertTrue(json == "\"NO_SHOW\"")
      },
      test("fromJson decodes PENDING status") {
        val result = "\"PENDING\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.PENDING))
      },
      test("fromJson decodes CONFIRMED status") {
        val result = "\"CONFIRMED\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.CONFIRMED))
      },
      test("fromJson decodes COMPLETED status") {
        val result = "\"COMPLETED\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.COMPLETED))
      },
      test("fromJson decodes CANCELLED status") {
        val result = "\"CANCELLED\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.CANCELLED))
      },
      test("fromJson decodes NO_SHOW status") {
        val result = "\"NO_SHOW\"".fromJson[AppointmentStatus]
        assertTrue(result == Right(AppointmentStatus.NO_SHOW))
      },
      test("fromJson fails on unknown status") {
        val result = "\"UNKNOWN\"".fromJson[AppointmentStatus]
        assertTrue(result.isLeft)
      },
      test("value returns correct string for PENDING") {
        assertTrue(AppointmentStatus.PENDING.value == "PENDING")
      },
      test("value returns correct string for CONFIRMED") {
        assertTrue(AppointmentStatus.CONFIRMED.value == "CONFIRMED")
      },
      test("value returns correct string for COMPLETED") {
        assertTrue(AppointmentStatus.COMPLETED.value == "COMPLETED")
      },
      test("value returns correct string for CANCELLED") {
        assertTrue(AppointmentStatus.CANCELLED.value == "CANCELLED")
      },
      test("value returns correct string for NO_SHOW") {
        assertTrue(AppointmentStatus.NO_SHOW.value == "NO_SHOW")
      },
      test("round-trip encode/decode for all statuses") {
        assertTrue(
          AppointmentStatus.PENDING.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.PENDING),
          AppointmentStatus.CONFIRMED.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.CONFIRMED),
          AppointmentStatus.COMPLETED.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.COMPLETED),
          AppointmentStatus.CANCELLED.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.CANCELLED),
          AppointmentStatus.NO_SHOW.toJson.fromJson[AppointmentStatus] == Right(AppointmentStatus.NO_SHOW)
        )
      }
    )
  )
