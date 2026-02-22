import { db } from '../src/db/client'
import { sql } from 'drizzle-orm'

async function testSchema() {
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
        const result = await db.execute(sql`SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ${table})`)
        const exists = result.rows[0]?.exists || false
        console.log(`   ${exists ? '✅' : '❌'} ${table}: ${exists ? 'Exists' : 'Missing'}`)
      } catch (error) {
        console.log(`   ❌ ${table}: Error checking`)
      }
    }
    
    // Test 2: Check if users table has new columns
    console.log('\n👤 Checking users table extensions...')
    const userColumns = await db.execute(sql`
      SELECT column_name, data_type 
      FROM information_schema.columns 
      WHERE table_name = 'users' 
      AND column_name IN ('role', 'phone', 'region_id', 'is_banned')
    `)
    
    const expectedColumns = ['role', 'phone', 'region_id', 'is_banned']
    for (const column of expectedColumns) {
      const found = userColumns.rows.some((row: any) => row.column_name === column)
      console.log(`   ${found ? '✅' : '❌'} ${column}: ${found ? 'Exists' : 'Missing'}`)
    }
    
    // Test 3: Check default data
    console.log('\n📊 Checking default data...')
    
    // Check regions
    const regionCount = await db.execute(sql`SELECT COUNT(*) as count FROM regions`)
    console.log(`   🌍 Regions: ${regionCount.rows[0]?.count || 0} records`)
    
    // Check categories
    const categoryCount = await db.execute(sql`SELECT COUNT(*) as count FROM categories`)
    console.log(`   📁 Categories: ${categoryCount.rows[0]?.count || 0} records`)
    
    // Check admin user
    const adminUser = await db.execute(sql`SELECT COUNT(*) as count FROM users WHERE role = 'admin'`)
    console.log(`   👑 Admin users: ${adminUser.rows[0]?.count || 0} records`)
    
    // Test 4: Check indexes
    console.log('\n🔍 Checking indexes...')
    const indexCount = await db.execute(sql`
      SELECT COUNT(*) as count 
      FROM pg_indexes 
      WHERE tablename IN ('regions', 'categories', 'businesses', 'services', 'appointments', 'reviews', 'business_hours')
    `)
    console.log(`   📈 Total indexes: ${indexCount.rows[0]?.count || 0}`)
    
    // Test 5: Check full-text search column
    console.log('\n🔎 Checking full-text search...')
    try {
      const hasFullText = await db.execute(sql`
        SELECT column_name 
        FROM information_schema.columns 
        WHERE table_name = 'businesses' 
        AND column_name = 'name_description_tsv'
      `)
      console.log(`   ${hasFullText.rows.length > 0 ? '✅' : '❌'} Full-text search column: ${hasFullText.rows.length > 0 ? 'Exists' : 'Missing'}`)
    } catch (error) {
      console.log('   ❌ Full-text search: Error checking')
    }
    
    console.log('\n🎉 Schema test completed!')
    
  } catch (error) {
    console.error('❌ Schema test failed:', error)
    process.exit(1)
  } finally {
    await sql.end()
    process.exit(0)
  }
}

// Run test if this script is executed directly
if (require.main === module) {
  testSchema()
}

export { testSchema }