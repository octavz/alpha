import { Elysia, t } from 'elysia'
import { appointmentService } from '../services/appointment.service'
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

export const appointmentRoutes = new Elysia({ prefix: '/api/appointments' })
  // Create appointment (customer only)
  .post('/', 
    async ({ body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      if (auth.role !== 'customer') {
        set.status = 403
        return { success: false, error: 'Only customers can create appointments' }
      }

      try {
        const appointment = await appointmentService.createAppointment(auth.userId, body)
        
        set.status = 201
        return {
          success: true,
          data: appointment
        }
      } catch (error: any) {
        set.status = 400
        return {
          success: false,
          error: error.message || 'Failed to create appointment'
        }
      }
    },
    {
      body: t.Object({
        businessId: t.String(),
        serviceId: t.Optional(t.String()),
        appointmentDate: t.String({ format: 'date' }),
        startTime: t.String({ pattern: '^([0-1][0-9]|2[0-3]):[0-5][0-9]$' }),
        endTime: t.String({ pattern: '^([0-1][0-9]|2[0-3]):[0-5][0-9]$' }),
        notes: t.Optional(t.String()),
        servicePointNumber: t.Optional(t.Number({ minimum: 1 }))
      })
    }
  )

  // Get appointment by ID
  .get('/:id',
    async ({ params, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      try {
        const appointment = await appointmentService.getAppointment(params.id, auth.userId)
        
        return {
          success: true,
          data: appointment
        }
      } catch (error: any) {
        set.status = 404
        return {
          success: false,
          error: error.message || 'Appointment not found'
        }
      }
    },
    {
      params: t.Object({
        id: t.String()
      })
    }
  )

  // Update appointment
  .put('/:id',
    async ({ params, body, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      try {
        const appointment = await appointmentService.updateAppointment(params.id, auth.userId, body)
        
        return {
          success: true,
          data: appointment
        }
      } catch (error: any) {
        set.status = 400
        return {
          success: false,
          error: error.message || 'Failed to update appointment'
        }
      }
    },
    {
      params: t.Object({
        id: t.String()
      }),
      body: t.Object({
        serviceId: t.Optional(t.String()),
        appointmentDate: t.Optional(t.String({ format: 'date' })),
        startTime: t.Optional(t.String({ pattern: '^([0-1][0-9]|2[0-3]):[0-5][0-9]$' })),
        endTime: t.Optional(t.String({ pattern: '^([0-1][0-9]|2[0-3]):[0-5][0-9]$' })),
        status: t.Optional(t.Union([
          t.Literal('scheduled'),
          t.Literal('confirmed'),
          t.Literal('in_progress'),
          t.Literal('completed'),
          t.Literal('cancelled'),
          t.Literal('no_show')
        ])),
        notes: t.Optional(t.String()),
        servicePointNumber: t.Optional(t.Number({ minimum: 1 }))
      })
    }
  )

  // Get customer appointments
  .get('/customer/my-appointments',
    async ({ request, query, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      if (auth.role !== 'customer') {
        set.status = 403
        return { success: false, error: 'Only customers can view their appointments' }
      }

      const page = query.page ? parseInt(query.page) : 1
      const limit = query.limit ? parseInt(query.limit) : 20

      const result = await appointmentService.getCustomerAppointments(auth.userId, page, limit)
      
      return {
        success: true,
        data: result
      }
    },
    {
      query: t.Object({
        page: t.Optional(t.String()),
        limit: t.Optional(t.String())
      })
    }
  )

  // Get business appointments (business owner only)
  .get('/business/:businessId',
    async ({ params, request, query, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      if (auth.role !== 'business_admin' && auth.role !== 'business_staff') {
        set.status = 403
        return { success: false, error: 'Only business owners can view business appointments' }
      }

      const page = query.page ? parseInt(query.page) : 1
      const limit = query.limit ? parseInt(query.limit) : 20

      const result = await appointmentService.getBusinessAppointments(
        params.businessId,
        auth.userId,
        page,
        limit,
        query.date
      )
      
      return {
        success: true,
        data: result
      }
    },
    {
      params: t.Object({
        businessId: t.String()
      }),
      query: t.Object({
        page: t.Optional(t.String()),
        limit: t.Optional(t.String()),
        date: t.Optional(t.String({ format: 'date' }))
      })
    }
  )

  // Get available time slots
  .get('/business/:businessId/available-slots',
    async ({ params, query, set }) => {
      // This endpoint is public (no auth required)
      const input: any = {
        businessId: params.businessId,
        date: query.date
      }

      if (query.serviceId) input.serviceId = query.serviceId
      if (query.durationMinutes) input.durationMinutes = parseInt(query.durationMinutes)

      try {
        const slots = await appointmentService.getAvailableTimeSlots(input)
        
        return {
          success: true,
          data: slots
        }
      } catch (error: any) {
        set.status = 400
        return {
          success: false,
          error: error.message || 'Failed to get available time slots'
        }
      }
    },
    {
      params: t.Object({
        businessId: t.String()
      }),
      query: t.Object({
        date: t.String({ format: 'date' }),
        serviceId: t.Optional(t.String()),
        durationMinutes: t.Optional(t.String())
      })
    }
  )

  // Get appointment stats (business owner only)
  .get('/business/:businessId/stats',
    async ({ params, request, set }) => {
      const authHeader = request.headers.get('authorization')
      const auth = await getAuthUser(authHeader)
      
      if (!auth) {
        set.status = 401
        return { success: false, error: 'Unauthorized' }
      }

      if (auth.role !== 'business_admin' && auth.role !== 'business_staff') {
        set.status = 403
        return { success: false, error: 'Only business owners can view appointment stats' }
      }

      try {
        const stats = await appointmentService.getAppointmentStats(params.businessId, auth.userId)
        
        return {
          success: true,
          data: stats
        }
      } catch (error: any) {
        set.status = 400
        return {
          success: false,
          error: error.message || 'Failed to get appointment stats'
        }
      }
    },
    {
      params: t.Object({
        businessId: t.String()
      })
    }
  )