import { eq, and, sql, asc, gte, lte, or, gt } from 'drizzle-orm'
import { db } from '../db/client'
import { 
  appointments,
  businesses,
  services,
  businessHours,
  users
} from '../db/schema'
import { 
  ValidationError, 
  NotFoundError,
  ConflictError
} from '../middleware/error-handler'

export interface CreateAppointmentInput {
  businessId: string
  serviceId?: string
  appointmentDate: string // ISO date string
  startTime: string // "09:00"
  endTime: string // "09:30"
  customerNotes?: string
  servicePointNumber?: number
}

export interface UpdateAppointmentInput {
  serviceId?: string
  appointmentDate?: string
  startTime?: string
  endTime?: string
  status?: 'scheduled' | 'confirmed' | 'in_progress' | 'completed' | 'cancelled' | 'no_show'
  notes?: string
  servicePointNumber?: number
}

export interface AvailableTimeSlot {
  date: string
  startTime: string
  endTime: string
  availableServicePoints: number[]
}

export interface TimeSlotQuery {
  businessId: string
  serviceId?: string
  date: string
  durationMinutes?: number
}

export class AppointmentService {
  async createAppointment(customerId: string, input: CreateAppointmentInput) {
    // Validate business exists and is active
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, input.businessId),
        eq(businesses.isActive, true),
        eq(businesses.isVerified, true)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or not available')
    }

    // Validate service if provided
    if (input.serviceId) {
      const service = await db.query.services.findFirst({
        where: and(
          eq(services.id, input.serviceId),
          eq(services.businessId, input.businessId),
          eq(services.isActive, true)
        )
      })

      if (!service) {
        throw new NotFoundError('Service not found or not available')
      }
    }

    // Validate appointment date is in the future
    const appointmentDate = new Date(input.appointmentDate)
    const now = new Date()
    if (appointmentDate < now) {
      throw new ValidationError('Appointment date must be in the future')
    }

    // Check business hours
    const dayOfWeek = appointmentDate.getDay() // 0 = Sunday, 6 = Saturday
    const businessDayHours = await db.query.businessHours.findFirst({
      where: and(
        eq(businessHours.businessId, input.businessId),
        eq(businessHours.dayOfWeek, dayOfWeek)
      )
    })

    if (!businessDayHours || businessDayHours.isClosed || !businessDayHours.openTime || !businessDayHours.closeTime) {
      throw new ValidationError('Business is closed on this day')
    }

    // Check if time slot is within business hours
    if (input.startTime < businessDayHours.openTime! || input.endTime > businessDayHours.closeTime!) {
      throw new ValidationError('Appointment time is outside business hours')
    }

    // Check for overlapping appointments
    const overlappingAppointments = await db.query.appointments.findMany({
      where: and(
        eq(appointments.businessId, input.businessId),
        eq(appointments.appointmentDate, input.appointmentDate),
        eq(appointments.status, 'pending'),
        or(
          // New appointment starts during existing appointment
          and(
            lte(appointments.startTime, input.startTime),
            gt(appointments.endTime, input.startTime)
          ),
          // New appointment ends during existing appointment
          and(
            lte(appointments.startTime, input.endTime),
            gt(appointments.endTime, input.endTime)
          ),
          // New appointment completely contains existing appointment
          and(
            lte(sql`${input.startTime}`, appointments.startTime),
            gte(sql`${input.endTime}`, appointments.endTime)
          )
        )
      )
    })

    // Check service point availability if specified
    if (input.servicePointNumber) {
      const servicePointsCount = business.servicePointsCount || 1
      if (input.servicePointNumber < 1 || input.servicePointNumber > servicePointsCount) {
        throw new ValidationError(`Service point must be between 1 and ${servicePointsCount}`)
      }

      // Check if service point is already booked
      const servicePointBooked = overlappingAppointments.some(
        appointment => appointment.servicePointNumber === input.servicePointNumber
      )

      if (servicePointBooked) {
        throw new ConflictError('Service point is already booked for this time slot')
      }
    }

    // If no service point specified, find an available one
    let servicePointNumber = input.servicePointNumber
    if (!servicePointNumber) {
      const bookedServicePoints = overlappingAppointments
        .map(a => a.servicePointNumber)
        .filter(Boolean) as number[]

      const servicePointsCount = business.servicePointsCount || 1
      // Find first available service point
      for (let i = 1; i <= servicePointsCount; i++) {
        if (!bookedServicePoints.includes(i)) {
          servicePointNumber = i
          break
        }
      }

      if (!servicePointNumber) {
        throw new ConflictError('No service points available for this time slot')
      }
    }

    // Get customer information for cached fields
    const customer = await db.query.users.findFirst({
      where: eq(users.id, customerId),
      columns: {
        name: true,
        email: true,
        phone: true
      }
    })

    if (!customer) {
      throw new NotFoundError('Customer not found')
    }

    // Create appointment
    const appointmentData: any = {
      businessId: input.businessId,
      customerId,
      appointmentDate: input.appointmentDate,
      startTime: input.startTime,
      endTime: input.endTime,
      customerName: customer.name || 'Unknown Customer',
      customerEmail: customer.email || '',
      customerPhone: customer.phone,
      servicePointNumber,
      status: 'pending'
    }

    if (input.serviceId) {
      appointmentData.serviceId = input.serviceId
    }

    if (input.customerNotes) {
      appointmentData.customerNotes = input.customerNotes
    }

    const [appointment] = await db.insert(appointments).values(appointmentData).returning()

    return appointment
  }

  async getAppointment(appointmentId: string, userId: string) {
    const appointment = await db.query.appointments.findFirst({
      where: eq(appointments.id, appointmentId),
      with: {
        business: {
          columns: {
            id: true,
            name: true,
            phone: true,
            email: true
          }
        },
        service: {
          columns: {
            id: true,
            name: true,
            durationMinutes: true,
            price: true
          }
        },
        customer: {
          columns: {
            id: true,
            name: true,
            email: true,
            phone: true
          }
        }
      }
    })

    if (!appointment) {
      throw new NotFoundError('Appointment not found')
    }

    // Check if user has permission to view this appointment
    if (appointment.customerId !== userId) {
      // Check if user owns the business
      const business = await db.query.businesses.findFirst({
        where: and(
          eq(businesses.id, appointment.businessId),
          eq(businesses.userId, userId)
        )
      })
      
      if (!business) {
        throw new ValidationError('You do not have permission to view this appointment')
      }
    }

    return appointment
  }

  async updateAppointment(appointmentId: string, userId: string, input: UpdateAppointmentInput) {
    const appointment = await db.query.appointments.findFirst({
      where: eq(appointments.id, appointmentId)
    })

    if (!appointment) {
      throw new NotFoundError('Appointment not found')
    }

    // Check if user has permission to update this appointment
    if (appointment.customerId !== userId) {
      // Check if user owns the business
      const business = await db.query.businesses.findFirst({
        where: and(
          eq(businesses.id, appointment.businessId),
          eq(businesses.userId, userId)
        )
      })
      
      if (!business) {
        throw new ValidationError('You do not have permission to update this appointment')
      }
    }

    // Customers can only cancel their own appointments
    if (appointment.customerId === userId && input.status && input.status !== 'cancelled') {
      throw new ValidationError('Customers can only cancel appointments')
    }

    // Business owners can update status and notes
    const updateData: any = {
      updatedAt: new Date()
    }

    if (input.status) updateData.status = input.status
    if (input.notes !== undefined) updateData.notes = input.notes
    if (input.servicePointNumber) updateData.servicePointNumber = input.servicePointNumber

    // Only allow date/time changes for future appointments
    const today = new Date().toISOString().split('T')[0] || ''
    if (appointment.appointmentDate > today) {
      if (input.appointmentDate) updateData.appointmentDate = input.appointmentDate
      if (input.startTime) updateData.startTime = input.startTime
      if (input.endTime) updateData.endTime = input.endTime
      if (input.serviceId) updateData.serviceId = input.serviceId
    }

    const [updatedAppointment] = await db.update(appointments)
      .set(updateData)
      .where(eq(appointments.id, appointmentId))
      .returning()

    return updatedAppointment
  }

  async getCustomerAppointments(customerId: string, page: number = 1, limit: number = 20) {
    const offset = (page - 1) * limit

    const appointmentsList = await db.query.appointments.findMany({
      where: eq(appointments.customerId, customerId),
      with: {
        business: {
          columns: {
            id: true,
            name: true,
            logoUrl: true
          }
        },
        service: {
          columns: {
            id: true,
            name: true
          }
        }
      },
      orderBy: [asc(appointments.appointmentDate), asc(appointments.startTime)],
      limit,
      offset
    })

    const countResult = await db
      .select({ count: sql<number>`count(*)` })
      .from(appointments)
      .where(eq(appointments.customerId, customerId))

    const total = countResult[0]?.count || 0

    return {
      appointments: appointmentsList,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    }
  }

  async getBusinessAppointments(businessId: string, userId: string, page: number = 1, limit: number = 20, date?: string) {
    // Verify user owns the business
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, businessId),
        eq(businesses.userId, userId)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or you do not have permission')
    }

    const offset = (page - 1) * limit

    const conditions = [eq(appointments.businessId, businessId)]
    if (date) {
      conditions.push(eq(appointments.appointmentDate, date))
    }

    const appointmentsList = await db.query.appointments.findMany({
      where: and(...conditions),
      with: {
        customer: {
          columns: {
            id: true,
            name: true,
            email: true,
            phone: true
          }
        },
        service: {
          columns: {
            id: true,
            name: true,
            durationMinutes: true
          }
        }
      },
      orderBy: [asc(appointments.appointmentDate), asc(appointments.startTime)],
      limit,
      offset
    })

    const countResult = await db
      .select({ count: sql<number>`count(*)` })
      .from(appointments)
      .where(and(...conditions))

    const total = countResult[0]?.count || 0

    return {
      appointments: appointmentsList,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    }
  }

  async getAvailableTimeSlots(query: TimeSlotQuery): Promise<AvailableTimeSlot[]> {
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, query.businessId),
        eq(businesses.isActive, true),
        eq(businesses.isVerified, true)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or not available')
    }

    // Get service duration if serviceId provided
    let durationMinutes = query.durationMinutes || 30 // Default 30 minutes
    if (query.serviceId) {
      const service = await db.query.services.findFirst({
        where: and(
          eq(services.id, query.serviceId),
          eq(services.businessId, query.businessId),
          eq(services.isActive, true)
        )
      })

      if (service) {
        durationMinutes = service.durationMinutes
      }
    }

    // Get business hours for the requested date
    const appointmentDate = new Date(query.date)
    const dayOfWeek = appointmentDate.getDay()
    const businessDayHours = await db.query.businessHours.findFirst({
      where: and(
        eq(businessHours.businessId, query.businessId),
        eq(businessHours.dayOfWeek, dayOfWeek)
      )
    })

    if (!businessDayHours || businessDayHours.isClosed) {
      return [] // No available slots if business is closed
    }

    // Get existing appointments for the date
    const existingAppointments = await db.query.appointments.findMany({
      where: and(
        eq(appointments.businessId, query.businessId),
        eq(appointments.appointmentDate, query.date),
        eq(appointments.status, 'pending')
      ),
      orderBy: asc(appointments.startTime)
    })

    // Generate time slots
    const timeSlots: AvailableTimeSlot[] = []
    const slotDuration = durationMinutes
    const openTime = businessDayHours.openTime
    const closeTime = businessDayHours.closeTime

    // Parse time strings to minutes since midnight
    const parseTime = (timeStr: string): number => {
      const [hours, minutes] = timeStr.split(':').map(Number)
      return (hours || 0) * 60 + (minutes || 0)
    }

    const formatTime = (minutes: number): string => {
      const hours = Math.floor(minutes / 60)
      const mins = minutes % 60
      return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}`
    }

    const openMinutes = parseTime(openTime || '09:00')
    const closeMinutes = parseTime(closeTime || '17:00')

    // Group appointments by time slot
    const bookedSlots = new Map<string, Set<number>>() // timeSlot -> booked service points

    existingAppointments.forEach(appointment => {
      const slotKey = `${appointment.startTime}-${appointment.endTime}`
      if (!bookedSlots.has(slotKey)) {
        bookedSlots.set(slotKey, new Set())
      }
      if (appointment.servicePointNumber) {
        bookedSlots.get(slotKey)!.add(appointment.servicePointNumber)
      }
    })

    // Generate available time slots
    for (let start = openMinutes; start + slotDuration <= closeMinutes; start += 15) { // 15-minute intervals
      const end = start + slotDuration
      const startTimeStr = formatTime(start)
      const endTimeStr = formatTime(end)

      const slotKey = `${startTimeStr}-${endTimeStr}`
      const bookedServicePoints = bookedSlots.get(slotKey) || new Set()

      // Calculate available service points
      const availableServicePoints: number[] = []
      const servicePointsCount = business.servicePointsCount || 1
      for (let i = 1; i <= servicePointsCount; i++) {
        if (!bookedServicePoints.has(i)) {
          availableServicePoints.push(i)
        }
      }

      if (availableServicePoints.length > 0) {
        timeSlots.push({
          date: query.date,
          startTime: startTimeStr,
          endTime: endTimeStr,
          availableServicePoints
        })
      }
    }

    return timeSlots
  }

  async getAppointmentStats(businessId: string, userId: string) {
    // Verify user owns the business
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, businessId),
        eq(businesses.userId, userId)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or you do not have permission')
    }

    const stats = await db
      .select({
        total: sql<number>`count(*)`,
        scheduled: sql<number>`count(*) filter (where status = 'scheduled')`,
        confirmed: sql<number>`count(*) filter (where status = 'confirmed')`,
        inProgress: sql<number>`count(*) filter (where status = 'in_progress')`,
        completed: sql<number>`count(*) filter (where status = 'completed')`,
        cancelled: sql<number>`count(*) filter (where status = 'cancelled')`,
        noShow: sql<number>`count(*) filter (where status = 'no_show')`,
        today: sql<number>`count(*) filter (where appointment_date = CURRENT_DATE)`,
        upcoming: sql<number>`count(*) filter (where appointment_date > CURRENT_DATE)`
      })
      .from(appointments)
      .where(eq(appointments.businessId, businessId))

    return stats[0] || {}
  }
}

export const appointmentService = new AppointmentService()