#!/bin/bash

# Alpha Development Setup Script
# This script sets up the development environment for the Alpha project

set -e

echo "🚀 Setting up Alpha development environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop and try again."
    exit 1
fi

# Check if Bun is installed
if ! command -v bun &> /dev/null; then
    echo "❌ Bun is not installed. Please install Bun.js first."
    echo "Visit: https://bun.sh/docs/installation"
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from .env.example..."
    cp .env.example .env
    echo "⚠️  Please update the .env file with your configuration"
fi

# Install root dependencies
echo "📦 Installing root dependencies..."
bun install

# Install workspace dependencies
echo "📦 Installing workspace dependencies..."
cd packages/backend && bun install
cd ../web && bun install
cd ../shared && bun install
cd ../mobile && bun install
cd ../..

# Build shared package
echo "🔨 Building shared package..."
cd packages/shared && bun run build
cd ../..

# Start Docker services
echo "🐳 Starting Docker services..."
docker-compose up -d

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
until docker exec alpha-postgres pg_isready -U alpha_user -d alpha > /dev/null 2>&1; do
    sleep 2
done

# Run database migrations
echo "🗄️  Running database migrations..."
cd packages/backend && bun run db:migrate
cd ../..

echo "✅ Development environment setup complete!"
echo ""
echo "📋 Next steps:"
echo "1. Update .env file with your configuration"
echo "2. Start development servers:"
echo "   - Backend: bun run dev:backend"
echo "   - Web: bun run dev:web"
echo "3. Or use Docker: docker-compose up"
echo ""
echo "🌐 Access points:"
echo "   - Backend API: http://localhost:3000"
echo "   - Backend Swagger: http://localhost:3000/swagger"
echo "   - Web frontend: http://localhost:5173"
echo ""
echo "🛠️  Useful commands:"
echo "   - bun run dev          # Start all services"
echo "   - bun run docker:dev   # Start Docker services"
echo "   - bun run db:migrate   # Run database migrations"
echo "   - bun run test         # Run tests"