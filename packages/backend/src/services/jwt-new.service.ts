import jwt from 'jsonwebtoken'
import { AuthenticationError } from '../middleware/error-handler'

export interface TokenPayload {
  userId: string
  email: string
  role: 'customer' | 'business_admin' | 'business_staff' | 'admin'
  sessionId: string
}

export class JWTService {
  private readonly accessTokenSecret: string
  private readonly refreshTokenSecret: string
  private readonly accessTokenExpiry: string
  private readonly refreshTokenExpiry: string

  constructor() {
    const env = process.env as any
    this.accessTokenSecret = env.JWT_ACCESS_SECRET || env.JWT_SECRET || 'your-access-token-secret-change-this'
    this.refreshTokenSecret = env.JWT_REFRESH_SECRET || env.JWT_SECRET || 'your-refresh-token-secret-change-this'
    this.accessTokenExpiry = env.JWT_ACCESS_EXPIRY || '15m'
    this.refreshTokenExpiry = env.JWT_REFRESH_EXPIRY || '7d'

    if (!this.accessTokenSecret || !this.refreshTokenSecret) {
      throw new Error('JWT secrets must be configured in environment variables')
    }
  }

  generateAccessToken(payload: TokenPayload): string {
    return jwt.sign(payload, this.accessTokenSecret, {
      expiresIn: this.accessTokenExpiry,
      issuer: 'alpha-backend',
      audience: 'alpha-client'
    } as jwt.SignOptions)
  }

  generateRefreshToken(payload: TokenPayload): string {
    return jwt.sign(payload, this.refreshTokenSecret, {
      expiresIn: this.refreshTokenExpiry,
      issuer: 'alpha-backend',
      audience: 'alpha-client'
    } as jwt.SignOptions)
  }

  verifyAccessToken(token: string): TokenPayload {
    try {
      const payload = jwt.verify(token, this.accessTokenSecret, {
        issuer: 'alpha-backend',
        audience: 'alpha-client'
      }) as TokenPayload
      return payload
    } catch (error) {
      if (error instanceof jwt.TokenExpiredError) {
        throw new AuthenticationError('Access token expired')
      }
      if (error instanceof jwt.JsonWebTokenError) {
        throw new AuthenticationError('Invalid access token')
      }
      throw new AuthenticationError('Failed to verify access token')
    }
  }

  verifyRefreshToken(token: string): TokenPayload {
    try {
      const payload = jwt.verify(token, this.refreshTokenSecret, {
        issuer: 'alpha-backend',
        audience: 'alpha-client'
      }) as TokenPayload
      return payload
    } catch (error) {
      if (error instanceof jwt.TokenExpiredError) {
        throw new AuthenticationError('Refresh token expired')
      }
      if (error instanceof jwt.JsonWebTokenError) {
        throw new AuthenticationError('Invalid refresh token')
      }
      throw new AuthenticationError('Failed to verify refresh token')
    }
  }
}

export const jwtService = new JWTService()