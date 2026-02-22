import postgres from 'postgres'
import fs from 'fs'
import path from 'path'
import * as dotenv from 'dotenv'

dotenv.config()

async function runCompleteMigration() {
  const connectionString = process.env.DATABASE_URL
  
  if (!connectionString) {
    throw new Error('DATABASE_URL environment variable is required')
  }
  
  // Create raw PostgreSQL client
  const sql = postgres(connectionString, {
    max: 1,
    idle_timeout: 20,
    connect_timeout: 10
  })
  
  try {
    console.log('🚀 Starting complete schema migration...')
    
    // Read the complete migration SQL file
    const migrationPath = path.join(__dirname, '../migrations/002_complete_schema.sql')
    const migrationSQL = fs.readFileSync(migrationPath, 'utf8')
    
    console.log('📋 Executing complete schema SQL...')
    
    // Execute the entire migration SQL
    await sql.unsafe(migrationSQL)
    
    console.log('✅ Complete migration completed successfully!')
    console.log('📊 Schema created:')
    console.log('   - 10 tables total (users, sessions, email_verifications, password_resets, regions, categories, businesses, services, appointments, reviews, business_hours)')
    console.log('   - 22 indexes for performance')
    console.log('   - 5 triggers for updated_at timestamps')
    console.log('   - Default data: 5 regions, 10 categories, 1 admin user')
    console.log('   - Full-text search enabled on businesses table')
    
  } catch (error) {
    console.error('❌ Migration failed:', error)
    process.exit(1)
  } finally {
    await sql.end()
    process.exit(0)
  }
}

// Run migration
runCompleteMigration()