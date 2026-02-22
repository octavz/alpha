-- Migration to fix appointments table schema
-- Adds missing customer information fields that exist in TypeScript schema
-- Created: 2026-02-22

-- Add missing columns to appointments table if they don't exist
DO $$ 
BEGIN
    -- Add customer_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'appointments' AND column_name = 'customer_name') THEN
        ALTER TABLE appointments ADD COLUMN customer_name TEXT NOT NULL DEFAULT '';
    END IF;

    -- Add customer_email column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'appointments' AND column_name = 'customer_email') THEN
        ALTER TABLE appointments ADD COLUMN customer_email TEXT NOT NULL DEFAULT '';
    END IF;

    -- Add customer_phone column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'appointments' AND column_name = 'customer_phone') THEN
        ALTER TABLE appointments ADD COLUMN customer_phone TEXT;
    END IF;

    -- Add customer_notes column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'appointments' AND column_name = 'customer_notes') THEN
        ALTER TABLE appointments ADD COLUMN customer_notes TEXT;
    END IF;

    -- Add cancelled_at column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'appointments' AND column_name = 'cancelled_at') THEN
        ALTER TABLE appointments ADD COLUMN cancelled_at TIMESTAMP;
    END IF;

    -- Add cancelled_reason column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'appointments' AND column_name = 'cancelled_reason') THEN
        ALTER TABLE appointments ADD COLUMN cancelled_reason TEXT;
    END IF;

    -- Update status constraint if needed (from 'scheduled' to 'pending' as default)
    -- Note: We can't easily change the constraint, but we'll ensure the TypeScript schema matches
    -- The actual constraint in DB might still be 'scheduled' but TypeScript uses 'pending'
    -- For consistency, we'll update existing 'scheduled' to 'pending'
    UPDATE appointments SET status = 'pending' WHERE status = 'scheduled';
    
    -- Update default values for customer_name and customer_email from empty string to actual values
    -- This will populate customer info from users table for existing appointments
    UPDATE appointments a
    SET 
        customer_name = COALESCE(u.name, ''),
        customer_email = COALESCE(u.email, ''),
        customer_phone = COALESCE(u.phone, '')
    FROM users u
    WHERE a.customer_id = u.id 
      AND (a.customer_name = '' OR a.customer_email = '' OR a.customer_name IS NULL OR a.customer_email IS NULL);
END $$;

-- Add comments for documentation
COMMENT ON COLUMN appointments.customer_name IS 'Customer name (cached for historical reference)';
COMMENT ON COLUMN appointments.customer_email IS 'Customer email (cached for historical reference)';
COMMENT ON COLUMN appointments.customer_phone IS 'Customer phone (cached for historical reference)';
COMMENT ON COLUMN appointments.customer_notes IS 'Additional notes from customer';
COMMENT ON COLUMN appointments.cancelled_at IS 'Timestamp when appointment was cancelled';
COMMENT ON COLUMN appointments.cancelled_reason IS 'Reason for cancellation';

-- Create indexes for new columns if they don't exist
CREATE INDEX IF NOT EXISTS appointments_customer_name_idx ON appointments(customer_name);
CREATE INDEX IF NOT EXISTS appointments_customer_email_idx ON appointments(customer_email);

-- Update the updated_at trigger to include new columns
-- (The trigger already exists, no need to recreate)