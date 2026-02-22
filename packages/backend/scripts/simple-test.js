// Simple test script to verify database schema
const postgres = require('postgres')
require('dotenv').config()

async function testSchema() {
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
    console.log('🔍 Testing database schema...')
    
    // Test 1: Check if new tables exist
    console.log('📋 Checking for new tables...')
    
    const tables = [
      'regions',
      'categories', 
      'businesses',
      'services',
      'appointments',
      'reviews',
      'business_hours'
    ]
    
    for (const table of tables) {
      try {
        const result = await sql`SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ${table})`
        const exists = result[0]?.exists || false
        console.log(`   ${exists ? '✅' : '❌'} ${table}: ${exists ? 'Exists' : 'Missing'}`)
      } catch (error) {
        console.log(`   ❌ ${table}: Error checking`)
      }
    }
    
    // Test 2: Check if users table has new columns
    console.log('\n👤 Checking users table extensions...')
    const userColumns = await sql`
      SELECT column_name, data_type 
      FROM information_schema.columns 
      WHERE table_name = 'users' 
      AND column_name IN ('role', 'phone', 'region_id', 'is_banned')
    `
    
    const expectedColumns = ['role', 'phone', 'region_id', 'is_banned']
    for (const column of expectedColumns) {
      const found = userColumns.some(row => row.column_name === column)
      console.log(`   ${found ? '✅' : '❌'} ${column}: ${found ? 'Exists' : 'Missing'}`)
    }
    
    // Test 3: Check default data
    console.log('\n📊 Checking default data...')
    
    // Check regions
    const regionCount = await sql`SELECT COUNT(*) as count FROM regions`
    console.log(`   🌍 Regions: ${regionCount[0]?.count || 0} records`)
    
    // Check categories
    const categoryCount = await sql`SELECT COUNT(*) as count FROM categories`
    console.log(`   📁 Categories: ${categoryCount[0]?.count || 0} records`)
    
    // Check admin user
    const adminUser = await sql`SELECT COUNT(*) as count FROM users WHERE role = 'admin'`
    console.log(`   👑 Admin users: ${adminUser[0]?.count || 0} records`)
    
    console.log('\n🎉 Schema test completed!')
    
  } catch (error) {
    console.error('❌ Schema test failed:', error)
    process.exit(1)
  } finally {
    await sql.end()
    process.exit(0)
  }
}

// Run test
testSchema()