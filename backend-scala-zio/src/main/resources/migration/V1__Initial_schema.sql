-- Migration: Business Directory Schema (Baseline)
-- Description: Creates all tables for business directory application
-- Created: 2026-02-28
-- Baseline: This is a baseline migration for existing database

-- ========== CREATE TABLES ==========

-- Regions table
CREATE TABLE IF NOT EXISTS regions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  code TEXT UNIQUE NOT NULL,
  country TEXT NOT NULL,
  timezone TEXT NOT NULL,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS regions_code_idx ON regions(code);
CREATE INDEX IF NOT EXISTS regions_country_idx ON regions(country);

-- Categories table (fixed hierarchy)
CREATE TABLE IF NOT EXISTS categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  slug TEXT UNIQUE NOT NULL,
  description TEXT,
  icon TEXT,
  parent_id UUID REFERENCES categories(id) ON DELETE SET NULL,
  sort_order INTEGER DEFAULT 0,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS categories_slug_idx ON categories(slug);
CREATE INDEX IF NOT EXISTS categories_parent_id_idx ON categories(parent_id);

-- Businesses table
CREATE TABLE IF NOT EXISTS businesses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL,
  name TEXT NOT NULL,
  slug TEXT UNIQUE NOT NULL,
  description TEXT,
  category_id UUID REFERENCES categories(id),
  region_id UUID NOT NULL,
  
  -- Contact Information
  email TEXT,
  phone TEXT,
  website TEXT,
  
  -- Address (for geolocation)
  address_line1 TEXT,
  address_line2 TEXT,
  city TEXT,
  state TEXT,
  zip_code TEXT,
  country TEXT,
  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),
  
  -- Business Details
  logo_url TEXT,
  cover_image_url TEXT,
  is_verified BOOLEAN DEFAULT false,
  is_active BOOLEAN DEFAULT true,
  is_featured BOOLEAN DEFAULT false,
  verification_status TEXT DEFAULT 'pending' CHECK (verification_status IN ('pending', 'approved', 'rejected')),
  
  -- Service Points (multiple chairs/employees)
  service_points_count INTEGER DEFAULT 1,
  
  -- Metadata
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

-- Indexes for businesses table
CREATE INDEX IF NOT EXISTS businesses_region_id_idx ON businesses(region_id);
CREATE INDEX IF NOT EXISTS businesses_category_id_idx ON businesses(category_id);
CREATE INDEX IF NOT EXISTS businesses_verification_status_idx ON businesses(verification_status);
CREATE INDEX IF NOT EXISTS businesses_location_idx ON businesses(latitude, longitude);
CREATE INDEX IF NOT EXISTS businesses_slug_idx ON businesses(slug);

-- Services table
CREATE TABLE IF NOT EXISTS services (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID NOT NULL,
  name TEXT NOT NULL,
  description TEXT,
  duration_minutes INTEGER NOT NULL,
  price DECIMAL(10, 2),
  is_active BOOLEAN DEFAULT true,
  sort_order INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS services_business_id_idx ON services(business_id);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID NOT NULL,
  customer_id UUID NOT NULL,
  service_id UUID,
  
  -- Appointment Details
  appointment_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  service_point_number INTEGER,
  
  -- Customer Information (cached for historical reference)
  customer_name TEXT NOT NULL,
  customer_email TEXT NOT NULL,
  customer_phone TEXT,
  customer_notes TEXT,
  
  -- Status
  status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'completed', 'cancelled', 'no_show')),
  
  -- Metadata
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL,
  cancelled_at TIMESTAMP,
  cancelled_reason TEXT
);

-- Indexes for appointments table
CREATE INDEX IF NOT EXISTS appointments_business_id_idx ON appointments(business_id);
CREATE INDEX IF NOT EXISTS appointments_customer_id_idx ON appointments(customer_id);
CREATE INDEX IF NOT EXISTS appointments_date_status_idx ON appointments(appointment_date, status);
CREATE INDEX IF NOT EXISTS appointments_business_date_idx ON appointments(business_id, appointment_date);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID NOT NULL,
  customer_id UUID NOT NULL,
  appointment_id UUID,
  
  -- Review Details
  rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
  title TEXT,
  comment TEXT,
  is_approved BOOLEAN DEFAULT false,
  is_featured BOOLEAN DEFAULT false,
  
  -- Metadata
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS reviews_business_id_idx ON reviews(business_id);
CREATE INDEX IF NOT EXISTS reviews_customer_id_idx ON reviews(customer_id);
CREATE INDEX IF NOT EXISTS reviews_rating_idx ON reviews(rating);
CREATE INDEX IF NOT EXISTS reviews_is_approved_idx ON reviews(is_approved);

-- Business hours table
CREATE TABLE IF NOT EXISTS business_hours (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID NOT NULL,
  day_of_week INTEGER NOT NULL CHECK (day_of_week >= 0 AND day_of_week <= 6),
  open_time TIME,
  close_time TIME,
  is_closed BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS business_hours_business_id_idx ON business_hours(business_id);

-- ========== ADD FOREIGN KEY CONSTRAINTS ==========

-- Add foreign key for businesses.category_id
ALTER TABLE businesses 
ADD CONSTRAINT fk_businesses_category 
FOREIGN KEY (category_id) REFERENCES categories(id);

-- Add foreign key for businesses.region_id
ALTER TABLE businesses 
ADD CONSTRAINT fk_businesses_region 
FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE RESTRICT;

-- Add foreign key for services.business_id
ALTER TABLE services 
ADD CONSTRAINT fk_services_business 
FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

-- Add foreign key for appointments.business_id
ALTER TABLE appointments 
ADD CONSTRAINT fk_appointments_business 
FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

-- Add foreign key for appointments.service_id
ALTER TABLE appointments 
ADD CONSTRAINT fk_appointments_service 
FOREIGN KEY (service_id) REFERENCES services(id);

-- Add foreign key for reviews.business_id
ALTER TABLE reviews 
ADD CONSTRAINT fk_reviews_business 
FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

-- Add foreign key for reviews.appointment_id
ALTER TABLE reviews 
ADD CONSTRAINT fk_reviews_appointment 
FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL;

-- Add foreign key for business_hours.business_id
ALTER TABLE business_hours 
ADD CONSTRAINT fk_business_hours_business 
FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

-- ========== CREATE FULL-TEXT SEARCH INDEX ==========

-- Create generated column for full-text search
ALTER TABLE businesses 
ADD COLUMN IF NOT EXISTS name_description_tsv TSVECTOR 
GENERATED ALWAYS AS (
  to_tsvector('english', 
    COALESCE(name, '') || ' ' || 
    COALESCE(description, '') || ' ' ||
    COALESCE(address_line1, '') || ' ' ||
    COALESCE(city, '') || ' ' ||
    COALESCE(state, '')
  )
) STORED;

-- Create GIN index for full-text search
CREATE INDEX IF NOT EXISTS businesses_tsv_idx ON businesses USING GIN(name_description_tsv);

-- ========== CREATE TRIGGERS FOR UPDATED_AT ==========

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for tables with updated_at
CREATE TRIGGER update_businesses_updated_at 
  BEFORE UPDATE ON businesses 
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_appointments_updated_at 
  BEFORE UPDATE ON appointments 
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reviews_updated_at 
  BEFORE UPDATE ON reviews 
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ========== INSERT DEFAULT DATA ==========

-- Insert default regions
INSERT INTO regions (name, code, country, timezone) VALUES
  ('New York City', 'NYC', 'USA', 'America/New_York'),
  ('Los Angeles', 'LA', 'USA', 'America/Los_Angeles'),
  ('London', 'LON', 'UK', 'Europe/London'),
  ('Toronto', 'TOR', 'Canada', 'America/Toronto'),
  ('Sydney', 'SYD', 'Australia', 'Australia/Sydney')
ON CONFLICT (code) DO NOTHING;

-- Insert default categories (fixed hierarchy)
INSERT INTO categories (name, slug, description, icon, sort_order) VALUES
  ('Beauty & Salon', 'beauty-salon', 'Hair, nails, and beauty services', 'scissors', 1),
  ('Health & Wellness', 'health-wellness', 'Medical and wellness services', 'heart', 2),
  ('Professional Services', 'professional-services', 'Business and professional services', 'briefcase', 3),
  ('Home Services', 'home-services', 'Home maintenance and repair', 'home', 4),
  ('Food & Dining', 'food-dining', 'Restaurants and food services', 'utensils', 5)
ON CONFLICT (slug) DO NOTHING;

-- Insert sub-categories
WITH parent_cats AS (
  SELECT id, name FROM categories WHERE slug IN ('beauty-salon', 'health-wellness', 'professional-services', 'home-services', 'food-dining')
)
INSERT INTO categories (name, slug, description, icon, parent_id, sort_order) VALUES
  -- Beauty & Salon subcategories
  ('Hair Salons', 'hair-salons', 'Hair cutting and styling', 'scissors', (SELECT id FROM parent_cats WHERE name = 'Beauty & Salon'), 1),
  ('Nail Salons', 'nail-salons', 'Manicures and pedicures', 'hand', (SELECT id FROM parent_cats WHERE name = 'Beauty & Salon'), 2),
  ('Barbershops', 'barbershops', 'Traditional barber services', 'razor', (SELECT id FROM parent_cats WHERE name = 'Beauty & Salon'), 3),
  ('Spas', 'spas', 'Relaxation and spa treatments', 'spa', (SELECT id FROM parent_cats WHERE name = 'Beauty & Salon'), 4),
  
  -- Health & Wellness subcategories
  ('Doctors', 'doctors', 'Medical doctors and physicians', 'stethoscope', (SELECT id FROM parent_cats WHERE name = 'Health & Wellness'), 1),
  ('Dentists', 'dentists', 'Dental care and orthodontics', 'tooth', (SELECT id FROM parent_cats WHERE name = 'Health & Wellness'), 2),
  ('Physiotherapists', 'physiotherapists', 'Physical therapy and rehabilitation', 'activity', (SELECT id FROM parent_cats WHERE name = 'Health & Wellness'), 3),
  ('Mental Health', 'mental-health', 'Therapists and counselors', 'brain', (SELECT id FROM parent_cats WHERE name = 'Health & Wellness'), 4),
  
  -- Professional Services subcategories
  ('Lawyers', 'lawyers', 'Legal services and attorneys', 'scale', (SELECT id FROM parent_cats WHERE name = 'Professional Services'), 1),
  ('Accountants', 'accountants', 'Accounting and tax services', 'calculator', (SELECT id FROM parent_cats WHERE name = 'Professional Services'), 2),
  ('Consultants', 'consultants', 'Business consulting', 'briefcase', (SELECT id FROM parent_cats WHERE name = 'Professional Services'), 3),
  ('Real Estate', 'real-estate', 'Real estate agents and brokers', 'home', (SELECT id FROM parent_cats WHERE name = 'Professional Services'), 4),
  
  -- Home Services subcategories
  ('Plumbers', 'plumbers', 'Plumbing services', 'droplet', (SELECT id FROM parent_cats WHERE name = 'Home Services'), 1),
  ('Electricians', 'electricians', 'Electrical services', 'zap', (SELECT id FROM parent_cats WHERE name = 'Home Services'), 2),
  ('Cleaners', 'cleaners', 'Cleaning services', 'sparkles', (SELECT id FROM parent_cats WHERE name = 'Home Services'), 3),
  ('Landscapers', 'landscapers', 'Landscaping and gardening', 'tree', (SELECT id FROM parent_cats WHERE name = 'Home Services'), 4),
  
  -- Food & Dining subcategories
  ('Restaurants', 'restaurants', 'Dining establishments', 'utensils', (SELECT id FROM parent_cats WHERE name = 'Food & Dining'), 1),
  ('Cafes', 'cafes', 'Coffee shops and cafes', 'coffee', (SELECT id FROM parent_cats WHERE name = 'Food & Dining'), 2),
  ('Bakeries', 'bakeries', 'Baked goods and pastries', 'cake', (SELECT id FROM parent_cats WHERE name = 'Food & Dining'), 3),
  ('Catering', 'catering', 'Event catering services', 'truck', (SELECT id FROM parent_cats WHERE name = 'Food & Dining'), 4)
ON CONFLICT (slug) DO NOTHING;

-- ========== MIGRATION COMPLETE ==========

COMMENT ON TABLE regions IS 'Geographic regions for business segmentation';
COMMENT ON TABLE categories IS 'Fixed hierarchy of business categories';
COMMENT ON TABLE businesses IS 'Business listings with full-text search capability';
COMMENT ON TABLE services IS 'Services offered by businesses';
COMMENT ON TABLE appointments IS 'Customer appointments with service points';
COMMENT ON TABLE reviews IS 'Customer reviews requiring admin approval';
COMMENT ON TABLE business_hours IS 'Business operating hours';

-- Print migration completion message
DO $$
BEGIN
  RAISE NOTICE 'Business directory schema migration (baseline) completed successfully';
  RAISE NOTICE 'Created/Verified: 8 tables, 22 indexes, 3 triggers';
  RAISE NOTICE 'Inserted/Verified: 5 regions, 25 categories';
END $$;