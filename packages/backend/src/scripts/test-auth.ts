import bcrypt from 'bcryptjs'

// Test credentials
const testCredentials = [
  {
    email: 'admin@alpha.com',
    password: 'admin123',
    role: 'admin'
  },
  {
    email: 'business1@test.com',
    password: 'password123',
    role: 'business_admin'
  }
]

async function testAuth() {
  console.log('🔐 Testing Authentication')
  console.log('=======================')
  
  // Test the admin password hash
  const adminHash = '$2b$10$qjpBH3VUDogv5T0F0KPPyOiF.Ezx0LDcVuMo3yK1f/M0faZKGTeLC'
  const adminPassword = 'admin123'
  
  const adminMatch = await bcrypt.compare(adminPassword, adminHash)
  console.log(`Admin password match: ${adminMatch ? '✅' : '❌'}`)
  
  // Generate hash for business user password
  const businessHash = await bcrypt.hash('password123', 10)
  console.log(`Business user password hash: ${businessHash}`)
  
  console.log('\n📋 Test Credentials for Swagger:')
  console.log('==============================')
  testCredentials.forEach((cred, index) => {
    console.log(`\n${index + 1}. ${cred.role.toUpperCase()}`)
    console.log(`   Email: ${cred.email}`)
    console.log(`   Password: ${cred.password}`)
  })
  
  console.log('\n🔗 Swagger Authentication Steps:')
  console.log('===============================')
  console.log('1. Go to: http://localhost:3000/swagger')
  console.log('2. Click "Authorize" button (top right)')
  console.log('3. Enter: Bearer YOUR_JWT_TOKEN')
  console.log('4. Get token from: POST /api/auth/login')
  console.log('5. Test protected endpoint: GET /api/test-protected')
  
  console.log('\n📝 Example curl commands:')
  console.log('========================')
  console.log('# Get admin token:')
  console.log(`curl -X POST http://localhost:3000/api/auth/login \\`)
  console.log(`  -H "Content-Type: application/json" \\`)
  console.log(`  -d '{"email":"admin@alpha.com","password":"admin123"}'`)
  
  console.log('\n# Test protected endpoint:')
  console.log(`curl -X GET http://localhost:3000/api/test-protected \\`)
  console.log(`  -H "Authorization: Bearer YOUR_TOKEN_HERE"`)
}

testAuth().catch(console.error)