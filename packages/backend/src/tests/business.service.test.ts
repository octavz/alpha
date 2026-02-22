// Simple test file for BusinessService
// We'll create a proper test setup later

import { BusinessService } from '../services/business.service'

// Mock the database module
jest.mock('../db/client', () => ({
  db: {
    query: {
      users: {
        findFirst: jest.fn()
      },
      businesses: {
        findFirst: jest.fn(),
        findMany: jest.fn()
      },
      categories: {
        findFirst: jest.fn()
      }
    },
    insert: jest.fn().mockReturnValue({
      values: jest.fn().mockReturnValue({
        returning: jest.fn().mockResolvedValue([{}])
      })
    }),
    update: jest.fn().mockReturnValue({
      set: jest.fn().mockReturnValue({
        where: jest.fn().mockResolvedValue(undefined)
      })
    })
  }
}))

// Mock drizzle-orm
jest.mock('drizzle-orm', () => ({
  eq: jest.fn()
}))

describe('BusinessService - Basic Tests', () => {
  let businessService: BusinessService

  beforeEach(() => {
    jest.clearAllMocks()
    businessService = new BusinessService()
  })

  test('getUserBusiness should return null when user has no business', async () => {
    const mockDb = require('../db/client').db
    mockDb.query.businesses.findFirst.mockResolvedValue(null)

    const result = await businessService.getUserBusiness('user-123')
    
    expect(result).toBeNull()
    expect(mockDb.query.businesses.findFirst).toHaveBeenCalled()
  })

  test('getBusiness should throw error when business not found', async () => {
    const mockDb = require('../db/client').db
    mockDb.query.businesses.findFirst.mockResolvedValue(null)

    await expect(businessService.getBusiness('non-existent-id'))
      .rejects
      .toThrow('Business not found')
  })
})

// Export for test runner
export {}