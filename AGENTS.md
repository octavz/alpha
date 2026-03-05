# OpenCode Configuration for Alpha Project

## Project Overview
Alpha is a business directory platform with:
- **Backend**: Spring Boot 3.2 + Kotlin + PostgreSQL (port 3000)
- **Frontend**: React + Vite (port 5173)
- **Database**: PostgreSQL running on port 5433

## Development Commands

### Backend (backend/)
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

### Frontend (frontend/web/)
```bash
# Install dependencies (first time)
npm install --legacy-peer-deps

# Start frontend dev server
npm run dev

# Build for production
npm run build

# Run tests
npm run test

# Lint code
npm run lint

# Check TypeScript
./node_modules/.bin/tsc --noEmit
```

### Database
```bash
# PostgreSQL is running on port 5433
# Database name: alpha
# Username: alpha_user
# Password: alpha_password
```

## Environment Variables
Create `.env` file in `frontend/web/.env` with:
```
NODE_ENV=development
VITE_API_URL=http://localhost:3000
```

For backend, create `.env` in `backend/` with:
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

## IMPORTANT: PowerShell Only
**ALWAYS use PowerShell commands on Windows** - do not use bash/shell syntax.
- Use `powershell -Command "..."` for all commands
- Do NOT use bash-specific syntax like `&&`, `||`, or `;` for chaining
- Use PowerShell equivalents: `-ErrorAction SilentlyContinue`, `-Force`, etc.
- All process kill commands use PowerShell, not taskkill directly
- All port checks use PowerShell `Get-NetTCPConnection`

## Shell Command Conversion
Instead of bash: `cd folder && npm install`
Use PowerShell: `cd folder; npm install` or separate commands

For sequential execution in PowerShell:
```powershell
# Use semicolons or separate calls
cd backend; ./gradlew bootRun

# Or as separate commands
cd backend
./gradlew bootRun
```

## Project Structure
```
alpha/
├── backend/              # Spring Boot + Kotlin backend
├── frontend/
│   ├── web/             # React + Vite frontend
│   └── shared/          # Shared types and utilities
├── .env.example
├── AGENTS.md           # This file
└── README.md
```

## Common Issues & Solutions

### Port Conflicts
```bash
# Kill all node processes
taskkill /F /IM node.exe /T 2>nul || powershell -Command "Get-Process node* -ErrorAction SilentlyContinue | Stop-Process -Force"

# Kill processes using port 5173 (frontend)
powershell -Command "Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"

# Kill processes using port 3000 (backend)
powershell -Command "Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"

# Verify processes are killed (check after 2 seconds)
timeout /t 2 /nobreak >nul
powershell -Command "if (Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue) { Write-Host 'WARNING: Port 5173 still in use' -ForegroundColor Yellow }"
powershell -Command "if (Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue) { Write-Host 'WARNING: Port 3000 still in use' -ForegroundColor Yellow }"
powershell -Command "if (Get-Process node* -ErrorAction SilentlyContinue) { Write-Host 'WARNING: Node processes still running' -ForegroundColor Yellow }"
```

### Dependency Issues (Frontend)
If npm install fails with peer dependency conflicts, use:
```bash
npm install --legacy-peer-deps
```

### Database Connection Issues
1. Ensure PostgreSQL is running on port 5433
2. Check `.env` file in backend/ has correct DATABASE_URL
3. Run `./gradlew flywayMigrate` to apply migrations

### CORS Issues
- Backend CORS is configured to allow `http://localhost:5173` and `http://localhost:5179`
- No Vite proxy should be used
- Frontend should make direct API calls to `http://localhost:3000`

## Testing
- Always run tests after making changes
- Backend: `cd backend && ./gradlew test`
- Frontend: `cd frontend/web && npm run test`

## Code Quality
- Follow existing code conventions
- Use TypeScript strict mode
- Add appropriate error handling
- Include tests for new features