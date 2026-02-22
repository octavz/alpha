# Business Directory Implementation Plan

## Project Overview
A business directory application with search, appointments, and dual user roles (customers and businesses). The application features public search, appointment booking, business backoffice, and admin management.

## Core Requirements

### User Roles:
1. **Customers**: Search businesses, book appointments, leave reviews
2. **Businesses**: Register, manage profile, handle appointments, view analytics
3. **Admins**: Approve businesses/reviews, manage users, view platform analytics

### Key Features:
- Public search page (homepage) with region-based filtering
- Business listings with detailed profiles
- Appointment booking system with time slots
- Business verification via email
- Admin dashboard for platform management
- No payment integration required
- Mobile app development after web completion

## Database Schema

### Current Tables (Existing):
- `users` (id, email, password_hash, google_id, name, email_verified, avatar_url, created_at, updated_at)
- `sessions`
- `email_verifications`
- `password_resets`

### New Tables Required:

#### 1. User Extensions
```sql
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'customer' 
  CHECK (role IN ('customer', 'business_admin', 'business_staff', 'admin'));
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users ADD COLUMN region_id UUID REFERENCES regions(id);
ALTER TABLE users ADD COLUMN is_banned BOOLEAN DEFAULT false;
```

#### 2. Regions Table (Geographic Segmentation)
```sql
CREATE TABLE regions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100) NOT NULL,
  code VARCHAR(10) UNIQUE NOT NULL, -- e.g., "NYC", "LA", "LON"
  country VARCHAR(100) NOT NULL,
  timezone VARCHAR(50) NOT NULL,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW()
);
```

#### 3. Categories Table (Fixed Hierarchy)
```sql
CREATE TABLE categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100) NOT NULL,
  slug VARCHAR(100) UNIQUE NOT NULL,
  description TEXT,
  icon VARCHAR(50),
  parent_id UUID REFERENCES categories(id) ON DELETE SET NULL,
  sort_order INTEGER DEFAULT 0,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW()
);
```

#### 4. Businesses Table
```sql
CREATE TABLE businesses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  slug VARCHAR(255) UNIQUE NOT NULL,
  description TEXT, -- Up to 2000 chars
  category_id UUID REFERENCES categories(id),
  region_id UUID REFERENCES regions(id) NOT NULL,
  
  -- Contact Information
  email VARCHAR(255),
  phone VARCHAR(50),
  website VARCHAR(255),
  
  -- Address (for geolocation)
  address_line1 VARCHAR(255),
  address_line2 VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  zip_code VARCHAR(20),
  country VARCHAR(100),
  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),
  
  -- Business Details
  logo_url VARCHAR(500), -- Random internet logos for test data
  cover_image_url VARCHAR(500),
  is_verified BOOLEAN DEFAULT false,
  is_active BOOLEAN DEFAULT true,
  is_featured BOOLEAN DEFAULT false,
  verification_status VARCHAR(20) DEFAULT 'pending' 
    CHECK (verification_status IN ('pending', 'approved', 'rejected')),
  
  -- Service Points (multiple chairs/employees)
  service_points_count INTEGER DEFAULT 1,
  
  -- Metadata
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  
  -- Full-text search index
  CONSTRAINT businesses_name_description_tsv 
    GENERATED ALWAYS AS (to_tsvector('english', 
      COALESCE(name, '') || ' ' || 
      COALESCE(description, '') || ' ' ||
      COALESCE(address_line1, '') || ' ' ||
      COALESCE(city, '') || ' ' ||
      COALESCE(state, ''))) STORED
);
```

#### 5. Services Table
```sql
CREATE TABLE services (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  duration_minutes INTEGER NOT NULL, -- e.g., 30, 60, 90
  price DECIMAL(10, 2), -- NULL for free services
  is_active BOOLEAN DEFAULT true,
  sort_order INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
);
```

#### 6. Appointments Table
```sql
CREATE TABLE appointments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
  customer_id UUID REFERENCES users(id) ON DELETE CASCADE,
  service_id UUID REFERENCES services(id),
  
  -- Appointment Details
  appointment_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  service_point_number INTEGER, -- Which chair/station (1, 2, 3...)
  
  -- Customer Information (cached for historical reference)
  customer_name VARCHAR(255) NOT NULL,
  customer_email VARCHAR(255) NOT NULL,
  customer_phone VARCHAR(50),
  customer_notes TEXT,
  
  -- Status
  status VARCHAR(20) DEFAULT 'pending' 
    CHECK (status IN ('pending', 'confirmed', 'completed', 'cancelled', 'no_show')),
  
  -- Metadata
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  cancelled_at TIMESTAMP,
  cancelled_reason TEXT
);
```

#### 7. Reviews Table
```sql
CREATE TABLE reviews (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
  customer_id UUID REFERENCES users(id) ON DELETE CASCADE,
  appointment_id UUID REFERENCES appointments(id) ON DELETE SET NULL,
  
  -- Review Details
  rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
  title VARCHAR(255),
  comment TEXT,
  is_approved BOOLEAN DEFAULT false, -- Admin approval required
  is_featured BOOLEAN DEFAULT false,
  
  -- Metadata
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

#### 8. Business Hours Table
```sql
CREATE TABLE business_hours (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id UUID REFERENCES businesses(id) ON DELETE CASCADE,
  day_of_week INTEGER NOT NULL CHECK (day_of_week >= 0 AND day_of_week <= 6), -- 0=Sunday
  open_time TIME,
  close_time TIME,
  is_closed BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW()
);
```

## API Architecture

### Authentication & User Management:
```
GET    /api/auth/me                    - Get current user with role
POST   /api/auth/register/business     - Business registration
POST   /api/auth/login/google          - Google OAuth
PUT    /api/users/profile              - Update profile (phone, region)
GET    /api/users/:id                  - Get user (admin only)
PUT    /api/users/:id/ban              - Ban user (admin only)
```

### Business Management:
```
GET    /api/businesses                 - Search businesses (public)
GET    /api/businesses/:id             - Get business details (public)
POST   /api/businesses                 - Create business (business role)
PUT    /api/businesses/:id             - Update business (owner/admin)
GET    /api/businesses/:id/services    - Get business services (public)
GET    /api/businesses/:id/reviews     - Get business reviews (public)
GET    /api/businesses/:id/hours       - Get business hours (public)
GET    /api/my-business                - Get current user's business
GET    /api/businesses/region/:code    - Get businesses by region (public)
```

### Search API:
```
GET /api/search/businesses
Query Parameters:
  - q: search text (name, description, address)
  - region: region code (required)
  - category: category slug
  - lat/lng: customer location (for distance sorting)
  - rating_min: minimum rating
  - page/limit: pagination
  - sort: relevance, rating, distance, name
```

### Appointment Management:
```
GET    /api/appointments/availability/:businessId  - Get available slots
POST   /api/appointments                          - Create appointment
GET    /api/appointments/my-appointments          - Customer appointments
GET    /api/appointments/business/:businessId     - Business appointments
PUT    /api/appointments/:id/status               - Update status
DELETE /api/appointments/:id                      - Cancel appointment
GET    /api/appointments/:id                      - Get appointment details
```

### Review Management:
```
POST   /api/reviews                              - Create review
GET    /api/reviews/business/:businessId         - Get business reviews
PUT    /api/reviews/:id/approve                  - Approve review (admin)
DELETE /api/reviews/:id                          - Delete review (admin/owner)
```

### Admin API:
```
GET    /api/admin/businesses/pending            - Pending business approvals
PUT    /api/admin/businesses/:id/verify         - Approve/reject business
GET    /api/admin/reviews/pending               - Pending review approvals
GET    /api/admin/appointments                  - All appointments (with filters)
GET    /api/admin/stats                         - Platform statistics
GET    /api/admin/users                         - User management
```

## Frontend Architecture

### Component Structure:
```
src/
├── components/
│   ├── layout/
│   │   ├── Header.tsx           - With region selector, search bar
│   │   ├── Footer.tsx
│   │   └── Navigation.tsx       - Role-based navigation
│   ├── business/
│   │   ├── BusinessCard.tsx     - Grid/list view item
│   │   ├── BusinessDetail.tsx   - Full business page
│   │   ├── SearchFilters.tsx    - Category, rating, distance filters
│   │   └── BusinessMap.tsx      - Map view of businesses
│   ├── appointment/
│   │   ├── BookingForm.tsx      - Date/time/service selection
│   │   ├── CalendarView.tsx     - Business calendar
│   │   ├── TimeSlotPicker.tsx   - Available time slots
│   │   └── AppointmentCard.tsx  - Appointment list item
│   ├── auth/
│   │   ├── LoginForm.tsx
│   │   ├── RegisterForm.tsx
│   │   ├── BusinessRegisterForm.tsx
│   │   └── GoogleAuthButton.tsx
│   ├── common/
│   │   ├── RegionSelector.tsx   - Region dropdown
│   │   ├── CategoryFilter.tsx   - Category selection
│   │   ├── RatingDisplay.tsx    - Star ratings
│   │   └── LoadingSpinner.tsx
│   └── admin/
│       ├── AdminDashboard.tsx
│       ├── BusinessApprovalList.tsx
│       ├── ReviewApprovalList.tsx
│       └── UserManagement.tsx
├── pages/
│   ├── HomePage.tsx             - Public search (default)
│   ├── BusinessDetailPage.tsx
│   ├── BookingPage.tsx
│   ├── CustomerDashboard.tsx    - My appointments, reviews
│   ├── BusinessDashboard.tsx    - Appointment management, analytics
│   └── AdminPanel.tsx           - Admin dashboard
├── hooks/
│   ├── useSearch.ts             - Search business hook
│   ├── useBusiness.ts           - Business data hook
│   ├── useAppointment.ts        - Appointment management
│   └── useAuth.ts               - Authentication state
├── stores/
│   ├── auth.store.ts            - Auth state (Zustand)
│   ├── search.store.ts          - Search filters state
│   └── ui.store.ts              - UI state (theme, region)
├── services/
│   ├── api.service.ts           - Axios instance
│   ├── business.service.ts      - Business API calls
│   ├── appointment.service.ts   - Appointment API calls
│   └── auth.service.ts          - Auth API calls
└── utils/
    ├── constants.ts             - Categories, regions, etc.
    ├── validation.ts            - Form validation
    ├── formatters.ts            - Date/time formatting
    └── geolocation.ts           - Distance calculations
```

### Key Pages:

1. **Home Page (Public Search)** - Region selector, search bar, filters, business grid/list
2. **Business Detail Page** - Business info, services, reviews, booking CTA
3. **Booking Page** - Service selection, date/time picker, customer details
4. **Customer Dashboard** - Upcoming/past appointments, profile management
5. **Business Backoffice** - Appointment calendar, business profile, services management
6. **Admin Panel** - Platform stats, business/review approvals, user management

## Search Implementation

### PostgreSQL Full-Text Search:
- Region-locked search (user must select region first)
- Full-text search on name, description, address fields
- Category filtering
- Rating filtering
- Distance sorting (if location provided)
- Pagination with infinite scroll

### Search Query Example:
```sql
SELECT 
  b.*,
  c.name as category_name,
  r.name as region_name,
  ts_rank(businesses_name_description_tsv, plainto_tsquery('english', :query)) as relevance,
  -- Calculate distance if lat/lng provided
  CASE 
    WHEN :lat IS NOT NULL AND :lng IS NOT NULL 
    THEN earth_distance(
      ll_to_earth(b.latitude, b.longitude),
      ll_to_earth(:lat, :lng)
    )
    ELSE NULL
  END as distance_meters
FROM businesses b
JOIN categories c ON b.category_id = c.id
JOIN regions r ON b.region_id = r.id
WHERE 
  b.region_id = (SELECT id FROM regions WHERE code = :region_code)
  AND b.verification_status = 'approved'
  AND b.is_active = true
  AND (
    :query IS NULL 
    OR businesses_name_description_tsv @@ plainto_tsquery('english', :query)
  )
  AND (:category_id IS NULL OR b.category_id = :category_id)
  AND (:rating_min IS NULL OR (
    SELECT COALESCE(AVG(rating), 0) 
    FROM reviews 
    WHERE business_id = b.id AND is_approved = true
  ) >= :rating_min)
ORDER BY
  CASE :sort
    WHEN 'distance' THEN distance_meters
    WHEN 'rating' THEN (
      SELECT COALESCE(AVG(rating), 0) 
      FROM reviews 
      WHERE business_id = b.id AND is_approved = true
    ) DESC
    WHEN 'name' THEN b.name
    ELSE relevance DESC
  END
LIMIT :limit OFFSET :offset;
```

## Appointment Logic

### Availability Calculation:
1. Get business hours for the day
2. Get existing appointments for the date
3. Get service duration
4. Get service points count
5. Generate available slots considering:
   - Operating hours
   - Existing appointments
   - Service duration
   - Multiple service points (parallel appointments)
   - Buffer times between appointments

### Booking Flow:
1. Customer selects business
2. Chooses service (duration, price)
3. Selects date from calendar
4. Sees available time slots (considering service points)
5. Chooses time slot
6. Provides contact details (pre-filled if logged in)
7. Confirms booking
8. Receives confirmation email/SMS

## Test Data Strategy

### Categories (Fixed Set - 20-30 categories):
```typescript
const categories = [
  { name: "Hair Salons", slug: "hair-salons", icon: "scissors", parent: "Beauty & Salon" },
  { name: "Nail Salons", slug: "nail-salons", icon: "hand", parent: "Beauty & Salon" },
  { name: "Barbershops", slug: "barbershops", icon: "razor", parent: "Beauty & Salon" },
  { name: "Doctors", slug: "doctors", icon: "stethoscope", parent: "Health & Wellness" },
  { name: "Dentists", slug: "dentists", icon: "tooth", parent: "Health & Wellness" },
  { name: "Physiotherapists", slug: "physiotherapists", icon: "activity", parent: "Health & Wellness" },
  { name: "Lawyers", slug: "lawyers", icon: "scale", parent: "Professional Services" },
  { name: "Accountants", slug: "accountants", icon: "calculator", parent: "Professional Services" },
  { name: "Consultants", slug: "consultants", icon: "briefcase", parent: "Professional Services" },
  // ... more categories
];
```

### Regions (Example):
```typescript
const regions = [
  { name: "New York City", code: "NYC", country: "USA", timezone: "America/New_York" },
  { name: "Los Angeles", code: "LA", country: "USA", timezone: "America/Los_Angeles" },
  { name: "London", code: "LON", country: "UK", timezone: "Europe/London" },
  { name: "Toronto", code: "TOR", country: "Canada", timezone: "America/Toronto" },
  { name: "Sydney", code: "SYD", country: "Australia", timezone: "Australia/Sydney" },
];
```

### Business Test Data (100-200 businesses):
- **Name Generation**: Realistic business names
- **Descriptions**: 50-200 characters, varied content
- **Logos**: Random logo URLs from services like:
  - `https://logo.clearbit.com/{domain}.com`
  - `https://picsum.photos/200/200?random={id}`
  - `https://via.placeholder.com/200x200/007bff/ffffff?text=${businessInitials}`
- **Addresses**: Real addresses within each region
- **Coordinates**: Random coordinates within region boundaries
- **Service Points**: Random 1-10 for each business
- **Verification Status**: Mix of pending, approved, rejected

### Services for Each Business:
- 3-10 services per business
- Varied durations (15, 30, 45, 60, 90 minutes)
- Some with prices, some free

### Appointment Test Data:
- 500-1000 appointments across all businesses
- Mix of statuses (pending, confirmed, completed, cancelled)
- Historical and future dates

## Implementation Timeline

### Week 1: Database & Backend Foundation
- Extend database schema with all tables
- Create migration scripts
- Implement role-based authentication
- Create basic business CRUD API

### Week 2: Search & Business APIs
- Implement region-based search
- Create full-text search with PostgreSQL
- Build business detail API
- Create services and hours APIs

### Week 3: Appointment System
- Implement availability calculation
- Create appointment booking API
- Build appointment management APIs
- Add email notifications

### Week 4: Frontend - Public Pages
- Build home page with search
- Create business listing components
- Implement business detail page
- Add region selector

### Week 5: Frontend - Booking & User Dashboards
- Build booking flow
- Create customer dashboard
- Build business backoffice
- Implement authentication flows

### Week 6: Admin Panel & Polish
- Build admin dashboard
- Implement approval workflows
- Add test data seed script
- Performance optimization

### Week 7: Testing & Deployment
- End-to-end testing
- Load testing
- Deployment preparation
- Documentation

## Technical Considerations

### Performance:
1. **Database Indexes**: Critical for search and filtering
2. **Query Optimization**: Use EXPLAIN ANALYZE for complex queries
3. **Caching**: Redis for search results, business listings
4. **Pagination**: Always implement to prevent large result sets
5. **Lazy Loading**: For images and map markers

### Scalability:
1. **Database Partitioning**: By region for large datasets
2. **Read Replicas**: For search-heavy operations
3. **CDN**: For business logos and images
4. **Queue System**: For email notifications (BullMQ/Kafka)

### Security:
1. **Input Validation**: All API endpoints
2. **SQL Injection Prevention**: Use parameterized queries
3. **XSS Protection**: Sanitize user inputs
4. **Rate Limiting**: Per IP and per user
5. **CORS Configuration**: Strict origin checking

## Open Questions for Clarification

1. **Region Selection**: 
   - Should users be automatically assigned a region based on IP?
   - Can users switch regions easily?
   - Should we remember user's last selected region?

2. **Business Verification**:
   - What documents are needed for verification?
   - Should there be different verification levels?
   - What's the turnaround time for approval?

3. **Appointment Limits**:
   - Maximum appointments per customer per day?
   - Cancellation policy (how far in advance can they cancel)?
   - No-show policy (ban after X no-shows)?

4. **Review System**:
   - Can customers review without appointment?
   - Response system for businesses to reply to reviews?
   - Review moderation guidelines?

5. **Admin Features**:
   - Bulk actions for approvals?
   - Export functionality for appointments?
   - Audit logs for admin actions?

6. **Mobile App Features**:
   - Which features are essential for mobile vs web?
   - Push notification requirements?
   - Offline capabilities?

## Next Steps

1. **Start with Database Schema**: Create migration files for all new tables
2. **Update User Model**: Add role, phone, region_id fields
3. **Implement Region-based Search**: Core search functionality
4. **Build Public Search Page**: First user-facing feature
5. **Create Test Data**: Seed database with realistic businesses and appointments

This plan provides a comprehensive roadmap for building a production-ready business directory with appointment booking. The architecture is designed to be scalable, maintainable, and user-friendly.