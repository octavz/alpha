import { eq, and, sql, desc, asc, like, or, gte, lte } from 'drizzle-orm'
import { db } from '../db/client'
import { 
  users, 
  businesses, 
  categories, 
  regions,
  reviews,
  appointments
} from '../db/schema'
import { 
  ValidationError, 
  NotFoundError,
  AuthorizationError
} from '../middleware/error-handler'

export interface BusinessApprovalInput {
  businessId: string
  status: 'approved' | 'rejected'
  rejectionReason?: string
}

export interface UserBanInput {
  userId: string
  isBanned: boolean
  banReason?: string
}

export interface ReviewApprovalInput {
  reviewId: string
  isApproved: boolean
}

export interface PlatformStatsInput {
  startDate?: Date
  endDate?: Date
  regionId?: string
}

export class AdminService {
  // Business Management
  async getPendingBusinesses(page: number = 1, limit: number = 20) {
    const offset = (page - 1) * limit

    const businessesList = await db.query.businesses.findMany({
      where: eq(businesses.verificationStatus, 'pending'),
      with: {
        category: true,
        region: true,
        user: {
          columns: {
            id: true,
            name: true,
            email: true,
            phone: true
          }
        }
      },
      orderBy: desc(businesses.createdAt),
      limit,
      offset
    })

    const [countResult] = await db
      .select({ count: sql<number>`count(*)` })
      .from(businesses)
      .where(eq(businesses.verificationStatus, 'pending'))

    const total = countResult.count

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

  async approveBusiness(input: BusinessApprovalInput) {
    const business = await db.query.businesses.findFirst({
      where: eq(businesses.id, input.businessId)
    })

    if (!business) {
      throw new NotFoundError('Business not found')
    }

    const updateData: any = {
      verificationStatus: input.status,
      isVerified: input.status === 'approved',
      isActive: input.status === 'approved'
    }

    if (input.status === 'rejected' && input.rejectionReason) {
      // Store rejection reason (you might want to add a column for this)
      updateData.rejectionReason = input.rejectionReason
    }

    const [updatedBusiness] = await db.update(businesses)
      .set(updateData)
      .where(eq(businesses.id, input.businessId))
      .returning()

    return updatedBusiness
  }

  async getBusinessVerificationStats() {
    const stats = await db
      .select({
        status: businesses.verificationStatus,
        count: sql<number>`count(*)`
      })
      .from(businesses)
      .groupBy(businesses.verificationStatus)

    return stats
  }

  // User Management
  async getUsers(page: number = 1, limit: number = 20, search?: string) {
    const offset = (page - 1) * limit

    const conditions = []
    if (search) {
      conditions.push(
        or(
          like(users.email, `%${search}%`),
          like(users.name, `%${search}%`),
          like(users.phone, `%${search}%`)
        )
      )
    }

    const where = conditions.length > 0 ? and(...conditions) : undefined

    const usersList = await db.query.users.findMany({
      where,
      columns: {
        id: true,
        email: true,
        name: true,
        phone: true,
        role: true,
        emailVerified: true,
        regionId: true,
        isBanned: true,
        createdAt: true,
        updatedAt: true
      },
      orderBy: desc(users.createdAt),
      limit,
      offset
    })

    const [countResult] = await db
      .select({ count: sql<number>`count(*)` })
      .from(users)
      .where(where || undefined)

    const total = countResult.count

    return {
      users: usersList,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    }
  }

  async updateUserBanStatus(input: UserBanInput) {
    const user = await db.query.users.findFirst({
      where: eq(users.id, input.userId)
    })

    if (!user) {
      throw new NotFoundError('User not found')
    }

    // Prevent banning admin users
    if (user.role === 'admin' && input.isBanned) {
      throw new AuthorizationError('Cannot ban admin users')
    }

    const [updatedUser] = await db.update(users)
      .set({ 
        isBanned: input.isBanned,
        updatedAt: new Date()
      })
      .where(eq(users.id, input.userId))
      .returning()

    return updatedUser
  }

  async getUserStats() {
    const stats = await db
      .select({
        role: users.role,
        count: sql<number>`count(*)`,
        bannedCount: sql<number>`count(*) filter (where is_banned = true)`
      })
      .from(users)
      .groupBy(users.role)

    return stats
  }

  // Review Management
  async getPendingReviews(page: number = 1, limit: number = 20) {
    const offset = (page - 1) * limit

    const reviewsList = await db.query.reviews.findMany({
      where: eq(reviews.isApproved, false),
      with: {
        business: {
          columns: {
            id: true,
            name: true
          }
        },
        customer: {
          columns: {
            id: true,
            name: true,
            email: true
          }
        }
      },
      orderBy: desc(reviews.createdAt),
      limit,
      offset
    })

    const [countResult] = await db
      .select({ count: sql<number>`count(*)` })
      .from(reviews)
      .where(eq(reviews.isApproved, false))

    const total = countResult.count

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

  async approveReview(input: ReviewApprovalInput) {
    const review = await db.query.reviews.findFirst({
      where: eq(reviews.id, input.reviewId)
    })

    if (!review) {
      throw new NotFoundError('Review not found')
    }

    const [updatedReview] = await db.update(reviews)
      .set({ 
        isApproved: input.isApproved,
        updatedAt: new Date()
      })
      .where(eq(reviews.id, input.reviewId))
      .returning()

    // Update business rating if review is approved
    if (input.isApproved) {
      await this.updateBusinessRating(review.businessId)
    }

    return updatedReview
  }

  private async updateBusinessRating(businessId: string) {
    const ratingStats = await db
      .select({
        averageRating: sql<number>`coalesce(avg(rating), 0)`,
        totalReviews: sql<number>`count(*)`
      })
      .from(reviews)
      .where(and(
        eq(reviews.businessId, businessId),
        eq(reviews.isApproved, true)
      ))

    const stats = ratingStats[0]

    if (stats) {
      await db.update(businesses)
        .set({
          // Note: businesses table doesn't have rating columns yet
          // We need to add them or calculate on the fly
          updatedAt: new Date()
        })
        .where(eq(businesses.id, businessId))
    }
  }

  // Platform Analytics
  async getPlatformStats(input: PlatformStatsInput = {}) {
    const conditions = []

    if (input.startDate) {
      conditions.push(gte(businesses.createdAt, input.startDate))
    }
    if (input.endDate) {
      conditions.push(lte(businesses.createdAt, input.endDate))
    }
    if (input.regionId) {
      conditions.push(eq(businesses.regionId, input.regionId))
    }

    const where = conditions.length > 0 ? and(...conditions) : undefined

    // Business stats
    const businessStats = await db
      .select({
        total: sql<number>`count(*)`,
        verified: sql<number>`count(*) filter (where is_verified = true)`,
        active: sql<number>`count(*) filter (where is_active = true)`,
        pending: sql<number>`count(*) filter (where verification_status = 'pending')`
      })
      .from(businesses)
      .where(where || undefined)

    // User stats
    const userStats = await db
      .select({
        total: sql<number>`count(*)`,
        customers: sql<number>`count(*) filter (where role = 'customer')`,
        businessAdmins: sql<number>`count(*) filter (where role = 'business_admin')`,
        businessStaff: sql<number>`count(*) filter (where role = 'business_staff')`,
        admins: sql<number>`count(*) filter (where role = 'admin')`,
        banned: sql<number>`count(*) filter (where is_banned = true)`
      })
      .from(users)

    // Appointment stats
    const appointmentStats = await db
      .select({
        total: sql<number>`count(*)`,
        upcoming: sql<number>`count(*) filter (where appointment_date > now())`,
        completed: sql<number>`count(*) filter (where appointment_date <= now() and status = 'completed')`,
        cancelled: sql<number>`count(*) filter (where status = 'cancelled')`
      })
      .from(appointments)

    // Review stats
    const reviewStats = await db
      .select({
        total: sql<number>`count(*)`,
        approved: sql<number>`count(*) filter (where is_approved = true)`,
        pending: sql<number>`count(*) filter (where is_approved = false)`,
        averageRating: sql<number>`coalesce(avg(rating), 0)`
      })
      .from(reviews)

    // Growth stats (last 30 days)
    const thirtyDaysAgo = new Date()
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)

    const growthStats = await db
      .select({
        newBusinesses: sql<number>`count(*) filter (where created_at >= ${thirtyDaysAgo})`,
        newUsers: sql<number>`count(*) filter (where created_at >= ${thirtyDaysAgo})`,
        newAppointments: sql<number>`count(*) filter (where created_at >= ${thirtyDaysAgo})`,
        newReviews: sql<number>`count(*) filter (where created_at >= ${thirtyDaysAgo})`
      })
      .from(businesses) // We'll need to combine multiple tables

    return {
      businessStats: businessStats[0] || {},
      userStats: userStats[0] || {},
      appointmentStats: appointmentStats[0] || {},
      reviewStats: reviewStats[0] || {},
      growthStats: growthStats[0] || {},
      timestamp: new Date()
    }
  }

  // Region Management
  async getRegions() {
    const regionsList = await db.query.regions.findMany({
      orderBy: asc(regions.name)
    })

    return regionsList
  }

  async updateRegionStatus(regionId: string, isActive: boolean) {
    const region = await db.query.regions.findFirst({
      where: eq(regions.id, regionId)
    })

    if (!region) {
      throw new NotFoundError('Region not found')
    }

    const [updatedRegion] = await db.update(regions)
      .set({ isActive })
      .where(eq(regions.id, regionId))
      .returning()

    return updatedRegion
  }

  // Category Management
  async getCategories() {
    const categoriesList = await db.query.categories.findMany({
      where: eq(categories.isActive, true),
      orderBy: asc(categories.sortOrder)
    })

    return categoriesList
  }

  async updateCategoryStatus(categoryId: string, isActive: boolean) {
    const category = await db.query.categories.findFirst({
      where: eq(categories.id, categoryId)
    })

    if (!category) {
      throw new NotFoundError('Category not found')
    }

    const [updatedCategory] = await db.update(categories)
      .set({ isActive })
      .where(eq(categories.id, categoryId))
      .returning()

    return updatedCategory
  }
}

export const adminService = new AdminService()