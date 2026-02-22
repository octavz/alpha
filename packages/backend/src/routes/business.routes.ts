import { Elysia, t } from 'elysia'
import { businessService } from '../services/business.service'
import { jwtService } from '../services/jwt.service'
import { db } from '../db/client'
import { users } from '../db/schema'
import { eq } from 'drizzle-orm'

// Helper function to extract user from auth header
async function getAuthUser(authHeader: string | null) {
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return null
  }

  try {
    const token = authHeader.substring(7)
    const payload = jwtService.verifyAccessToken(token)
    
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
      return null
    }

    return {
      userId: user.id,
      email: user.email,
      role: user.role as 'customer' | 'business_admin' | 'business_staff' | 'admin',
      regionId: user.regionId
    }
  } catch (error) {
    return null
  }
}

export const businessRoutes = new Elysia({ prefix: '/api/businesses' })
  // Create business (requires authentication)
  .post('/', 
    async ({ body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const business = await businessService.createBusiness(auth.userId, body)
      
      set.status = 201
      return {
        success: true,
        data: business
      }
    },
    {
      body: t.Object({
        name: t.String({ minLength: 2, maxLength: 100 }),
        description: t.String({ minLength: 10, maxLength: 200 }),
        addressLine1: t.String({ minLength: 5 }),
        addressLine2: t.Optional(t.String()),
        city: t.String({ minLength: 2 }),
        state: t.String({ minLength: 2 }),
        zipCode: t.String({ minLength: 3 }),
        country: t.String({ minLength: 2 }),
        latitude: t.Number(),
        longitude: t.Number(),
        categoryId: t.String(),
        phone: t.String({ minLength: 5 }),
        email: t.String({ format: 'email' }),
        website: t.Optional(t.String({ format: 'url' })),
        logoUrl: t.Optional(t.String({ format: 'url' })),
        servicePointsCount: t.Number({ minimum: 1, maximum: 50 })
      })
    }
  )

  // Get current user's business (requires authentication)
  .get('/my-business',
    async ({ request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const business = await businessService.getUserBusiness(auth.userId)
      
      if (!business) {
        set.status = 404
        return { success: false, error: 'Business not found' }
      }

      return {
        success: true,
        data: business
      }
    }
  )

  // Update business (requires authentication)
  .put('/:id',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const business = await businessService.updateBusiness(params.id, auth.userId, body)
      
      return {
        success: true,
        data: business
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      body: t.Object({
        name: t.Optional(t.String({ minLength: 2, maxLength: 100 })),
        description: t.Optional(t.String({ minLength: 10, maxLength: 200 })),
        addressLine1: t.Optional(t.String({ minLength: 5 })),
        addressLine2: t.Optional(t.String()),
        city: t.Optional(t.String({ minLength: 2 })),
        state: t.Optional(t.String({ minLength: 2 })),
        zipCode: t.Optional(t.String({ minLength: 3 })),
        country: t.Optional(t.String({ minLength: 2 })),
        latitude: t.Optional(t.Number()),
        longitude: t.Optional(t.Number()),
        categoryId: t.Optional(t.String()),
        phone: t.Optional(t.String({ minLength: 5 })),
        email: t.Optional(t.String({ format: 'email' })),
        website: t.Optional(t.String({ format: 'url' })),
        logoUrl: t.Optional(t.String({ format: 'url' })),
        servicePointsCount: t.Optional(t.Number({ minimum: 1, maximum: 50 })),
        isActive: t.Optional(t.Boolean())
      })
    }
  )

  // Get business by ID (public)
  .get('/:id',
    async ({ params }) => {
      const business = await businessService.getBusiness(params.id)
      
      return {
        success: true,
        data: business
      }
    },
    {
      params: t.Object({
        id: t.String()
      })
    }
  )

  // Search businesses (public)
  .get('/',
    async ({ query }) => {
      const searchInput: any = {
        regionId: query.regionId
      }

      if (query.categoryId) searchInput.categoryId = query.categoryId
      if (query.q) searchInput.query = query.q
      if (query.page) searchInput.page = parseInt(query.page)
      if (query.limit) searchInput.limit = parseInt(query.limit)
      if (query.sortBy) searchInput.sortBy = query.sortBy

      const result = await businessService.searchBusinesses(searchInput)
      
      return {
        success: true,
        data: result
      }
    },
    {
      query: t.Object({
        regionId: t.String(),
        categoryId: t.Optional(t.String()),
        q: t.Optional(t.String()),
        page: t.Optional(t.String()),
        limit: t.Optional(t.String()),
        sortBy: t.Optional(t.String())
      })
    }
  )

  // Get business hours (public)
  .get('/:id/hours',
    async ({ params }) => {
      const hours = await businessService.getBusinessHours(params.id)
      
      return {
        success: true,
        data: hours
      }
    },
    {
      params: t.Object({
        id: t.String()
      })
    }
  )

  // Update business hours (requires authentication)
  .put('/:id/hours',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const hours = await businessService.updateBusinessHours(params.id, auth.userId, body)
      
      return {
        success: true,
        data: hours
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      body: t.Array(
        t.Object({
          dayOfWeek: t.Number({ minimum: 0, maximum: 6 }),
          openTime: t.String({ pattern: '^([0-1][0-9]|2[0-3]):[0-5][0-9]$' }),
          closeTime: t.String({ pattern: '^([0-1][0-9]|2[0-3]):[0-5][0-9]$' }),
          isClosed: t.Boolean()
        })
      )
    }
  )

  // Get business services (public)
  .get('/:id/services',
    async ({ params }) => {
      const services = await businessService.getBusinessServices(params.id)
      
      return {
        success: true,
        data: services
      }
    },
    {
      params: t.Object({
        id: t.String()
      })
    }
  )

  // Create service (requires authentication)
  .post('/:id/services',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const service = await businessService.createService(params.id, auth.userId, body)
      
      set.status = 201
      return {
        success: true,
        data: service
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      body: t.Object({
        name: t.String({ minLength: 2, maxLength: 100 }),
        description: t.String({ minLength: 5, maxLength: 500 }),
        durationMinutes: t.Number({ minimum: 5, maximum: 480 }),
        price: t.Optional(t.Number({ minimum: 0 })),
        isActive: t.Boolean()
      })
    }
  )

  // Update service (requires authentication)
  .put('/services/:serviceId',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const service = await businessService.updateService(params.serviceId, auth.userId, body)
      
      return {
        success: true,
        data: service
      }
    },
    {
      params: t.Object({
        serviceId: t.String()
      }),
      body: t.Object({
        name: t.Optional(t.String({ minLength: 2, maxLength: 100 })),
        description: t.Optional(t.String({ minLength: 5, maxLength: 500 })),
        durationMinutes: t.Optional(t.Number({ minimum: 5, maximum: 480 })),
        price: t.Optional(t.Number({ minimum: 0 })),
        isActive: t.Optional(t.Boolean())
      })
    }
  )

  // Get business reviews (public)
  .get('/:id/reviews',
    async ({ params, query }) => {
      const result = await businessService.getBusinessReviews(
        params.id,
        query.page ? parseInt(query.page) : 1,
        query.limit ? parseInt(query.limit) : 20
      )
      
      return {
        success: true,
        data: result
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      query: t.Object({
        page: t.Optional(t.String()),
        limit: t.Optional(t.String())
      })
    }
  )

  // Get business stats (requires authentication)
  .get('/:id/stats',
    async ({ params, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      const stats = await businessService.getBusinessStats(params.id, auth.userId)
      
      return {
        success: true,
        data: stats
      }
    },
    {
      params: t.Object({
        id: t.String()
      })
    }
  )