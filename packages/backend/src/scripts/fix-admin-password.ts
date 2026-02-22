import { db } from '../db/client'
import { users } from '../db/schema'
import { eq } from 'drizzle-orm'
import bcrypt from 'bcrypt'

async function fixAdminPassword() {
  console.log('🔧 Fixing admin password...')
  
  // Hash for 'admin123'
  const passwordHash = await bcrypt.hash('admin123', 10)
  
  // Update admin user
  const result = await db.update(users)
    .set({ passwordHash })
    .where(eq(users.email, 'admin@alpha.com'))
    .returning()
  
  if (result.length > 0) {
    console.log('✅ Admin password updated successfully!')
    console.log(`New hash: ${passwordHash}`)
    console.log('\n📋 Test credentials:')
    console.log('Email: admin@alpha.com')
    console.log('Password: admin123')
  } else {
    console.log('❌ Admin user not found')
    
    // Create admin user if doesn't exist
    const newAdmin = await db.insert(users).values({
      email: 'admin@alpha.com',
      passwordHash,
      name: 'Admin User',
      role: 'admin',
      emailVerified: true
    }).returning()
    
    console.log('✅ Admin user created successfully!')
  }
}

fixAdminPassword()
  .then(() => {
    console.log('\n🎉 Done! You can now login with:')
    console.log('Email: admin@alpha.com')
    console.log('Password: admin123')
    process.exit(0)
  })
  .catch((error) => {
    console.error('❌ Error:', error)
    process.exit(1)
  })