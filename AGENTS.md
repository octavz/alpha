# OpenCode Configuration for Alpha Project

## Project Overview
Alpha is a business directory platform with:
- **Backend**: Spring Boot 3.2 + Kotlin + PostgreSQL (port 3000)
- **Frontend**: React + Vite (port 5173)
- **Database**: PostgreSQL running on port 5433

## Development Commands

### Backend (packages/backend-java)
```bash
# Start backend server
./gradlew bootRun

# Run database migrations
./gradlew flywayMigrate

# Build application
./gradlew build

# Run tests
./gradlew test

# Clean build
./gradlew clean
```

### Frontend (packages/web)
```bash
# Start frontend dev server
npm run dev

# Build for production
npm run build

# Run tests
npm run test
```

### Database
```bash
# PostgreSQL is running on port 5433
# Database name: alpha
# Username: alpha_user
# Password: alpha_password
```

## Environment Variables
Create `.env` file in root directory with:
```
NODE_ENV=development
PORT=3000
DATABASE_URL=postgresql://alpha_user:alpha_password@localhost:5433/alpha
JWT_SECRET=your_jwt_secret_here
JWT_ACCESS_SECRET=your_jwt_access_secret_here
JWT_REFRESH_SECRET=your_jwt_refresh_secret_here
JWT_ACCESS_EXPIRY=15m
JWT_REFRESH_EXPIRY=7d
CORS_ORIGIN=http://localhost:5173,http://localhost:5179
```

## Port Configuration
- **Backend**: Always start on port 3000
- **Frontend**: Always start on port 5173
- **Database**: PostgreSQL on port 5433

## Important Rules
1. **Kill all processes before starting**: Always kill existing bun and node processes to avoid port conflicts
2. **Frontend port 5173**: Always start frontend on port 5173 and kill any processes using port 5173 first
3. **Backend port 3000**: Always start backend on port 3000 and kill any processes using port 3000 first
4. **Single .env file**: Use only the root `.env` file, not multiple .env files
5. **No Vite proxy**: Handle CORS properly instead of using Vite proxy
6. **Real data only**: Never use mock/static data when real API/database is available
7. **Fix problems at source**: When finding issues, fix the root cause, not patch symptoms

## Project Structure
```
alpha/
├── packages/
│   ├── backend-java/    # Spring Boot + Kotlin backend
│   ├── web/             # React + Vite frontend
│   └── shared/          # Shared types and utilities
├── .env                 # Environment variables
└── AGENTS.md           # This file
```

## Common Issues & Solutions

### Port Conflicts
```bash
# Kill all bun processes
taskkill /F /IM bun.exe /T 2>nul || powershell -Command "Get-Process bun* -ErrorAction SilentlyContinue | Stop-Process -Force"

# Kill processes using port 5173 (frontend)
powershell -Command "Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"

# Kill processes using port 3000 (backend)
powershell -Command "Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"

# Verify processes are killed (check after 2 seconds)
timeout /t 2 /nobreak >nul
powershell -Command "if (Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue) { Write-Host 'WARNING: Port 5173 still in use' -ForegroundColor Yellow }"
powershell -Command "if (Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue) { Write-Host 'WARNING: Port 3000 still in use' -ForegroundColor Yellow }"
powershell -Command "if (Get-Process bun* -ErrorAction SilentlyContinue) { Write-Host 'WARNING: Bun processes still running' -ForegroundColor Yellow }"
```

### Database Connection Issues
1. Ensure PostgreSQL is running on port 5433
2. Check `.env` file has correct DATABASE_URL
3. Run `./gradlew flywayMigrate` to apply migrations

### CORS Issues
- Backend CORS is configured to allow `http://localhost:5173` and `http://localhost:5179`
- No Vite proxy should be used
- Frontend should make direct API calls to `http://localhost:3000`

## Testing
- Always run tests after making changes
- Backend: `cd packages/backend-java && ./gradlew test`
- Frontend: `cd packages/web && npm run test`

## Code Quality
- Follow existing code conventions
- Use TypeScript strict mode
- Add appropriate error handling
- Include tests for new features