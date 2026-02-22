import { eq, and, sql, desc, asc } from 'drizzle-orm'
import { db } from '../db/client'
import { 
  users, 
  businesses, 
  categories, 
  services, 
  businessHours,
  reviews
} from '../db/schema'
import { 
  ValidationError, 
  NotFoundError, 
  ConflictError
} from '../middleware/error-handler'

export interface CreateBusinessInput {
  name: string
  description: string
  addressLine1: string
  addressLine2?: string
  city: string
  state: string
  zipCode: string
  country: string
  latitude: number
  longitude: number
  categoryId: string
  phone: string
  email: string
  website?: string
  logoUrl?: string
  servicePointsCount: number
}

export interface UpdateBusinessInput {
  name?: string
  description?: string
  addressLine1?: string
  addressLine2?: string
  city?: string
  state?: string
  zipCode?: string
  country?: string
  latitude?: number
  longitude?: number
  categoryId?: string
  phone?: string
  email?: string
  website?: string
  logoUrl?: string
  servicePointsCount?: number
  isActive?: boolean
}

export interface BusinessHoursInput {
  dayOfWeek: number
  openTime: string
  closeTime: string
  isClosed: boolean
}

export interface CreateServiceInput {
  name: string
  description: string
  durationMinutes: number
  price?: number
  isActive: boolean
}

export interface SearchBusinessesInput {
  regionId: string
  categoryId?: string
  query?: string
  page?: number
  limit?: number
  sortBy?: 'name' | 'createdAt'
}

export class BusinessService {
  async createBusiness(userId: string, input: CreateBusinessInput) {
    // Check if user already has a business
    const existingBusiness = await db.query.businesses.findFirst({
      where: eq(businesses.userId, userId)
    })

    if (existingBusiness) {
      throw new ConflictError('User already has a business')
    }

    // Validate category exists
    const category = await db.query.categories.findFirst({
      where: eq(categories.id, input.categoryId)
    })

    if (!category) {
      throw new ValidationError('Invalid category')
    }

    // Validate region from user
    const user = await db.query.users.findFirst({
      where: eq(users.id, userId)
    })

    if (!user || !user.regionId) {
      throw new ValidationError('User must have a region set')
    }

    // Generate slug from name
    const slug = input.name.toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '')

    // Create business
    const businessData: any = {
      userId,
      name: input.name,
      slug,
      description: input.description,
      categoryId: input.categoryId,
      regionId: user.regionId,
      email: input.email,
      phone: input.phone,
      addressLine1: input.addressLine1,
      city: input.city,
      state: input.state,
      zipCode: input.zipCode,
      country: input.country,
      latitude: input.latitude.toString(),
      longitude: input.longitude.toString(),
      servicePointsCount: input.servicePointsCount,
      isActive: false,
      isVerified: false,
      verificationStatus: 'pending'
    }

    // Add optional fields if provided
    if (input.website) businessData.website = input.website
    if (input.addressLine2) businessData.addressLine2 = input.addressLine2
    if (input.logoUrl) businessData.logoUrl = input.logoUrl

    const [business] = await db.insert(businesses).values(businessData).returning()

    if (!business) {
      throw new Error('Failed to create business')
    }

    // Update user role to business_admin
    await db.update(users)
      .set({ role: 'business_admin' })
      .where(eq(users.id, userId))

    return business
  }

  async updateBusiness(businessId: string, userId: string, input: UpdateBusinessInput) {
    // Check if business exists and user owns it
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, businessId),
        eq(businesses.userId, userId)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or you do not have permission')
    }

    // Validate category if provided
    if (input.categoryId) {
      const category = await db.query.categories.findFirst({
        where: eq(categories.id, input.categoryId)
      })

      if (!category) {
        throw new ValidationError('Invalid category')
      }
    }

    // Prepare update data
    const updateData: any = {
      updatedAt: new Date()
    }

    // Add fields that are provided
    if (input.name !== undefined) updateData.name = input.name
    if (input.description !== undefined) updateData.description = input.description
    if (input.addressLine1 !== undefined) updateData.addressLine1 = input.addressLine1
    if (input.addressLine2 !== undefined) updateData.addressLine2 = input.addressLine2
    if (input.city !== undefined) updateData.city = input.city
    if (input.state !== undefined) updateData.state = input.state
    if (input.zipCode !== undefined) updateData.zipCode = input.zipCode
    if (input.country !== undefined) updateData.country = input.country
    if (input.latitude !== undefined) updateData.latitude = input.latitude.toString()
    if (input.longitude !== undefined) updateData.longitude = input.longitude.toString()
    if (input.categoryId !== undefined) updateData.categoryId = input.categoryId
    if (input.phone !== undefined) updateData.phone = input.phone
    if (input.email !== undefined) updateData.email = input.email
    if (input.website !== undefined) updateData.website = input.website
    if (input.logoUrl !== undefined) updateData.logoUrl = input.logoUrl
    if (input.servicePointsCount !== undefined) updateData.servicePointsCount = input.servicePointsCount
    if (input.isActive !== undefined) updateData.isActive = input.isActive

    // Update business
    const [updatedBusiness] = await db.update(businesses)
      .set(updateData)
      .where(eq(businesses.id, businessId))
      .returning()

    return updatedBusiness
  }

  async getBusiness(businessId: string) {
    const business = await db.query.businesses.findFirst({
      where: eq(businesses.id, businessId),
      with: {
        category: true,
        user: {
          columns: {
            id: true,
            name: true,
            email: true,
            phone: true
          }
        }
      }
    })

    if (!business) {
      throw new NotFoundError('Business not found')
    }

    return business
  }

  async searchBusinesses(input: SearchBusinessesInput) {
    const page = input.page || 1
    const limit = input.limit || 20
    const offset = (page - 1) * limit

    // Build where conditions
    const conditions = [
      eq(businesses.regionId, input.regionId),
      eq(businesses.isActive, true),
      eq(businesses.isVerified, true),
      eq(businesses.verificationStatus, 'approved')
    ]

    if (input.categoryId) {
      conditions.push(eq(businesses.categoryId, input.categoryId))
    }

    if (input.query) {
      // Simple text search on name and description
      conditions.push(
        sql`(${businesses.name} ILIKE ${'%' + input.query + '%'} OR ${businesses.description} ILIKE ${'%' + input.query + '%'})`
      )
    }

    // Get total count
    const countResult = await db
      .select({ count: sql<number>`count(*)` })
      .from(businesses)
      .where(and(...conditions))

    const total = countResult[0]?.count || 0

    // Build order by
    let orderBy = desc(businesses.createdAt)
    if (input.sortBy === 'name') {
      orderBy = asc(businesses.name)
    }

    // Get businesses
    const businessesList = await db.query.businesses.findMany({
      where: and(...conditions),
      with: {
        category: true
      },
      limit,
      offset,
      orderBy
    })

    return {
      businesses: businessesList,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    }
  }

  async getBusinessHours(businessId: string) {
    const hours = await db.query.businessHours.findMany({
      where: eq(businessHours.businessId, businessId),
      orderBy: asc(businessHours.dayOfWeek)
    })

    return hours
  }

  async updateBusinessHours(businessId: string, userId: string, hours: BusinessHoursInput[]) {
    // Check if business exists and user owns it
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, businessId),
        eq(businesses.userId, userId)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or you do not have permission')
    }

    // Delete existing hours
    await db.delete(businessHours)
      .where(eq(businessHours.businessId, businessId))

    // Insert new hours
    const businessHoursList = hours.map(hour => ({
      businessId,
      dayOfWeek: hour.dayOfWeek,
      openTime: hour.openTime,
      closeTime: hour.closeTime,
      isClosed: hour.isClosed
    }))

    await db.insert(businessHours).values(businessHoursList)

    return this.getBusinessHours(businessId)
  }

  async createService(businessId: string, userId: string, input: CreateServiceInput) {
    // Check if business exists and user owns it
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, businessId),
        eq(businesses.userId, userId)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or you do not have permission')
    }

    const serviceData: any = {
      businessId,
      name: input.name,
      description: input.description,
      durationMinutes: input.durationMinutes,
      isActive: input.isActive
    }

    if (input.price !== undefined) {
      serviceData.price = input.price.toString()
    }

    const [service] = await db.insert(services).values(serviceData).returning()

    return service
  }

  async getBusinessServices(businessId: string) {
    const servicesList = await db.query.services.findMany({
      where: and(
        eq(services.businessId, businessId),
        eq(services.isActive, true)
      ),
      orderBy: asc(services.name)
    })

    return servicesList
  }

  async updateService(serviceId: string, userId: string, input: Partial<CreateServiceInput>) {
    // Check if service exists and user owns the business
    const service = await db.query.services.findFirst({
      where: eq(services.id, serviceId),
      with: {
        business: true
      }
    })

    if (!service || service.business.userId !== userId) {
      throw new NotFoundError('Service not found or you do not have permission')
    }

    // Prepare update data
    const updateData: any = {}

    if (input.name !== undefined) updateData.name = input.name
    if (input.description !== undefined) updateData.description = input.description
    if (input.durationMinutes !== undefined) updateData.durationMinutes = input.durationMinutes
    if (input.price !== undefined) updateData.price = input.price.toString()
    if (input.isActive !== undefined) updateData.isActive = input.isActive

    const [updatedService] = await db.update(services)
      .set(updateData)
      .where(eq(services.id, serviceId))
      .returning()

    return updatedService
  }

  async getBusinessReviews(businessId: string, page: number = 1, limit: number = 20) {
    const offset = (page - 1) * limit

    const reviewsList = await db.query.reviews.findMany({
      where: and(
        eq(reviews.businessId, businessId),
        eq(reviews.isApproved, true)
      ),
      with: {
        customer: {
          columns: {
            id: true,
            name: true,
            avatarUrl: true
          }
        }
      },
      orderBy: desc(reviews.createdAt),
      limit,
      offset
    })

    const countResult = await db
      .select({ count: sql<number>`count(*)` })
      .from(reviews)
      .where(and(
        eq(reviews.businessId, businessId),
        eq(reviews.isApproved, true)
      ))

    const total = countResult[0]?.count || 0

    return {
      reviews: reviewsList,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    }
  }

  async getBusinessStats(businessId: string, userId: string) {
    // Check if business exists and user owns it
    const business = await db.query.businesses.findFirst({
      where: and(
        eq(businesses.id, businessId),
        eq(businesses.userId, userId)
      )
    })

    if (!business) {
      throw new NotFoundError('Business not found or you do not have permission')
    }

    // Get review stats
    const reviewStatsResult = await db
      .select({
        averageRating: sql<number>`coalesce(avg(rating), 0)`,
        totalReviews: sql<number>`count(*)`
      })
      .from(reviews)
      .where(and(
        eq(reviews.businessId, businessId),
        eq(reviews.isApproved, true)
      ))

    const reviewStats = reviewStatsResult[0] || { averageRating: 0, totalReviews: 0 }

    return {
      reviewStats
    }
  }

  async getUserBusiness(userId: string) {
    const business = await db.query.businesses.findFirst({
      where: eq(businesses.userId, userId),
      with: {
        category: true
      }
    })

    return business
  }
}

export const businessService = new BusinessService()