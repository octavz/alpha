import { Elysia } from 'elysia'
import { cors } from '@elysiajs/cors'
import { swagger } from '@elysiajs/swagger'
import { logger } from './middleware/logger'
import { errorHandler } from './middleware/error-handler'
import { authRoutes } from './routes/auth.routes'
import { healthRoutes } from './routes/health.routes'

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
  .get('/', () => 'Alpha API Server')

export type App = typeof app