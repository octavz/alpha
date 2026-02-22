import { db } from '../db/client'
import { businesses, services, businessHours, users, appointments, reviews } from '../db/schema'
import { like } from 'drizzle-orm'

async function cleanupTestData() {
  console.log('Starting cleanup of test data...')

  // Delete test business users (excluding admin)
  const testUsers = await db.delete(users)
    .where(like(users.email, 'business%@test.com'))
    .returning()
  console.log(`Deleted ${testUsers.length} test business users`)

  // Delete appointments
  const deletedAppointments = await db.delete(appointments).returning()
  console.log(`Deleted ${deletedAppointments.length} appointments`)

  // Delete reviews
  const deletedReviews = await db.delete(reviews).returning()
  console.log(`Deleted ${deletedReviews.length} reviews`)

  // Delete services
  const deletedServices = await db.delete(services).returning()
  console.log(`Deleted ${deletedServices.length} services`)

  // Delete business hours
  const deletedBusinessHours = await db.delete(businessHours).returning()
  console.log(`Deleted ${deletedBusinessHours.length} business hours`)

  // Delete businesses (excluding any that might be manually created)
  const deletedBusinesses = await db.delete(businesses).returning()
  console.log(`Deleted ${deletedBusinesses.length} businesses`)

  console.log('✅ Cleanup completed successfully!')
}

// Run cleanup
cleanupTestData()
  .then(() => {
    console.log('🎉 Cleanup completed!')
    process.exit(0)
  })
  .catch((error) => {
    console.error('❌ Cleanup failed:', error)
    process.exit(1)
  })