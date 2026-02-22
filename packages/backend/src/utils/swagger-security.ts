/**
 * Utility functions to add Swagger security metadata to routes
 */

import { Elysia } from 'elysia'

/**
 * Add security metadata to a route configuration
 */
export function withSecurity(
  config: any,
  options: {
    tags: string[]
    description: string
    security?: Array<{ bearerAuth: string[] }>
  }
) {
  return {
    ...config,
    detail: {
      tags: options.tags,
      security: options.security || [{ bearerAuth: [] }],
      description: options.description
    }
  }
}

/**
 * Public endpoint (no authentication required)
 */
export function publicEndpoint(
  config: any,
  options: {
    tags: string[]
    description: string
  }
) {
  return {
    ...config,
    detail: {
      tags: options.tags,
      security: [], // Empty array means no auth required
      description: options.description
    }
  }
}

/**
 * Customer-only endpoint
 */
export function customerEndpoint(
  config: any,
  options: {
    tags: string[]
    description: string
  }
) {
  return withSecurity(config, {
    ...options,
    description: `${options.description} Customer role required.`
  })
}

/**
 * Business owner endpoint
 */
export function businessOwnerEndpoint(
  config: any,
  options: {
    tags: string[]
    description: string
  }
) {
  return withSecurity(config, {
    ...options,
    description: `${options.description} Business owner role required.`
  })
}

/**
 * Admin-only endpoint
 */
export function adminEndpoint(
  config: any,
  options: {
    tags: string[]
    description: string
  }
) {
  return withSecurity(config, {
    ...options,
    description: `${options.description} Admin role required.`
  })
}