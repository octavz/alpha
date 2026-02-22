# Alpha Project

A client-server application with:
- Backend: Bun.js + Elysia + PostgreSQL + Drizzle ORM
- Web Frontend: React + TypeScript + Vite + Tailwind CSS + Zustand
- Mobile Frontend: React Native + TypeScript (Android) + React Native Styling
- Authentication: Email/password + Google OAuth
- Deployment: Docker + Kubernetes-ready

## Project Structure

```
alpha/
├── packages/
│   ├── backend/     # Bun.js + Elysia backend
│   ├── web/         # React web application
│   ├── mobile/      # React Native Android app
│   └── shared/      # Shared TypeScript code
├── docker/          # Docker configurations
├── kubernetes/      # Kubernetes manifests
├── scripts/         # Development scripts
└── docker-compose.yml
```

## Getting Started

1. Install dependencies: `bun install`
2. Start development: `bun run dev`
3. Run with Docker: `docker-compose up`

## Technology Stack

- **Runtime**: Bun.js 1.3.9
- **Backend Framework**: Elysia
- **Database**: PostgreSQL + Drizzle ORM
- **Web Frontend**: React 18 + TypeScript + Vite
- **Mobile Frontend**: React Native + TypeScript
- **State Management**: Zustand
- **Styling**: Tailwind CSS (web), React Native Styling (mobile)
- **Authentication**: JWT + Google OAuth
- **Containerization**: Docker + Docker Compose
- **Orchestration**: Kubernetes