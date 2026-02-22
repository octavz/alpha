import postgres from 'postgres'
import fs from 'fs'
import path from 'path'
import * as dotenv from 'dotenv'

dotenv.config()

async function runMigration() {
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
    console.log('🚀 Starting business directory schema migration...')
    
    // Read the migration SQL file
    const migrationPath = path.join(__dirname, '../migrations/001_business_directory_schema.sql')
    const migrationSQL = fs.readFileSync(migrationPath, 'utf8')
    
    console.log('📋 Executing migration SQL...')
    
    // Execute the entire migration SQL
    await sql.unsafe(migrationSQL)
    
    console.log('✅ Migration completed successfully!')
    console.log('📊 Schema created:')
    console.log('   - 8 new tables (regions, categories, businesses, services, appointments, reviews, business_hours)')
    console.log('   - Extended users table with role, phone, region_id, is_banned')
    console.log('   - 22 indexes for performance')
    console.log('   - 3 triggers for updated_at timestamps')
    console.log('   - Default data: 5 regions, 25 categories, 1 admin user')
    
  } catch (error) {
    console.error('❌ Migration failed:', error)
    process.exit(1)
  } finally {
    await sql.end()
    process.exit(0)
  }
}

// Run migration if this script is executed directly
if (require.main === module) {
  runMigration()
}

export { runMigration }