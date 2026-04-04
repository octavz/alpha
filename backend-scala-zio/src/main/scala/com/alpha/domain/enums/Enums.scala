package com.alpha.domain.enums

enum UserRole(val value: String):
  case ADMIN extends UserRole("ADMIN")
  case CUSTOMER extends UserRole("CUSTOMER")
  case BUSINESS_ADMIN extends UserRole("BUSINESS_ADMIN")
  case BUSINESS_STAFF extends UserRole("BUSINESS_STAFF")

enum VerificationStatus(val value: String):
  case PENDING extends VerificationStatus("PENDING")
  case APPROVED extends VerificationStatus("APPROVED")
  case REJECTED extends VerificationStatus("REJECTED")

enum AppointmentStatus(val value: String):
  case PENDING extends AppointmentStatus("PENDING")
  case CONFIRMED extends AppointmentStatus("CONFIRMED")
  case COMPLETED extends AppointmentStatus("COMPLETED")
  case CANCELLED extends AppointmentStatus("CANCELLED")
  case NO_SHOW extends AppointmentStatus("NO_SHOW")
