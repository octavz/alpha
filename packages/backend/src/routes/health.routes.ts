import { Elysia, t } from 'elysia'
import { db, testConnection } from '../db/client'

export const healthRoutes = new Elysia({ prefix: '/api/health' })
  .get('/', () => ({
    success: true,
    data: {
      status: 'healthy',
      timestamp: new Date().toISOString(),
      service: 'alpha-backend',
      version: '1.0.0',
      message: 'API is running with Swagger!'
    }
  }))
  .get('/db', async () => {
    const dbHealthy = await testConnection()
    return {
      success: true,
      data: {
        status: dbHealthy ? 'healthy' : 'unhealthy',
        timestamp: new Date().toISOString(),
        service: 'database',
        message: dbHealthy ? 'Database connection successful' : 'Database connection failed'
      }
    }
  })
  .get('/detailed', async () => {
    const dbHealthy = await testConnection()
    
    return {
      success: true,
      data: {
        status: dbHealthy ? 'healthy' : 'degraded',
        timestamp: new Date().toISOString(),
        services: {
          api: {
            status: 'healthy',
            uptime: process.uptime()
          },
          database: {
            status: dbHealthy ? 'healthy' : 'unhealthy',
            connected: dbHealthy
          }
        },
        system: {
          nodeVersion: process.version,
          platform: process.platform,
          memory: process.memoryUsage(),
          uptime: process.uptime()
        }
      }
    }
  })