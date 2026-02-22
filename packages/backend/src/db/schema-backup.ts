import { pgTable, text, timestamp, uuid, boolean, index, integer, decimal, date, time } from 'drizzle-orm/pg-core'
import { relations } from 'drizzle-orm'

// Users table
export const users = pgTable('users', {
  id: uuid('id').primaryKey().defaultRandom(),
  email: text('email').unique().notNull(),
  passwordHash: text('password_hash'),
  googleId: text('google_id').unique(),
  name: text('name'),
  phone: text('phone'),
  role: text('role').default('customer').$type<'customer' | 'business_admin' | 'business_staff' | 'admin'>(),
  emailVerified: boolean('email_verified').default(false),
  avatarUrl: text('avatar_url'),
  regionId: uuid('region_id').references(() => regions.id),
  isBanned: boolean('is_banned').default(false),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull()
}, (table) => ({
  emailIdx: index('users_email_idx').on(table.email),
  googleIdIdx: index('users_google_id_idx').on(table.googleId),
  roleIdx: index('users_role_idx').on(table.role),
  regionIdIdx: index('users_region_id_idx').on(table.regionId)
}))

// Sessions table
export const sessions = pgTable('sessions', {
  id: uuid('id').primaryKey().defaultRandom(),
  userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  token: text('token').notNull(),
  refreshToken: text('refresh_token').notNull(),
  userAgent: text('user_agent'),
  ipAddress: text('ip_address'),
  expiresAt: timestamp('expires_at').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  userIdIdx: index('sessions_user_id_idx').on(table.userId),
  tokenIdx: index('sessions_token_idx').on(table.token),
  refreshTokenIdx: index('sessions_refresh_token_idx').on(table.refreshToken)
}))

// Email verifications table
export const emailVerifications = pgTable('email_verifications', {
  id: uuid('id').primaryKey().defaultRandom(),
  userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  token: text('token').notNull(),
  expiresAt: timestamp('expires_at').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  userIdIdx: index('email_verifications_user_id_idx').on(table.userId),
  tokenIdx: index('email_verifications_token_idx').on(table.token)
}))

// Password resets table
export const passwordResets = pgTable('password_resets', {
  id: uuid('id').primaryKey().defaultRandom(),
  userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  token: text('token').notNull(),
  expiresAt: timestamp('expires_at').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  userIdIdx: index('password_resets_user_id_idx').on(table.userId),
  tokenIdx: index('password_resets_token_idx').on(table.token)
}))

// Relations
export const usersRelations = relations(users, ({ many, one }) => ({
  sessions: many(sessions),
  emailVerifications: many(emailVerifications),
  passwordResets: many(passwordResets),
  region: one(regions, {
    fields: [users.regionId],
    references: [regions.id]
  }),
  businesses: many(businesses),
  appointmentsAsCustomer: many(appointments, { relationName: 'customerAppointments' }),
  appointmentsAsBusiness: many(appointments, { relationName: 'businessAppointments' }),
  reviews: many(reviews)
}))

export const sessionsRelations = relations(sessions, ({ one }) => ({
  user: one(users, {
    fields: [sessions.userId],
    references: [users.id]
  })
}))

export const emailVerificationsRelations = relations(emailVerifications, ({ one }) => ({
  user: one(users, {
    fields: [emailVerifications.userId],
    references: [users.id]
  })
}))

export const passwordResetsRelations = relations(passwordResets, ({ one }) => ({
  user: one(users, {
    fields: [passwordResets.userId],
    references: [users.id]
  })
}))

export const regionsRelations = relations(regions, ({ many }) => ({
  users: many(users),
  businesses: many(businesses)
}))

export const categoriesRelations = relations(categories, ({ many, one }) => ({
  parent: one(categories, {
    fields: [categories.parentId],
    references: [categories.id],
    relationName: 'parentCategory'
  }),
  children: many(categories, { relationName: 'childCategories' }),
  businesses: many(businesses)
}))

export const businessesRelations = relations(businesses, ({ many, one }) => ({
  user: one(users, {
    fields: [businesses.userId],
    references: [users.id]
  }),
  category: one(categories, {
    fields: [businesses.categoryId],
    references: [categories.id]
  }),
  region: one(regions, {
    fields: [businesses.regionId],
    references: [regions.id]
  }),
  services: many(services),
  appointments: many(appointments),
  reviews: many(reviews),
  businessHours: many(businessHours)
}))

export const servicesRelations = relations(services, ({ many, one }) => ({
  business: one(businesses, {
    fields: [services.businessId],
    references: [businesses.id]
  }),
  appointments: many(appointments)
}))

export const appointmentsRelations = relations(appointments, ({ one }) => ({
  business: one(businesses, {
    fields: [appointments.businessId],
    references: [businesses.id],
    relationName: 'businessAppointments'
  }),
  customer: one(users, {
    fields: [appointments.customerId],
    references: [users.id],
    relationName: 'customerAppointments'
  }),
  service: one(services, {
    fields: [appointments.serviceId],
    references: [services.id]
  }),
  review: one(reviews, {
    fields: [appointments.id],
    references: [reviews.appointmentId]
  })
}))

export const reviewsRelations = relations(reviews, ({ one }) => ({
  business: one(businesses, {
    fields: [reviews.businessId],
    references: [businesses.id]
  }),
  customer: one(users, {
    fields: [reviews.customerId],
    references: [users.id]
  }),
  appointment: one(appointments, {
    fields: [reviews.appointmentId],
    references: [appointments.id]
  })
}))

export const businessHoursRelations = relations(businessHours, ({ one }) => ({
  business: one(businesses, {
    fields: [businessHours.businessId],
    references: [businesses.id]
  })
}))

// Regions table
export const regions = pgTable('regions', {
  id: uuid('id').primaryKey().defaultRandom(),
  name: text('name').notNull(),
  code: text('code').unique().notNull(),
  country: text('country').notNull(),
  timezone: text('timezone').notNull(),
  isActive: boolean('is_active').default(true),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  codeIdx: index('regions_code_idx').on(table.code),
  countryIdx: index('regions_country_idx').on(table.country)
}))

// Categories table (fixed hierarchy)
export const categories = pgTable('categories', {
  id: uuid('id').primaryKey().defaultRandom(),
  name: text('name').notNull(),
  slug: text('slug').unique().notNull(),
  description: text('description'),
  icon: text('icon'),
  parentId: uuid('parent_id'),
  sortOrder: integer('sort_order').default(0),
  isActive: boolean('is_active').default(true),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  slugIdx: index('categories_slug_idx').on(table.slug),
  parentIdIdx: index('categories_parent_id_idx').on(table.parentId)
}))

// Businesses table
export const businesses = pgTable('businesses', {
  id: uuid('id').primaryKey().defaultRandom(),
  userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  name: text('name').notNull(),
  slug: text('slug').unique().notNull(),
  description: text('description'),
  categoryId: uuid('category_id').references(() => categories.id),
  regionId: uuid('region_id').references(() => regions.id, { onDelete: 'restrict' }).notNull(),
  
  // Contact Information
  email: text('email'),
  phone: text('phone'),
  website: text('website'),
  
  // Address (for geolocation)
  addressLine1: text('address_line1'),
  addressLine2: text('address_line2'),
  city: text('city'),
  state: text('state'),
  zipCode: text('zip_code'),
  country: text('country'),
  latitude: decimal('latitude', { precision: 10, scale: 8 }),
  longitude: decimal('longitude', { precision: 11, scale: 8 }),
  
  // Business Details
  logoUrl: text('logo_url'),
  coverImageUrl: text('cover_image_url'),
  isVerified: boolean('is_verified').default(false),
  isActive: boolean('is_active').default(true),
  isFeatured: boolean('is_featured').default(false),
  verificationStatus: text('verification_status').default('pending').$type<'pending' | 'approved' | 'rejected'>(),
  
  // Service Points (multiple chairs/employees)
  servicePointsCount: integer('service_points_count').default(1),
  
  // Metadata
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull()
}, (table) => ({
  regionIdIdx: index('businesses_region_id_idx').on(table.regionId),
  categoryIdIdx: index('businesses_category_id_idx').on(table.categoryId),
  verificationStatusIdx: index('businesses_verification_status_idx').on(table.verificationStatus),
  locationIdx: index('businesses_location_idx').on(table.latitude, table.longitude),
  slugIdx: index('businesses_slug_idx').on(table.slug)
}))

// Services table
export const services = pgTable('services', {
  id: uuid('id').primaryKey().defaultRandom(),
  businessId: uuid('business_id').references(() => businesses.id, { onDelete: 'cascade' }).notNull(),
  name: text('name').notNull(),
  description: text('description'),
  durationMinutes: integer('duration_minutes').notNull(),
  price: decimal('price', { precision: 10, scale: 2 }),
  isActive: boolean('is_active').default(true),
  sortOrder: integer('sort_order').default(0),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  businessIdIdx: index('services_business_id_idx').on(table.businessId)
}))

// Appointments table
export const appointments = pgTable('appointments', {
  id: uuid('id').primaryKey().defaultRandom(),
  businessId: uuid('business_id').references(() => businesses.id, { onDelete: 'cascade' }).notNull(),
  customerId: uuid('customer_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  serviceId: uuid('service_id').references(() => services.id),
  
  // Appointment Details
  appointmentDate: date('appointment_date').notNull(),
  startTime: time('start_time').notNull(),
  endTime: time('end_time').notNull(),
  servicePointNumber: integer('service_point_number'),
  
  // Customer Information (cached for historical reference)
  customerName: text('customer_name').notNull(),
  customerEmail: text('customer_email').notNull(),
  customerPhone: text('customer_phone'),
  customerNotes: text('customer_notes'),
  
  // Status
  status: text('status').default('pending').$type<'pending' | 'confirmed' | 'completed' | 'cancelled' | 'no_show'>(),
  
  // Metadata
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
  cancelledAt: timestamp('cancelled_at'),
  cancelledReason: text('cancelled_reason')
}, (table) => ({
  businessIdIdx: index('appointments_business_id_idx').on(table.businessId),
  customerIdIdx: index('appointments_customer_id_idx').on(table.customerId),
  dateStatusIdx: index('appointments_date_status_idx').on(table.appointmentDate, table.status),
  businessDateIdx: index('appointments_business_date_idx').on(table.businessId, table.appointmentDate)
}))

// Reviews table
export const reviews = pgTable('reviews', {
  id: uuid('id').primaryKey().defaultRandom(),
  businessId: uuid('business_id').references(() => businesses.id, { onDelete: 'cascade' }).notNull(),
  customerId: uuid('customer_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  appointmentId: uuid('appointment_id').references(() => appointments.id, { onDelete: 'set null' }),
  
  // Review Details
  rating: integer('rating').notNull(),
  title: text('title'),
  comment: text('comment'),
  isApproved: boolean('is_approved').default(false),
  isFeatured: boolean('is_featured').default(false),
  
  // Metadata
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull()
}, (table) => ({
  businessIdIdx: index('reviews_business_id_idx').on(table.businessId),
  customerIdIdx: index('reviews_customer_id_idx').on(table.customerId),
  ratingIdx: index('reviews_rating_idx').on(table.rating),
  isApprovedIdx: index('reviews_is_approved_idx').on(table.isApproved)
}))

// Business hours table
export const businessHours = pgTable('business_hours', {
  id: uuid('id').primaryKey().defaultRandom(),
  businessId: uuid('business_id').references(() => businesses.id, { onDelete: 'cascade' }).notNull(),
  dayOfWeek: integer('day_of_week').notNull(),
  openTime: time('open_time'),
  closeTime: time('close_time'),
  isClosed: boolean('is_closed').default(false),
  createdAt: timestamp('created_at').defaultNow().notNull()
}, (table) => ({
  businessIdIdx: index('business_hours_business_id_idx').on(table.businessId)
}))

// Export types
export type User = typeof users.$inferSelect
export type NewUser = typeof users.$inferInsert
export type Session = typeof sessions.$inferSelect
export type NewSession = typeof sessions.$inferInsert
export type EmailVerification = typeof emailVerifications.$inferSelect
export type NewEmailVerification = typeof emailVerifications.$inferInsert
export type PasswordReset = typeof passwordResets.$inferSelect
export type NewPasswordReset = typeof passwordResets.$inferInsert

export type Region = typeof regions.$inferSelect
export type NewRegion = typeof regions.$inferInsert
export type Category = typeof categories.$inferSelect
export type NewCategory = typeof categories.$inferInsert
export type Business = typeof businesses.$inferSelect
export type NewBusiness = typeof businesses.$inferInsert
export type Service = typeof services.$inferSelect
export type NewService = typeof services.$inferInsert
export type Appointment = typeof appointments.$inferSelect
export type NewAppointment = typeof appointments.$inferInsert
export type Review = typeof reviews.$inferSelect
export type NewReview = typeof reviews.$inferInsert
export type BusinessHour = typeof businessHours.$inferSelect
export type NewBusinessHour = typeof businessHours.$inferInsert