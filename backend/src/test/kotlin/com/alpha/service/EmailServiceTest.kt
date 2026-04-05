package com.alpha.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EmailServiceTest {

    private val emailService = EmailService()

    @Test
    fun `sendVerificationEmail should not throw exception`() {
        assertDoesNotThrow {
            emailService.sendVerificationEmail("test@example.com", "Test User")
        }
    }

    @Test
    fun `sendPasswordResetEmail should not throw exception`() {
        assertDoesNotThrow {
            emailService.sendPasswordResetEmail("test@example.com", "Test User", "token123")
        }
    }

    @Test
    fun `sendWelcomeEmail should not throw exception`() {
        assertDoesNotThrow {
            emailService.sendWelcomeEmail("test@example.com", "Test User")
        }
    }

    @Test
    fun `sendAppointmentConfirmation should not throw exception`() {
        assertDoesNotThrow {
            emailService.sendAppointmentConfirmation("test@example.com", "Test User", "Appointment at 10 AM")
        }
    }

    @Test
    fun `sendAppointmentReminder should not throw exception`() {
        assertDoesNotThrow {
            emailService.sendAppointmentReminder("test@example.com", "Test User", "Reminder: Appointment at 10 AM")
        }
    }

    @Test
    fun `sendBusinessVerificationEmail should not throw exception`() {
        assertDoesNotThrow {
            emailService.sendBusinessVerificationEmail("test@example.com", "Test Business", "approved")
        }
    }
}
