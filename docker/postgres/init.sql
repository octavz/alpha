-- Alpha Database Initialization Script
-- This script runs when the PostgreSQL container is first created

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Set timezone
SET timezone = 'UTC';

-- Create additional roles if needed
-- CREATE ROLE alpha_readonly WITH LOGIN PASSWORD 'readonly_password' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
-- CREATE ROLE alpha_write WITH LOGIN PASSWORD 'write_password' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

-- Grant privileges
-- GRANT CONNECT ON DATABASE alpha TO alpha_readonly;
-- GRANT CONNECT ON DATABASE alpha TO alpha_write;

-- Create schema if using schema other than public
-- CREATE SCHEMA IF NOT EXISTS alpha_schema;
-- GRANT USAGE ON SCHEMA alpha_schema TO alpha_user;
-- GRANT USAGE ON SCHEMA alpha_schema TO alpha_readonly;
-- GRANT USAGE ON SCHEMA alpha_schema TO alpha_write;

-- Set search path
-- ALTER DATABASE alpha SET search_path TO alpha_schema, public;

-- Create tables will be created by Drizzle migrations
-- This file is for additional database setup

-- Create functions if needed
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Comment: Tables will be created by Drizzle ORM migrations
-- Run migrations with: bun run db:migrate