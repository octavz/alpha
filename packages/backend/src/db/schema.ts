import { pgTable, text, timestamp, uuid, boolean, index } from 'drizzle-orm/pg-core'
import { relations } from 'drizzle-orm'

// Users table
export const users = pgTable('users', {
  id: uuid('id').primaryKey().defaultRandom(),
  email: text('email').unique().notNull(),
  passwordHash: text('password_hash'),
  googleId: text('google_id').unique(),
  name: text('name'),
  emailVerified: boolean('email_verified').default(false),
  avatarUrl: text('avatar_url'),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull()
}, (table) => ({
  emailIdx: index('users_email_idx').on(table.email),
  googleIdIdx: index('users_google_id_idx').on(table.googleId)
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
export const usersRelations = relations(users, ({ many }) => ({
  sessions: many(sessions),
  emailVerifications: many(emailVerifications),
  passwordResets: many(passwordResets)
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

// Export types
export type User = typeof users.$inferSelect
export type NewUser = typeof users.$inferInsert
export type Session = typeof sessions.$inferSelect
export type NewSession = typeof sessions.$inferInsert
export type EmailVerification = typeof emailVerifications.$inferSelect
export type NewEmailVerification = typeof emailVerifications.$inferInsert
export type PasswordReset = typeof passwordResets.$inferSelect
export type NewPasswordReset = typeof passwordResets.$inferInsert