import { Elysia, t } from 'elysia'
import { adminService } from '../services/admin.service'
import { jwtService } from '../services/jwt.service'
import { db } from '../db/client'
import { users } from '../db/schema'
import { eq } from 'drizzle-orm'

// Helper function to check if user is admin
async function verifyAdmin(authHeader: string | null) {
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
        isBanned: true
      }
    })

    if (!user || user.isBanned || user.role !== 'admin') {
      return null
    }

    return {
      userId: user.id,
      email: user.email,
      role: user.role
    }
  } catch (error) {
    return null
  }
}

export const adminRoutes = new Elysia({ prefix: '/api/admin' })
  // Business Management
  .get('/businesses/pending',
    async ({ request, query, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const page = query.page ? parseInt(query.page) : 1
      const limit = query.limit ? parseInt(query.limit) : 20

      const result = await adminService.getPendingBusinesses(page, limit)
      
      return {
        success: true,
        data: result
      }
    },
    {
      query: t.Object({
        page: t.Optional(t.String()),
        limit: t.Optional(t.String())
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .post('/businesses/approve',
    async ({ body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const business = await adminService.approveBusiness(body)
      
      return {
        success: true,
        data: business
      }
    },
    {
      body: t.Object({
        businessId: t.String(),
        status: t.Union([t.Literal('approved'), t.Literal('rejected')]),
        rejectionReason: t.Optional(t.String())
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .get('/businesses/stats',
    async ({ request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const stats = await adminService.getBusinessVerificationStats()
      
      return {
        success: true,
        data: stats
      }
    },
    {
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  // User Management
  .get('/users',
    async ({ request, query, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const page = query.page ? parseInt(query.page) : 1
      const limit = query.limit ? parseInt(query.limit) : 20

      const result = await adminService.getUsers(page, limit, query.search)
      
      return {
        success: true,
        data: result
      }
    },
    {
      query: t.Object({
        page: t.Optional(t.String()),
        limit: t.Optional(t.String()),
        search: t.Optional(t.String())
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .post('/users/ban',
    async ({ body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const user = await adminService.updateUserBanStatus(body)
      
      return {
        success: true,
        data: user
      }
    },
    {
      body: t.Object({
        userId: t.String(),
        isBanned: t.Boolean(),
        banReason: t.Optional(t.String())
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .get('/users/stats',
    async ({ request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const stats = await adminService.getUserStats()
      
      return {
        success: true,
        data: stats
      }
    },
    {
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  // Review Management
  .get('/reviews/pending',
    async ({ request, query, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const page = query.page ? parseInt(query.page) : 1
      const limit = query.limit ? parseInt(query.limit) : 20

      const result = await adminService.getPendingReviews(page, limit)
      
      return {
        success: true,
        data: result
      }
    },
    {
      query: t.Object({
        page: t.Optional(t.String()),
        limit: t.Optional(t.String())
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .post('/reviews/approve',
    async ({ body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const review = await adminService.approveReview(body)
      
      return {
        success: true,
        data: review
      }
    },
    {
      body: t.Object({
        reviewId: t.String(),
        isApproved: t.Boolean()
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  // Platform Analytics
  .get('/stats',
    async ({ request, query, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const input: any = {}
      if (query.startDate) input.startDate = new Date(query.startDate)
      if (query.endDate) input.endDate = new Date(query.endDate)
      if (query.regionId) input.regionId = query.regionId

      const stats = await adminService.getPlatformStats(input)
      
      return {
        success: true,
        data: stats
      }
    },
    {
      query: t.Object({
        startDate: t.Optional(t.String({ format: 'date' })),
        endDate: t.Optional(t.String({ format: 'date' })),
        regionId: t.Optional(t.String())
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  // Region Management
  .get('/regions',
    async ({ request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const regions = await adminService.getRegions()
      
      return {
        success: true,
        data: regions
      }
    },
    {
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .put('/regions/:id/status',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const region = await adminService.updateRegionStatus(params.id, body.isActive)
      
      return {
        success: true,
        data: region
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      body: t.Object({
        isActive: t.Boolean()
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  // Category Management
  .get('/categories',
    async ({ request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const categories = await adminService.getCategories()
      
      return {
        success: true,
        data: categories
      }
    },
    {
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )

  .put('/categories/:id/status',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const admin = await verifyAdmin(authHeader)
      
      if (!admin) {
        set.status = 401
        return { success: false, error: 'Admin access required' }
      }

      const category = await adminService.updateCategoryStatus(params.id, body.isActive)
      
      return {
        success: true,
        data: category
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      body: t.Object({
        isActive: t.Boolean()
      }),
      detail: {
        security: [{ bearerAuth: [] }]
      }
    }
  )