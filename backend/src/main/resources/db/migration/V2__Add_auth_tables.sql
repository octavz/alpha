-- Migration: Add authentication tables
-- Description: Adds users, sessions, email_verifications, and password_resets tables
-- Created: 2026-03-01

-- ========== CREATE AUTHENTICATION TABLES ==========

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT,
  google_id TEXT UNIQUE,
  name TEXT,
  phone TEXT,
  role TEXT NOT NULL CHECK (role IN ('customer', 'business_admin', 'business_staff', 'admin')),
  email_verified BOOLEAN DEFAULT false NOT NULL,
  avatar_url TEXT,
  region_id UUID REFERENCES regions(id),
  is_banned BOOLEAN DEFAULT false NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS users_email_idx ON users(email);
CREATE INDEX IF NOT EXISTS users_google_id_idx ON users(google_id);
CREATE INDEX IF NOT EXISTS users_role_idx ON users(role);
CREATE INDEX IF NOT EXISTS users_region_id_idx ON users(region_id);

-- Sessions table
CREATE TABLE IF NOT EXISTS sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token TEXT, -- JWT access token (nullable, not storing full token anymore)
  refresh_token TEXT NOT NULL,
  user_agent TEXT,
  ip_address TEXT,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS sessions_user_id_idx ON sessions(user_id);
CREATE INDEX IF NOT EXISTS sessions_token_idx ON sessions(token);
CREATE INDEX IF NOT EXISTS sessions_refresh_token_idx ON sessions(refresh_token);

-- Email verifications table
CREATE TABLE IF NOT EXISTS email_verifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token TEXT NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS email_verifications_token_idx ON email_verifications(token);
CREATE INDEX IF NOT EXISTS email_verifications_user_id_idx ON email_verifications(user_id);

-- Password resets table
CREATE TABLE IF NOT EXISTS password_resets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token TEXT NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS password_resets_token_idx ON password_resets(token);
CREATE INDEX IF NOT EXISTS password_resets_user_id_idx ON password_resets(user_id);

-- ========== ADD MISSING FOREIGN KEY CONSTRAINTS ==========

-- Add foreign key for users.region_id (if not already exists)
DO $$ 
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE constraint_name = 'fk_users_region' AND table_name = 'users'
  ) THEN
    ALTER TABLE users 
    ADD CONSTRAINT fk_users_region 
    FOREIGN KEY (region_id) REFERENCES regions(id);
  END IF;
END $$;

-- Add foreign key for businesses.user_id (if not already exists)
DO $$ 
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE constraint_name = 'fk_businesses_user' AND table_name = 'businesses'
  ) THEN
    ALTER TABLE businesses 
    ADD CONSTRAINT fk_businesses_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
  END IF;
END $$;

-- Add foreign key for appointments.customer_id (if not already exists)
DO $$ 
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE constraint_name = 'fk_appointments_customer' AND table_name = 'appointments'
  ) THEN
    ALTER TABLE appointments 
    ADD CONSTRAINT fk_appointments_customer 
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE;
  END IF;
END $$;

-- Add foreign key for reviews.customer_id (if not already exists)
DO $$ 
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints 
    WHERE constraint_name = 'fk_reviews_customer' AND table_name = 'reviews'
  ) THEN
    ALTER TABLE reviews 
    ADD CONSTRAINT fk_reviews_customer 
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE;
  END IF;
END $$;