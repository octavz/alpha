// Test setup file
import { jest } from '@jest/globals'

// Global test timeout
jest.setTimeout(10000)

// Clean up after tests
afterEach(() => {
  jest.clearAllMocks()
})

afterAll(() => {
  jest.restoreAllMocks()
})