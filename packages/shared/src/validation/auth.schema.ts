import { z } from 'zod'

// Email validation
export const emailSchema = z.string().email('Invalid email address')

// Password validation
export const passwordSchema = z.string()
  .min(8, 'Password must be at least 8 characters')
  .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
  .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
  .regex(/\d/, 'Password must contain at least one number')
  .regex(/[!@#$%^&*(),.?":{}|<>]/, 'Password must contain at least one special character')

// Name validation
export const nameSchema = z.string()
  .min(2, 'Name must be at least 2 characters')
  .max(100, 'Name must be less than 100 characters')

// Login schema
export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, 'Password is required')
})

export type LoginInput = z.infer<typeof loginSchema>

// Register schema
export const registerSchema = z.object({
  email: emailSchema,
  password: passwordSchema,
  name: nameSchema
})

export type RegisterInput = z.infer<typeof registerSchema>

// Refresh token schema
export const refreshTokenSchema = z.object({
  refreshToken: z.string().min(1, 'Refresh token is required')
})

export type RefreshTokenInput = z.infer<typeof refreshTokenSchema>

// Verify email schema
export const verifyEmailSchema = z.object({
  token: z.string().min(1, 'Verification token is required')
})

export type VerifyEmailInput = z.infer<typeof verifyEmailSchema>

// Request password reset schema
export const requestPasswordResetSchema = z.object({
  email: emailSchema
})

export type RequestPasswordResetInput = z.infer<typeof requestPasswordResetSchema>

// Reset password schema
export const resetPasswordSchema = z.object({
  token: z.string().min(1, 'Reset token is required'),
  newPassword: passwordSchema
})

export type ResetPasswordInput = z.infer<typeof resetPasswordSchema>

// Update profile schema
export const updateProfileSchema = z.object({
  name: nameSchema.optional(),
  avatarUrl: z.string().url('Invalid URL').optional().or(z.literal(''))
})

export type UpdateProfileInput = z.infer<typeof updateProfileSchema>

// Change password schema
export const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, 'Current password is required'),
  newPassword: passwordSchema
})

export type ChangePasswordInput = z.infer<typeof changePasswordSchema>