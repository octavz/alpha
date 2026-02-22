# Alpha Project - Setup Guide

## Overview

Alpha is a client-server application with:
- **Backend**: Bun.js + Elysia + PostgreSQL + Drizzle ORM
- **Web Frontend**: React + TypeScript + Vite + Tailwind CSS + Zustand
- **Mobile Frontend**: React Native + TypeScript (Android) + React Native Styling
- **Authentication**: Email/password + Google OAuth
- **Deployment**: Docker + Kubernetes-ready

## Prerequisites

### Required Tools
1. **Bun.js** (v1.0.0+) - [Installation Guide](https://bun.sh/docs/installation)
2. **Docker** (v20.10+) - [Installation Guide](https://docs.docker.com/get-docker/)
3. **Docker Compose** (v2.0+) - Included with Docker Desktop
4. **Node.js** (v18.0.0+) - Only needed for React Native
5. **Java JDK** (v17+) - For Android development
6. **Android Studio** - For React Native Android development

### Optional Tools
- **kubectl** - For Kubernetes deployment
- **minikube** or **k3d** - For local Kubernetes testing
- **PostgreSQL client** (pgAdmin, DBeaver, etc.)

## Quick Start

### Option 1: Automated Setup (Recommended)
```bash
# Make the setup script executable
chmod +x scripts/setup-dev.sh

# Run the setup script
./scripts/setup-dev.sh
```

### Option 2: Manual Setup

#### 1. Clone and Navigate
```bash
cd /c/work/alpha
```

#### 2. Install Dependencies
```bash
# Install root dependencies
bun install

# Install workspace dependencies
cd packages/backend && bun install
cd ../web && bun install
cd ../shared && bun install
cd ../mobile && bun install
cd ../..
```

#### 3. Configure Environment
```bash
# Copy environment template
cp .env.example .env

# Edit .env file with your configuration
# Important: Update JWT_SECRET, database credentials, etc.
```

#### 4. Start Docker Services
```bash
# Start PostgreSQL and services
docker-compose up -d

# Wait for PostgreSQL to be ready
sleep 10
```

#### 5. Run Database Migrations
```bash
cd packages/backend
bun run db:migrate
cd ../..
```

#### 6. Start Development Servers
```bash
# Start backend and web frontend
bun run dev

# Or start separately
bun run dev:backend  # Backend on http://localhost:3000
bun run dev:web      # Web frontend on http://localhost:5173
```

## Project Structure

```
alpha/
├── packages/
│   ├── backend/     # Bun.js + Elysia backend
│   │   ├── src/
│   │   │   ├── db/          # Database schema and client
│   │   │   ├── routes/      # API endpoints
│   │   │   ├── services/    # Business logic
│   │   │   ├── middleware/  # Custom middleware
│   │   │   └── index.ts     # Main entry point
│   │   ├── drizzle.config.ts
│   │   └── Dockerfile
│   │
│   ├── web/         # React web application
│   │   ├── src/
│   │   │   ├── components/  # Shared components
│   │   │   ├── pages/       # Page components
│   │   │   ├── hooks/       # Custom React hooks
│   │   │   ├── stores/      # Zustand stores
│   │   │   └── App.tsx      # Main app component
│   │   ├── vite.config.ts
│   │   └── Dockerfile
│   │
│   ├── mobile/      # React Native Android app
│   │   ├── android/ # Android native code
│   │   ├── src/
│   │   │   ├── components/  # Mobile components
│   │   │   ├── screens/     # Screen components
│   │   │   ├── navigation/  # Navigation setup
│   │   │   ├── contexts/    # React contexts
│   │   │   └── App.tsx      # Main app component
│   │   └── metro.config.js
│   │
│   └── shared/      # Shared TypeScript code
│       ├── src/
│       │   ├── types/     # Shared TypeScript interfaces
│       │   ├── api/       # API client definitions
│       │   ├── utils/     # Shared utilities
│       │   └── constants/ # Shared constants
│       └── tsconfig.json
│
├── docker/
│   ├── postgres/    # PostgreSQL configuration
│   └── nginx/       # Nginx configuration (production)
│
├── kubernetes/      # Kubernetes manifests
├── scripts/         # Development scripts
├── docker-compose.yml
└── README.md
```

## Development

### Backend Development
```bash
cd packages/backend

# Start development server
bun run dev

# Run tests
bun run test

# Database operations
bun run db:generate   # Generate migrations
bun run db:migrate    # Run migrations
bun run db:studio     # Open Drizzle Studio

# Build for production
bun run build
```

### Web Frontend Development
```bash
cd packages/web

# Start development server
bun run dev

# Build for production
bun run build

# Preview production build
bun run preview

# Run tests
bun run test

# Lint code
bun run lint
```

### Mobile Development
```bash
cd packages/mobile

# Install dependencies (if using Bun)
bun install

# Start Metro bundler
bun run start

# Run on Android (requires Android Studio/emulator)
bun run android

# Run tests
bun run test

# Build Android APK
bun run build:android
```

### Shared Package Development
```bash
cd packages/shared

# Build TypeScript
bun run build

# Watch mode for development
bun run watch
```

## Docker Development

### Using Docker Compose
```bash
# Start all services (development)
docker-compose up

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild images
docker-compose build

# Check service status
docker-compose ps
```

### Production Deployment
```bash
# Build and run production containers
docker-compose -f docker-compose.prod.yml up -d

# View production logs
docker-compose -f docker-compose.prod.yml logs -f
```

## Database Management

### PostgreSQL Operations
```bash
# Access PostgreSQL shell
docker exec -it alpha-postgres psql -U alpha_user -d alpha

# Backup database
docker exec alpha-postgres pg_dump -U alpha_user alpha > backup.sql

# Restore database
docker exec -i alpha-postgres psql -U alpha_user -d alpha < backup.sql

# View database logs
docker logs alpha-postgres
```

### Drizzle ORM Commands
```bash
cd packages/backend

# Generate migration from schema changes
bun run db:generate

# Apply migrations
bun run db:migrate

# Push schema changes (development only)
bun run db:push

# Open Drizzle Studio (web UI)
bun run db:studio
```

## Testing

### Run All Tests
```bash
bun run test
```

### Backend Tests
```bash
cd packages/backend
bun test
```

### Web Frontend Tests
```bash
cd packages/web
bun run test
```

### Mobile Tests
```bash
cd packages/mobile
bun run test
```

## Environment Variables

### Required Variables (.env)
```bash
# Database
DATABASE_URL=postgresql://alpha_user:alpha_password@localhost:5433/alpha
POSTGRES_DB=alpha
POSTGRES_USER=alpha_user
POSTGRES_PASSWORD=alpha_password

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_ACCESS_EXPIRY=15m
JWT_REFRESH_EXPIRY=7d

# Google OAuth (optional)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:3000/api/auth/google/callback

# Application
NODE_ENV=development
PORT=3000
API_URL=http://localhost:3000
WEB_URL=http://localhost:5173
CORS_ORIGIN=http://localhost:5173
```

## Troubleshooting

### Common Issues

#### 1. Docker Permission Errors
```bash
# On Linux/Mac, add your user to docker group
sudo usermod -aG docker $USER
# Log out and log back in
```

#### 2. Port Conflicts
If ports 3000, 5173, or 5433 are already in use:
- Update ports in `docker-compose.yml`
- Update environment variables accordingly

#### 3. Database Connection Issues
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check PostgreSQL logs
docker logs alpha-postgres

# Test database connection
docker exec alpha-postgres pg_isready -U alpha_user -d alpha
```

#### 4. Bun Installation Issues
```bash
# Uninstall and reinstall Bun
curl -fsSL https://bun.sh/install | bash

# Add to PATH (if not already)
export BUN_INSTALL="$HOME/.bun"
export PATH="$BUN_INSTALL/bin:$PATH"
```

#### 5. React Native Android Issues
```bash
# Clean build
cd packages/mobile/android
./gradlew clean

# Reinstall dependencies
cd ..
rm -rf node_modules
bun install

# Check Android SDK
echo $ANDROID_HOME
ls $ANDROID_HOME
```

### Debugging

#### Backend Debugging
```bash
# Run with debug logging
cd packages/backend
DEBUG=* bun run dev

# Check API endpoints
curl http://localhost:3000/health
curl http://localhost:3000/swagger
```

#### Web Frontend Debugging
```bash
# Check browser console for errors
# Open DevTools (F12)

# Check network requests
# Look for 404 or 500 errors
```

#### Docker Debugging
```bash
# Check container status
docker-compose ps

# View logs for specific service
docker-compose logs backend
docker-compose logs web
docker-compose logs postgres

# Enter container shell
docker exec -it alpha-backend sh
docker exec -it alpha-postgres sh
```

## Deployment

### Production Checklist
1. [ ] Update all environment variables in production `.env`
2. [ ] Set strong JWT secrets
3. [ ] Configure proper CORS origins
4. [ ] Set up SSL certificates
5. [ ] Configure database backups
6. [ ] Set up monitoring and logging
7. [ ] Configure CI/CD pipeline
8. [ ] Set up error tracking (Sentry, etc.)

### Kubernetes Deployment
```bash
# Apply Kubernetes manifests
kubectl apply -f kubernetes/

# Check deployment status
kubectl get pods
kubectl get services
kubectl get deployments

# View logs
kubectl logs -f deployment/alpha-backend
```

### Cloud Deployment Options
- **AWS**: ECS, EKS, RDS
- **Google Cloud**: GKE, Cloud SQL
- **Azure**: AKS, Azure Database for PostgreSQL
- **DigitalOcean**: Kubernetes, Managed Databases
- **Render**: Full-stack deployment

## Contributing

### Development Workflow
1. Create a feature branch
2. Make changes
3. Run tests
4. Submit pull request
5. Code review
6. Merge to main

### Code Style
- **TypeScript**: Strict mode enabled
- **React**: Functional components with hooks
- **Backend**: Elysia framework patterns
- **Database**: Drizzle ORM best practices
- **Formatting**: Prettier configuration provided

### Commit Guidelines
- Use conventional commits
- Reference issue numbers
- Write descriptive commit messages
- Keep commits focused and atomic

## Support

### Documentation
- [Bun.js Documentation](https://bun.sh/docs)
- [Elysia Documentation](https://elysiajs.com/)
- [React Documentation](https://react.dev/)
- [React Native Documentation](https://reactnative.dev/)
- [Drizzle ORM Documentation](https://orm.drizzle.team/)
- [Docker Documentation](https://docs.docker.com/)

### Community
- Join the Alpha project Discord/chat
- Check GitHub Issues for known problems
- Review the FAQ section

### Reporting Issues
1. Check existing issues
2. Create a new issue with:
   - Description of the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details
   - Screenshots/logs if applicable

## License

MIT License - See LICENSE file for details.

---

**Happy Coding!** 🚀

For additional help, contact the Alpha development team or check the project documentation.