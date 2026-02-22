import { db } from '../src/db/client'
import { readFileSync } from 'fs'
import { join } from 'path'
import { sql as drizzleSql } from 'drizzle-orm'

async function runFixMigration() {
  console.log('🚀 Running appointments schema fix migration...')
  
  try {
    // Read the migration SQL file
    const migrationPath = join(__dirname, '../migrations/003_fix_appointments_schema.sql')
    const sqlContent = readFileSync(migrationPath, 'utf-8')
    
    console.log('📋 Executing migration SQL...')
    
    // Execute the SQL
    await db.execute(drizzleSql.raw(sqlContent))
    
    console.log('✅ Migration completed successfully!')
    console.log('📊 Appointments table now has all required customer information fields:')
    console.log('   - customer_name')
    console.log('   - customer_email') 
    console.log('   - customer_phone')
    console.log('   - customer_notes')
    console.log('   - cancelled_at')
    console.log('   - cancelled_reason')
    
  } catch (error) {
    console.error('❌ Migration failed:', error)
    process.exit(1)
  }
}

runFixMigration().catch(console.error)