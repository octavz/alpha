import { eq, and, gt } from 'drizzle-orm'
import { db } from '../db/client'
import { users, sessions, emailVerifications, passwordResets } from '../db/schema'
import { jwtService, type TokenPayload } from './jwt.service'
import { passwordService } from './password.service'
import { 
  AuthenticationError, 
  ValidationError, 
  NotFoundError, 
  ConflictError 
} from '../middleware/error-handler'
import { randomBytes } from 'crypto'

export interface RegisterInput {
  email: string
  password: string
  name: string
  phone?: string
  role?: 'customer' | 'business_admin' | 'business_staff' | 'admin'
  regionId?: string
}

export interface LoginInput {
  email: string
  password: string
}

export interface AuthResponse {
  user: {
    id: string
    email: string
    name: string | null
    phone: string | null
    role: 'customer' | 'business_admin' | 'business_staff' | 'admin'
    emailVerified: boolean
    avatarUrl: string | null
    regionId: string | null
    isBanned: boolean
    createdAt: Date
  }
  accessToken: string
  refreshToken: string
  sessionId: string
}

export class AuthService {
  async register(input: RegisterInput, userAgent?: string, ipAddress?: string): Promise<AuthResponse> {
    // Validate email
    this.validateEmail(input.email)

    // Check if user already exists
    const existingUser = await db.query.users.findFirst({
      where: eq(users.email, input.email)
    })

    if (existingUser) {
      throw new ConflictError('User with this email already exists')
    }

    // Hash password
    const passwordHash = await passwordService.hashPassword(input.password)

    // Create user
    const [user] = await db.insert(users).values({
      email: input.email,
      passwordHash,
      name: input.name,
      phone: input.phone || null,
      role: input.role || 'customer',
      regionId: input.regionId || null,
      emailVerified: false
    }).returning()

    if (!user) {
      throw new Error('Failed to create user')
    }

    // Create session
    const session = await this.createSession(user.id, userAgent, ipAddress)

    // Generate tokens
    const tokenPayload: Omit<TokenPayload, 'sessionId'> & { sessionId: string } = {
      userId: user.id,
      email: user.email,
      role: user.role as 'customer' | 'business_admin' | 'business_staff' | 'admin',
      sessionId: session.id
    }

    const accessToken = jwtService.generateAccessToken(tokenPayload)
    const refreshToken = jwtService.generateRefreshToken(tokenPayload)

    // TODO: Send email verification

    return {
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        phone: user.phone,
        role: user.role as 'customer' | 'business_admin' | 'business_staff' | 'admin',
        emailVerified: user.emailVerified || false,
        avatarUrl: user.avatarUrl,
        regionId: user.regionId,
        isBanned: user.isBanned || false,
        createdAt: user.createdAt
      },
      accessToken,
      refreshToken,
      sessionId: session.id
    }
  }

  async login(input: LoginInput, userAgent?: string, ipAddress?: string): Promise<AuthResponse> {
    // Find user
    const user = await db.query.users.findFirst({
      where: eq(users.email, input.email)
    })

    if (!user) {
      throw new AuthenticationError('Invalid email or password')
    }

    // Check if user has password (might be Google OAuth user)
    if (!user.passwordHash) {
      throw new AuthenticationError('Please use Google login or reset your password')
    }

    // Verify password
    const passwordValid = await passwordService.verifyPassword(input.password, user.passwordHash)
    if (!passwordValid) {
      throw new AuthenticationError('Invalid email or password')
    }

    // Create session
    const session = await this.createSession(user.id, userAgent, ipAddress)

    // Generate tokens
    const tokenPayload: Omit<TokenPayload, 'sessionId'> & { sessionId: string } = {
      userId: user.id,
      email: user.email,
      role: user.role as 'customer' | 'business_admin' | 'business_staff' | 'admin',
      sessionId: session.id
    }

    const accessToken = jwtService.generateAccessToken(tokenPayload)
    const refreshToken = jwtService.generateRefreshToken(tokenPayload)

    return {
      user: {
        id: user.id,
        email: user.email,
        name: user.name,
        phone: user.phone,
        role: user.role as 'customer' | 'business_admin' | 'business_staff' | 'admin',
        emailVerified: user.emailVerified || false,
        avatarUrl: user.avatarUrl,
        regionId: user.regionId,
        isBanned: user.isBanned || false,
        createdAt: user.createdAt
      },
      accessToken,
      refreshToken,
      sessionId: session.id
    }
  }

  async refreshTokens(refreshToken: string): Promise<{ accessToken: string; refreshToken: string }> {
    // Verify refresh token
    const payload = jwtService.verifyRefreshToken(refreshToken)

    // Find session
    const session = await db.query.sessions.findFirst({
      where: and(
        eq(sessions.id, payload.sessionId),
        eq(sessions.userId, payload.userId),
        eq(sessions.refreshToken, refreshToken),
        gt(sessions.expiresAt, new Date())
      )
    })

    if (!session) {
      throw new AuthenticationError('Invalid refresh token')
    }

    // Generate new tokens
    const tokenPayload: Omit<TokenPayload, 'sessionId'> & { sessionId: string } = {
      userId: payload.userId,
      email: payload.email,
      role: payload.role,
      sessionId: session.id
    }

    const newAccessToken = jwtService.generateAccessToken(tokenPayload)
    const newRefreshToken = jwtService.generateRefreshToken(tokenPayload)

    // Update session with new refresh token
    await db.update(sessions)
      .set({ 
        refreshToken: newRefreshToken
      })
      .where(eq(sessions.id, session.id))

    return {
      accessToken: newAccessToken,
      refreshToken: newRefreshToken
    }
  }

  async logout(sessionId: string, userId: string): Promise<void> {
    await db.delete(sessions)
      .where(and(
        eq(sessions.id, sessionId),
        eq(sessions.userId, userId)
      ))
  }

  async logoutAll(userId: string): Promise<void> {
    await db.delete(sessions)
      .where(eq(sessions.userId, userId))
  }

  async getCurrentUser(userId: string) {
    const user = await db.query.users.findFirst({
      where: eq(users.id, userId)
    })

    if (!user) {
      throw new NotFoundError('User not found')
    }

    return {
      id: user.id,
      email: user.email,
      name: user.name,
      phone: user.phone,
      role: user.role as 'customer' | 'business_admin' | 'business_staff' | 'admin',
      emailVerified: user.emailVerified || false,
      avatarUrl: user.avatarUrl,
      regionId: user.regionId,
      isBanned: user.isBanned || false,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt
    }
  }

  async createEmailVerificationToken(userId: string): Promise<string> {
    const token = randomBytes(32).toString('hex')
    const expiresAt = new Date(Date.now() + 24 * 60 * 60 * 1000) // 24 hours

    await db.insert(emailVerifications).values({
      userId,
      token,
      expiresAt
    })

    return token
  }

  async verifyEmail(token: string): Promise<void> {
    const verification = await db.query.emailVerifications.findFirst({
      where: and(
        eq(emailVerifications.token, token),
        gt(emailVerifications.expiresAt, new Date())
      )
    })

    if (!verification) {
      throw new ValidationError('Invalid or expired verification token')
    }

    // Update user
    await db.update(users)
      .set({ emailVerified: true })
      .where(eq(users.id, verification.userId))

    // Delete verification token
    await db.delete(emailVerifications)
      .where(eq(emailVerifications.id, verification.id))
  }

  async createPasswordResetToken(email: string): Promise<string> {
    const user = await db.query.users.findFirst({
      where: eq(users.email, email)
    })

    if (!user) {
      // Don't reveal that user doesn't exist
      return randomBytes(32).toString('hex')
    }

    const token = randomBytes(32).toString('hex')
    const expiresAt = new Date(Date.now() + 1 * 60 * 60 * 1000) // 1 hour

    await db.insert(passwordResets).values({
      userId: user.id,
      token,
      expiresAt
    })

    return token
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    const reset = await db.query.passwordResets.findFirst({
      where: and(
        eq(passwordResets.token, token),
        gt(passwordResets.expiresAt, new Date())
      )
    })

    if (!reset) {
      throw new ValidationError('Invalid or expired reset token')
    }

    // Validate new password
    passwordService.validatePassword(newPassword)

    // Hash new password
    const passwordHash = await passwordService.hashPassword(newPassword)

    // Update user password
    await db.update(users)
      .set({ passwordHash })
      .where(eq(users.id, reset.userId))

    // Delete all password reset tokens for this user
    await db.delete(passwordResets)
      .where(eq(passwordResets.userId, reset.userId))

    // Delete all sessions for this user (force re-login)
    await this.logoutAll(reset.userId)
  }

  private async createSession(userId: string, userAgent?: string, ipAddress?: string) {
    const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // 7 days

    const [session] = await db.insert(sessions).values({
      userId,
      token: '', // Will be set with JWT
      refreshToken: '', // Will be set with JWT
      userAgent: userAgent || null,
      ipAddress: ipAddress || null,
      expiresAt
    }).returning()

    if (!session) {
      throw new Error('Failed to create session')
    }

    return session
  }

  private validateEmail(email: string): void {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(email)) {
      throw new ValidationError('Invalid email address')
    }
  }
}

export const authService = new AuthService()