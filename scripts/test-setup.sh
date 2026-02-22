#!/bin/bash

# Alpha Setup Test Script
# This script tests if the Alpha project setup is working correctly

set -e

echo "🧪 Testing Alpha project setup..."

# Test 1: Check if required tools are installed
echo "1. Checking required tools..."
if ! command -v bun &> /dev/null; then
    echo "   ❌ Bun is not installed"
    exit 1
else
    echo "   ✅ Bun is installed"
fi

if ! command -v docker &> /dev/null; then
    echo "   ❌ Docker is not installed"
    exit 1
else
    echo "   ✅ Docker is installed"
fi

if ! docker info > /dev/null 2>&1; then
    echo "   ❌ Docker is not running"
    exit 1
else
    echo "   ✅ Docker is running"
fi

# Test 2: Check if project structure exists
echo "2. Checking project structure..."
required_dirs=(
    "packages/backend"
    "packages/web"
    "packages/mobile"
    "packages/shared"
    "docker"
    "scripts"
)

for dir in "${required_dirs[@]}"; do
    if [ -d "$dir" ]; then
        echo "   ✅ $dir exists"
    else
        echo "   ❌ $dir is missing"
        exit 1
    fi
done

# Test 3: Check if required files exist
echo "3. Checking required files..."
required_files=(
    "package.json"
    "docker-compose.yml"
    "packages/backend/package.json"
    "packages/web/package.json"
    "packages/mobile/package.json"
    "packages/shared/package.json"
)

for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✅ $file exists"
    else
        echo "   ❌ $file is missing"
        exit 1
    fi
done

# Test 4: Check if dependencies can be installed
echo "4. Testing dependency installation..."
cd packages/shared
if bun install --dry-run > /dev/null 2>&1; then
    echo "   ✅ Shared package dependencies can be installed"
else
    echo "   ❌ Shared package dependency check failed"
    exit 1
fi
cd ../..

# Test 5: Check if TypeScript compiles
echo "5. Testing TypeScript compilation..."
cd packages/shared
if bun run build --dry-run > /dev/null 2>&1; then
    echo "   ✅ Shared package TypeScript compiles"
else
    echo "   ❌ Shared package TypeScript compilation failed"
    exit 1
fi
cd ../..

# Test 6: Check Docker configuration
echo "6. Testing Docker configuration..."
if docker-compose config -q > /dev/null 2>&1; then
    echo "   ✅ Docker Compose configuration is valid"
else
    echo "   ❌ Docker Compose configuration is invalid"
    exit 1
fi

echo ""
echo "🎉 All tests passed! The Alpha project is set up correctly."
echo ""
echo "To start development:"
echo "1. Run: ./scripts/setup-dev.sh"
echo "2. Or manually:"
echo "   - bun install"
echo "   - docker-compose up -d"
echo "   - cd packages/backend && bun run db:migrate"
echo "   - bun run dev"