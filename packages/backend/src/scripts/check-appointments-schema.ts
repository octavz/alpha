import { db } from '../db/client'

async function checkAppointmentsSchema() {
  console.log('Checking appointments table schema...\n')
  
  try {
    // Query the information schema to see what columns exist
    const result = await db.execute(`
      SELECT column_name, data_type, is_nullable
      FROM information_schema.columns
      WHERE table_name = 'appointments'
      ORDER BY ordinal_position
    `)
    
    console.log('Current appointments table columns:')
    console.log('====================================')
    result.rows.forEach((row: any) => {
      console.log(`${row.column_name} (${row.data_type}) - ${row.is_nullable === 'YES' ? 'NULLABLE' : 'NOT NULL'}`)
    })
    
    console.log('\nExpected columns based on TypeScript schema:')
    console.log('============================================')
    console.log('id (uuid) - NOT NULL')
    console.log('business_id (uuid) - NOT NULL')
    console.log('customer_id (uuid) - NOT NULL')
    console.log('service_id (uuid) - NULLABLE')
    console.log('appointment_date (date) - NOT NULL')
    console.log('start_time (time) - NOT NULL')
    console.log('end_time (time) - NOT NULL')
    console.log('service_point_number (integer) - NULLABLE')
    console.log('customer_name (text) - NOT NULL')
    console.log('customer_email (text) - NOT NULL')
    console.log('customer_phone (text) - NULLABLE')
    console.log('customer_notes (text) - NULLABLE')
    console.log('status (text) - NOT NULL')
    console.log('created_at (timestamp) - NOT NULL')
    console.log('updated_at (timestamp) - NOT NULL')
    console.log('cancelled_at (timestamp) - NULLABLE')
    console.log('cancelled_reason (text) - NULLABLE')
    
    console.log('\nMigration 001 has customer_name, customer_email, customer_phone, customer_notes')
    console.log('Migration 002 does NOT have these fields')
    console.log('\nWe need to fix this schema mismatch!')
    
  } catch (error) {
    console.error('Error checking schema:', error)
  }
}

checkAppointmentsSchema().catch(console.error)