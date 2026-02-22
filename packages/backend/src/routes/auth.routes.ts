import { Elysia, t } from 'elysia'
import { authService } from '../services/auth.service'
import { AuthenticationError } from '../middleware/error-handler'
import { jwtService } from '../services/jwt.service'

export const authRoutes = new Elysia({ prefix: '/api/auth' })
  // Register
  .post('/register', 
    async ({ body, request, set }) => {
      const userAgent = request.headers.get('user-agent')
      const ipAddress = request.headers.get('x-forwarded-for') || 'unknown'

      const result = await authService.register(body, userAgent, ipAddress)
      
      set.status = 201
      return {
        success: true,
        data: result
      }
    },
    {
      body: t.Object({
        email: t.String({ format: 'email' }),
        password: t.String({ minLength: 8 }),
        name: t.String({ minLength: 2 })
      })
    }
  )

  // Login
  .post('/login',
    async ({ body, request, set }) => {
      const userAgent = request.headers.get('user-agent')
      const ipAddress = request.headers.get('x-forwarded-for') || 'unknown'

      const result = await authService.login(body, userAgent, ipAddress)
      
      set.status = 200
      return {
        success: true,
        data: result
      }
    },
    {
      body: t.Object({
        email: t.String({ format: 'email' }),
        password: t.String()
      })
    }
  )

  // Refresh tokens
  .post('/refresh',
    async ({ body, set }) => {
      const { refreshToken } = body
      const result = await authService.refreshTokens(refreshToken)
      
      set.status = 200
      return {
        success: true,
        data: result
      }
    },
    {
      body: t.Object({
        refreshToken: t.String()
      })
    }
  )

  // Logout
  .post('/logout',
    async ({ headers, set }) => {
      const authHeader = headers.authorization
      
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        throw new AuthenticationError('Authentication required')
      }

      const token = authHeader.substring(7)
      const payload = jwtService.decodeToken(token)

      if (!payload) {
        throw new AuthenticationError('Invalid token')
      }

      await authService.logout(payload.sessionId, payload.userId)
      
      set.status = 200
      return {
        success: true,
        message: 'Logged out successfully'
      }
    }
  )

  // Get current user
  .get('/me',
    async ({ headers, set }) => {
      const authHeader = headers.authorization
      
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        throw new AuthenticationError('Authentication required')
      }

      const token = authHeader.substring(7)
      const payload = jwtService.verifyAccessToken(token)

      const user = await authService.getCurrentUser(payload.userId)
      
      set.status = 200
      return {
        success: true,
        data: user
      }
    }
  )

  // Request email verification
  .post('/verify-email/request',
    async ({ headers, set }) => {
      const authHeader = headers.authorization
      
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        throw new AuthenticationError('Authentication required')
      }

      const token = authHeader.substring(7)
      const payload = jwtService.verifyAccessToken(token)

      const verificationToken = await authService.createEmailVerificationToken(payload.userId)
      
      // TODO: Send email with verification token
      
      set.status = 200
      return {
        success: true,
        message: 'Verification email sent'
      }
    }
  )

  // Verify email
  .post('/verify-email',
    async ({ body, set }) => {
      const { token } = body
      await authService.verifyEmail(token)
      
      set.status = 200
      return {
        success: true,
        message: 'Email verified successfully'
      }
    },
    {
      body: t.Object({
        token: t.String()
      })
    }
  )

  // Request password reset
  .post('/reset-password/request',
    async ({ body, set }) => {
      const { email } = body
      const token = await authService.createPasswordResetToken(email)
      
      // TODO: Send email with reset token
      
      set.status = 200
      return {
        success: true,
        message: 'If an account exists with this email, a reset link has been sent'
      }
    },
    {
      body: t.Object({
        email: t.String({ format: 'email' })
      })
    }
  )

  // Reset password
  .post('/reset-password',
    async ({ body, set }) => {
      const { token, newPassword } = body
      await authService.resetPassword(token, newPassword)
      
      set.status = 200
      return {
        success: true,
        message: 'Password reset successfully'
      }
    },
    {
      body: t.Object({
        token: t.String(),
        newPassword: t.String({ minLength: 8 })
      })
    }
  )