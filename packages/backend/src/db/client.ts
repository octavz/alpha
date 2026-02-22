import { drizzle } from 'drizzle-orm/postgres-js'
import postgres from 'postgres'
import * as schema from './schema'

const connectionString = process.env.DATABASE_URL

if (!connectionString) {
  throw new Error('DATABASE_URL environment variable is required')
}

// Create PostgreSQL client
const client = postgres(connectionString, {
  max: 10,
  idle_timeout: 20,
  connect_timeout: 10
})

// Create Drizzle ORM instance
export const db = drizzle(client, { schema })

export type Database = typeof db

// Test database connection
export async function testConnection(): Promise<boolean> {
  try {
    await client`SELECT 1`
    console.log('✅ Database connection established')
    return true
  } catch (error) {
    console.error('❌ Database connection failed:', error)
    return false
  }
}

// Close database connection
export async function closeConnection(): Promise<void> {
  await client.end()
  console.log('Database connection closed')
}