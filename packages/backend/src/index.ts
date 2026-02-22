import { Elysia } from 'elysia'
import { cors } from '@elysiajs/cors'
import { swagger } from '@elysiajs/swagger'
import { logger } from './middleware/logger'
import { errorHandler } from './middleware/error-handler'
import { authRoutes } from './routes/auth.routes'
import { healthRoutes } from './routes/health.routes'
import { businessRoutes } from './routes/business.routes'
import { adminRoutes } from './routes/admin.routes'
import { appointmentRoutes } from './routes/appointment.routes'
import { db } from './db/client'

const app = new Elysia()
  .use(cors({
    origin: process.env.CORS_ORIGIN || 'http://localhost:5173',
    credentials: true,
    allowedHeaders: ['Content-Type', 'Authorization'],
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS']
  }))
  .use(swagger({
    documentation: {
      info: {
        title: 'Alpha API',
        version: '1.0.0',
        description: 'Alpha client-server application API'
      },
      tags: [
        { name: 'Auth', description: 'Authentication endpoints' },
        { name: 'Health', description: 'Health check endpoints' }
      ]
    }
  }))
  .use(logger)
  .use(errorHandler)
  .use(healthRoutes)
  .use(authRoutes)
  .use(businessRoutes)
  .use(adminRoutes)
  .use(appointmentRoutes)
  .get('/', () => 'Alpha API Server')
  .listen(process.env.PORT || 3000)

console.log(`🚀 Server is running at ${app.server?.hostname}:${app.server?.port}`)
console.log(`📚 Swagger documentation: ${app.server?.hostname}:${app.server?.port}/swagger`)

// Graceful shutdown
process.on('SIGINT', async () => {
  console.log('Shutting down server...')
  await db.end()
  process.exit(0)
})

process.on('SIGTERM', async () => {
  console.log('Shutting down server...')
  await db.end()
  process.exit(0)
})

export type App = typeof app