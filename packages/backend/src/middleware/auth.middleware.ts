import { Elysia } from 'elysia'
import { jwtService } from '../services/jwt.service'
import { AuthenticationError, AuthorizationError } from './error-handler'
import { db } from '../db/client'
import { users } from '../db/schema'
import { eq } from 'drizzle-orm'

export interface AuthContext {
  userId: string
  email: string
  role: 'customer' | 'business_admin' | 'business_staff' | 'admin'
  regionId?: string | null
}

// Type augmentation for Elysia context
declare global {
  namespace Elysia {
    interface Context {
      auth?: AuthContext
    }
  }
}

// Helper type for routes that use auth middleware
export type AuthenticatedContext = {
  auth: AuthContext
}



// Base authentication that extracts user info but doesn't require auth
export const extractAuth = new Elysia()
  .derive(async ({ request }) => {
    try {
      const authHeader = request.headers.get('authorization')
      
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return { auth: undefined }
      }

      const token = authHeader.substring(7)
      const payload = await jwtService.verifyAccessToken(token)

      if (!payload) {
        return { auth: undefined }
      }

      // Get user from database to ensure they exist and get latest role
      const user = await db.query.users.findFirst({
        where: eq(users.id, payload.userId),
        columns: {
          id: true,
          email: true,
          role: true,
          regionId: true,
          isBanned: true
        }
      })

      if (!user || user.isBanned) {
        return { auth: undefined }
      }

      return {
        auth: {
          userId: user.id,
          email: user.email,
          role: user.role as AuthContext['role'],
          regionId: user.regionId
        }
      }
    } catch (error) {
      console.error('Auth extraction error:', error)
      return { auth: undefined }
    }
  })

// Authentication middleware that requires valid auth
export const authMiddleware = new Elysia()
  .use(extractAuth)
  .onBeforeHandle(({ auth, set }) => {
    if (!auth) {
      set.status = 401
      throw new AuthenticationError('Authentication required')
    }
  })

// Role-based authorization middleware
export const requireRole = (...allowedRoles: AuthContext['role'][]) => {
  return new Elysia()
    .use(authMiddleware)
    .onBeforeHandle(({ auth, set }) => {
      if (!auth) {
        set.status = 401
        throw new AuthenticationError('Authentication required')
      }

      if (!allowedRoles.includes(auth.role)) {
        set.status = 403
        throw new AuthorizationError(`Required role: ${allowedRoles.join(' or ')}`)
      }
    })
}

// Specific role middlewares
export const requireAdmin = requireRole('admin')
export const requireBusiness = requireRole('business_admin', 'business_staff')
export const requireCustomer = requireRole('customer')

// Public routes that optionally use auth (for personalized results)
export const optionalAuth = extractAuth

// Region validation middleware
export const validateRegion = new Elysia()
  .derive(async ({ query, set }) => {
    const regionCode = (query as any).region
    
    if (!regionCode) {
      set.status = 400
      throw new Error('Region code is required')
    }

    // Check if region exists and is active
    const region = await db.query.regions.findFirst({
      where: (regions, { eq, and }) => and(
        eq(regions.code, regionCode),
        eq(regions.isActive, true)
      )
    })

    if (!region) {
      set.status = 400
      throw new Error('Invalid or inactive region')
    }

    return { region }
  })

// User region validation (for users setting their region)
export const validateUserRegion = new Elysia()
  .derive(async ({ body, set }) => {
    const { regionId } = body as { regionId?: string }
    
    if (!regionId) {
      return { region: null }
    }

    const region = await db.query.regions.findFirst({
      where: (regions, { eq, and }) => and(
        eq(regions.id, regionId),
        eq(regions.isActive, true)
      )
    })

    if (!region) {
      set.status = 400
      throw new Error('Invalid or inactive region')
    }

    return { region }
  })