import { describe, test, expect, beforeAll, afterAll } from '@jest/globals'
import { db } from '../db/client'
import { businesses, users, categories, regions } from '../db/schema'
import { eq, sql } from 'drizzle-orm'

describe('Business Directory API Integration Tests', () => {
  beforeAll(async () => {
    // Ensure database is connected
    await db.execute(sql`SELECT 1`)
  })

  test('Database has seed data', async () => {
    // Check categories
    const categoryCount = await db.select().from(categories)
    expect(categoryCount.length).toBeGreaterThan(0)
    console.log(`Categories: ${categoryCount.length}`)

    // Check regions
    const regionCount = await db.select().from(regions)
    expect(regionCount.length).toBeGreaterThan(0)
    console.log(`Regions: ${regionCount.length}`)

    // Check businesses
    const businessCount = await db.select().from(businesses)
    expect(businessCount.length).toBeGreaterThan(0)
    console.log(`Businesses: ${businessCount.length}`)

    // Check business users
    const businessUsers = await db.select().from(users).where(eq(users.role, 'business_admin'))
    expect(businessUsers.length).toBeGreaterThan(0)
    console.log(`Business Users: ${businessUsers.length}`)
  })

  test('Business search functionality', async () => {
    // Get a region to search in
    const regionsList = await db.select().from(regions).limit(1)
    expect(regionsList.length).toBeGreaterThan(0)
    
    const region = regionsList[0]
    
    // Search for businesses in this region
    const businessesInRegion = await db.select().from(businesses)
      .where(eq(businesses.regionId, region.id))
      .limit(5)
    
    expect(businessesInRegion.length).toBeGreaterThan(0)
    console.log(`Found ${businessesInRegion.length} businesses in region: ${region.name}`)
    
    // Verify business structure
    const business = businessesInRegion[0]
    expect(business).toHaveProperty('id')
    expect(business).toHaveProperty('name')
    expect(business).toHaveProperty('slug')
    expect(business).toHaveProperty('description')
    expect(business).toHaveProperty('regionId')
    expect(business).toHaveProperty('categoryId')
    expect(business).toHaveProperty('isActive')
    expect(business.isActive).toBe(true)
  })

  test('Business verification status distribution', async () => {
    const businessesList = await db.select().from(businesses)
    
    const statusCounts: Record<string, number> = {}
    businessesList.forEach(business => {
      const status = business.verificationStatus || 'unknown'
      statusCounts[status] = (statusCounts[status] || 0) + 1
    })
    
    console.log('Business verification status distribution:')
    Object.entries(statusCounts).forEach(([status, count]) => {
      console.log(`  ${status}: ${count} businesses`)
    })
    
    // Should have businesses in different statuses
    expect(Object.keys(statusCounts).length).toBeGreaterThan(0)
  })

  test('Business service points configuration', async () => {
    const businessesList = await db.select().from(businesses).limit(10)
    
    businessesList.forEach(business => {
      expect(business.servicePointsCount).toBeGreaterThanOrEqual(1)
      expect(business.servicePointsCount).toBeLessThanOrEqual(5)
    })
    
    console.log(`Checked service points for ${businessesList.length} businesses`)
  })
})