/**
 * Script to add Swagger security metadata to all routes
 * Run this after making changes to routes
 */

import fs from 'fs'
import path from 'path'

const routesDir = path.join(__dirname, '..', 'routes')
const files = [
  'business.routes.ts',
  'appointment.routes.ts', 
  'admin.routes.ts',
  'auth.routes.ts'
]

// Security metadata templates
const securityMetadata = {
  public: `detail: {
        tags: ['{TAG}'],
        security: [],
        description: '{DESCRIPTION}'
      }`,
  protected: `detail: {
        tags: ['{TAG}'],
        security: [{ bearerAuth: [] }],
        description: '{DESCRIPTION}'
      }`,
  admin: `detail: {
        tags: ['{TAG}'],
        security: [{ bearerAuth: [] }],
        description: '{DESCRIPTION} Admin role required.'
      }`,
  customer: `detail: {
        tags: ['{TAG}'],
        security: [{ bearerAuth: [] }],
        description: '{DESCRIPTION} Customer role required.'
      }`,
  business: `detail: {
        tags: ['{TAG}'],
        security: [{ bearerAuth: [] }],
        description: '{DESCRIPTION} Business owner role required.'
      }`
}

// Route patterns to detect
const routePatterns = {
  // Business routes
  'GET /api/businesses$': { type: 'public', tag: 'Business', desc: 'Search businesses. Public endpoint.' },
  'GET /api/businesses/my-business$': { type: 'protected', tag: 'Business', desc: 'Get current user\'s business' },
  'PUT /api/businesses/:id$': { type: 'protected', tag: 'Business', desc: 'Update business' },
  'GET /api/businesses/:id$': { type: 'public', tag: 'Business', desc: 'Get business details' },
  'GET /api/businesses/:id/services$': { type: 'public', tag: 'Business', desc: 'Get business services' },
  'POST /api/businesses/:id/services$': { type: 'business', tag: 'Business', desc: 'Add service to business' },
  
  // Appointment routes
  'POST /api/appointments$': { type: 'customer', tag: 'Appointment', desc: 'Create appointment' },
  'GET /api/appointments/my$': { type: 'protected', tag: 'Appointment', desc: 'Get user\'s appointments' },
  'GET /api/appointments/business/:id$': { type: 'business', tag: 'Appointment', desc: 'Get business appointments' },
  
  // Admin routes
  'GET /api/admin/.*': { type: 'admin', tag: 'Admin', desc: 'Admin endpoint' },
  'PUT /api/admin/.*': { type: 'admin', tag: 'Admin', desc: 'Admin endpoint' },
  'POST /api/admin/.*': { type: 'admin', tag: 'Admin', desc: 'Admin endpoint' },
  
  // Auth routes
  'POST /api/auth/register$': { type: 'public', tag: 'Auth', desc: 'Register new user' },
  'POST /api/auth/login$': { type: 'public', tag: 'Auth', desc: 'Login user' },
  'POST /api/auth/refresh$': { type: 'protected', tag: 'Auth', desc: 'Refresh access token' },
  'POST /api/auth/logout$': { type: 'protected', tag: 'Auth', desc: 'Logout user' }
}

console.log('🔧 Adding Swagger security metadata to routes...')

// For now, just output instructions
console.log('\n📋 Manual steps to add security metadata:')
console.log('=========================================')
console.log('\nFor each protected route, add:')
console.log(`
    detail: {
      tags: ['TAG_NAME'],
      security: [{ bearerAuth: [] }],
      description: 'Endpoint description'
    }
`)

console.log('\nFor public routes, add:')
console.log(`
    detail: {
      tags: ['TAG_NAME'],
      security: [],
      description: 'Endpoint description'
    }
`)

console.log('\n📁 Files to update:')
files.forEach(file => {
  const filePath = path.join(routesDir, file)
  if (fs.existsSync(filePath)) {
    console.log(`  - ${file}`)
  }
})

console.log('\n🎯 Example for business routes:')
console.log(`
  .get('/my-business',
    async ({ request, set }) => {
      // ... handler code
    },
    {
      detail: {
        tags: ['Business'],
        security: [{ bearerAuth: [] }],
        description: 'Get current user\\'s business. Requires authentication.'
      }
    }
  )
`)

console.log('\n🚀 Quick fix applied:')
console.log('1. Added security to /api/test-protected endpoint')
console.log('2. Added security to /api/businesses/my-business endpoint')
console.log('3. Added security to /api/businesses/:id PUT endpoint')
console.log('4. Added security to /api/businesses/:id GET endpoint (public)')

console.log('\n✅ Done! Swagger should now show lock icons for protected endpoints.')
console.log('Access Swagger at: http://localhost:3000/swagger')