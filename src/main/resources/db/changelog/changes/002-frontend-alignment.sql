--liquibase formatted sql

-- Brings the schema in line with the JPA entities so Hibernate can run with ddl-auto=validate.
-- Every statement is idempotent (IF NOT EXISTS / guarded DO blocks) because some environments
-- already contain tables that Hibernate created with ddl-auto=update.

--changeset petshop-team:2-01-create-missing-tables
CREATE TABLE IF NOT EXISTS about_sections (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    image_url VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS admin_site_services (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    image_url VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS blog_sub_categories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS contact_info (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255),
    mail VARCHAR(255),
    phone VARCHAR(255),
    working_hours VARCHAR(255),
    location_map_url TEXT,
    updated_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    reply_message TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    replied_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS hero_sections (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    bg_image_url VARCHAR(255),
    price NUMERIC(38,2),
    discount_price NUMERIC(38,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS site_settings (
    id BIGSERIAL PRIMARY KEY,
    instagram_url VARCHAR(255),
    tiktok_url VARCHAR(255),
    facebook_url VARCHAR(255),
    x_url VARCHAR(255),
    logo_url VARCHAR(255),
    updated_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    discount_type VARCHAR(255) NOT NULL,
    discount_value NUMERIC(38,2) NOT NULL,
    min_cart_amount NUMERIC(38,2),
    max_discount_amount NUMERIC(38,2),
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    usage_limit INTEGER,
    usage_count INTEGER,
    is_active BOOLEAN,
    created_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS sub_categories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS provider_galleries (
    id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    created_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS service_provider_profiles (
    id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT NOT NULL UNIQUE,
    center_name VARCHAR(255),
    service_type VARCHAR(255),
    bio TEXT,
    city VARCHAR(255),
    district VARCHAR(255),
    address VARCHAR(255),
    google_maps_url TEXT,
    phone VARCHAR(255),
    whatsapp VARCHAR(255),
    instagram VARCHAR(255),
    banner_url VARCHAR(255),
    logo_url VARCHAR(255),
    updated_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS vendor_bank_details (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT NOT NULL UNIQUE,
    iban VARCHAR(255),
    bank_name VARCHAR(255),
    account_holder VARCHAR(255),
    updated_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS vendor_payouts (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT NOT NULL,
    amount NUMERIC(38,2) NOT NULL,
    iban VARCHAR(255),
    bank_name VARCHAR(255),
    account_holder VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    processed_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS wishlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER DEFAULT 1,
    created_at TIMESTAMP
);

--changeset petshop-team:2-02-create-new-tables
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    remember_me BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL,
    note TEXT,
    changed_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS newsletter_subscribers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unsubscribed_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS uploaded_files (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    storage_key VARCHAR(255) NOT NULL UNIQUE,
    url TEXT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    original_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset petshop-team:2-03-add-missing-columns
ALTER TABLE users ADD COLUMN IF NOT EXISTS city_address VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_orders BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_promotions BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_blog BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

ALTER TABLE password_reset_tokens ADD COLUMN IF NOT EXISTS attempts INTEGER NOT NULL DEFAULT 0;

ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS district VARCHAR(255);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS street VARCHAR(255);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS floor VARCHAR(255);
ALTER TABLE user_addresses ADD COLUMN IF NOT EXISTS note TEXT;

ALTER TABLE categories ADD COLUMN IF NOT EXISTS icon VARCHAR(255);
ALTER TABLE categories ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE products ADD COLUMN IF NOT EXISTS vendor_id BIGINT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS sub_category_id BIGINT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS sub_category_name VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS sold_count INTEGER DEFAULT 0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE products ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE products ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE products ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE products ALTER COLUMN badges TYPE TEXT;
ALTER TABLE products ALTER COLUMN sizes TYPE TEXT;
ALTER TABLE products ALTER COLUMN flavors TYPE TEXT;

ALTER TABLE stores ADD COLUMN IF NOT EXISTS tagline VARCHAR(255);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS alt_phone VARCHAR(255);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS city VARCHAR(255);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS working_hours VARCHAR(255);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS delivery_info TEXT;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS features TEXT;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS voen VARCHAR(20);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS badge_text VARCHAR(255);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS response_rate INTEGER;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS checkout_group VARCHAR(64);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS store_id BIGINT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS vendor_id BIGINT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS store_name VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS subtotal NUMERIC(12,2);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_fee NUMERIC(12,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(12,2) DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS coupon_code VARCHAR(64);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS payment_method VARCHAR(30);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS customer_name VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS customer_phone VARCHAR(50);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS note TEXT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS estimated_delivery_from TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS estimated_delivery_to TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivered_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS courier_name VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancel_reason TEXT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS archived_by_vendor BOOLEAN DEFAULT FALSE;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE order_items ADD COLUMN IF NOT EXISTS store_id BIGINT;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS product_title VARCHAR(255);
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS product_image TEXT;
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS original_price NUMERIC(12,2);

ALTER TABLE services ADD COLUMN IF NOT EXISTS price_max NUMERIC(10,2);
ALTER TABLE services ADD COLUMN IF NOT EXISTS duration_text VARCHAR(100);
ALTER TABLE services ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;
ALTER TABLE services ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE team_members ADD COLUMN IF NOT EXISTS certifications TEXT;
ALTER TABLE team_members ADD COLUMN IF NOT EXISTS bio TEXT;
ALTER TABLE team_members ADD COLUMN IF NOT EXISTS featured BOOLEAN DEFAULT FALSE;

ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS amenities TEXT;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS tags TEXT;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS rating NUMERIC(3,2) DEFAULT 0;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS page_views BIGINT DEFAULT 0;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS contact_clicks BIGINT DEFAULT 0;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS wishlist_adds BIGINT DEFAULT 0;
ALTER TABLE service_provider_profiles ALTER COLUMN google_maps_url TYPE TEXT;

ALTER TABLE vendor_bank_details ADD COLUMN IF NOT EXISTS voen VARCHAR(20);
ALTER TABLE vendor_payouts ADD COLUMN IF NOT EXISTS admin_note TEXT;

ALTER TABLE reviews ADD COLUMN IF NOT EXISTS reply_date TIMESTAMP;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS verified_purchase BOOLEAN DEFAULT FALSE;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS pet_name VARCHAR(255);
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS pet_photo VARCHAR(500);
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE blogs ADD COLUMN IF NOT EXISTS category_id BIGINT;
ALTER TABLE blogs ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE blogs ALTER COLUMN excerpt TYPE TEXT;
ALTER TABLE blogs ALTER COLUMN tags TYPE TEXT;

ALTER TABLE blog_sub_categories ADD COLUMN IF NOT EXISTS slug VARCHAR(255);

--changeset petshop-team:2-04-normalize-data splitStatements:false
-- Roles: one vocabulary (ROLE_BUYER, ROLE_VENDOR, ROLE_SERVICE_PROVIDER, ROLE_ADMIN).
UPDATE users SET role = 'ROLE_ADMIN' WHERE upper(role) IN ('ADMIN', 'ROLE_SUPER_ADMIN', 'SUPER_ADMIN', 'ROLE_ADMIN');
UPDATE users SET role = 'ROLE_VENDOR' WHERE upper(role) IN ('VENDOR', 'SELLER', 'ROLE_SELLER');
UPDATE users SET role = 'ROLE_SERVICE_PROVIDER' WHERE upper(role) IN ('SERVICE_PROVIDER', 'PROVIDER');
UPDATE users SET role = 'ROLE_BUYER' WHERE role NOT IN ('ROLE_ADMIN', 'ROLE_VENDOR', 'ROLE_SERVICE_PROVIDER', 'ROLE_BUYER');
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;

-- Emails are compared case-insensitively; store them lower-case when that does not create a duplicate.
UPDATE users u SET email = lower(u.email)
WHERE u.email <> lower(u.email)
  AND NOT EXISTS (SELECT 1 FROM users o WHERE o.id <> u.id AND lower(o.email) = lower(u.email));

-- Orders: enum values only.
UPDATE orders SET status = 'DELIVERED' WHERE upper(status) = 'COMPLETED';
UPDATE orders SET status = upper(status) WHERE upper(status) IN ('PENDING', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED');
UPDATE orders SET status = 'PENDING' WHERE status NOT IN ('PENDING', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED');
UPDATE orders SET payment_status = 'PENDING' WHERE payment_status IS NULL OR upper(payment_status) NOT IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED');
UPDATE orders SET payment_status = upper(payment_status);
UPDATE orders SET subtotal = total_amount WHERE subtotal IS NULL;
UPDATE orders SET shipping_fee = 0 WHERE shipping_fee IS NULL;
UPDATE orders SET discount_amount = 0 WHERE discount_amount IS NULL;
UPDATE orders SET archived_by_vendor = FALSE WHERE archived_by_vendor IS NULL;

-- Payouts: enum values only.
UPDATE vendor_payouts SET status = upper(status);
UPDATE vendor_payouts SET status = 'PENDING' WHERE status NOT IN ('PENDING', 'APPROVED', 'COMPLETED', 'REJECTED');

-- Reviews: canonical target types.
UPDATE reviews SET target_type = 'SERVICE_PROVIDER' WHERE upper(target_type) IN ('SERVICE', 'PROVIDER', 'SERVICECENTER', 'SERVICE_CENTER', 'SERVICE_PROVIDER');
UPDATE reviews SET target_type = upper(target_type) WHERE upper(target_type) IN ('PRODUCT', 'STORE');

-- Pet types: canonical ids used by the storefront.
UPDATE products SET pet_type = CASE lower(trim(pet_type))
        WHEN 'dog' THEN 'dogs' WHEN 'dogs' THEN 'dogs'
        WHEN 'cat' THEN 'cats' WHEN 'cats' THEN 'cats'
        WHEN 'bird' THEN 'birds' WHEN 'birds' THEN 'birds'
        WHEN 'fish' THEN 'fish'
        WHEN 'rodent' THEN 'small-pets' WHEN 'small-pets' THEN 'small-pets' WHEN 'small_pets' THEN 'small-pets'
        WHEN 'reptile' THEN 'reptiles' WHEN 'reptiles' THEN 'reptiles'
        ELSE NULL END
WHERE pet_type IS NOT NULL;

UPDATE products SET is_active = TRUE WHERE is_active IS NULL;
UPDATE products SET review_count = 0 WHERE review_count IS NULL;
UPDATE products SET sold_count = 0 WHERE sold_count IS NULL;
UPDATE products SET stock_quantity = 0 WHERE stock_quantity IS NULL;
UPDATE products p SET vendor_id = s.owner_id FROM stores s WHERE p.vendor_id IS NULL AND p.store_id = s.id;

UPDATE categories SET type = 'PRODUCT' WHERE type IS NULL OR type = '' OR lower(type) = 'string';
UPDATE stores SET verified = FALSE WHERE verified IS NULL;
UPDATE stores SET review_count = 0 WHERE review_count IS NULL;
UPDATE services SET active = TRUE WHERE active IS NULL;
UPDATE team_members SET featured = FALSE WHERE featured IS NULL;

-- Snapshot data for existing order lines.
UPDATE order_items oi SET product_title = p.title, store_id = p.store_id
FROM products p WHERE oi.product_id = p.id AND oi.product_title IS NULL;
UPDATE order_items SET original_price = unit_price WHERE original_price IS NULL;

-- Remove duplicate cart and wishlist rows before adding unique constraints (keep the newest row).
DELETE FROM cart_items a USING cart_items b
WHERE a.user_id = b.user_id AND a.product_id = b.product_id AND a.id < b.id;
DELETE FROM wishlists a USING wishlists b
WHERE a.user_id = b.user_id AND a.product_id = b.product_id AND a.id < b.id;

-- Blog category slugs.
UPDATE blog_sub_categories SET slug = lower(regexp_replace(title, '[^a-zA-Z0-9]+', '-', 'g')) || '-' || id WHERE slug IS NULL;

--changeset petshop-team:2-05-constraints splitStatements:false
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_cart_items_user_product') THEN
        ALTER TABLE cart_items ADD CONSTRAINT uq_cart_items_user_product UNIQUE (user_id, product_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_wishlists_user_product') THEN
        ALTER TABLE wishlists ADD CONSTRAINT uq_wishlists_user_product UNIQUE (user_id, product_id);
    END IF;
    -- Only add the review uniqueness rule when existing data already satisfies it.
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_reviews_user_target')
       AND NOT EXISTS (SELECT 1 FROM reviews GROUP BY user_id, target_type, target_id HAVING count(*) > 1) THEN
        ALTER TABLE reviews ADD CONSTRAINT uq_reviews_user_target UNIQUE (user_id, target_type, target_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_reviews_rating_range') THEN
        ALTER TABLE reviews ADD CONSTRAINT ck_reviews_rating_range CHECK (rating BETWEEN 1 AND 5) NOT VALID;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_products_price_positive') THEN
        ALTER TABLE products ADD CONSTRAINT ck_products_price_positive CHECK (price >= 0) NOT VALID;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_products_stock_positive') THEN
        ALTER TABLE products ADD CONSTRAINT ck_products_stock_positive CHECK (stock_quantity >= 0) NOT VALID;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_cart_items_quantity_positive') THEN
        ALTER TABLE cart_items ADD CONSTRAINT ck_cart_items_quantity_positive CHECK (quantity >= 1) NOT VALID;
    END IF;
    -- Case-insensitive email uniqueness when existing data allows it.
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'uq_users_email_lower')
       AND NOT EXISTS (SELECT 1 FROM users GROUP BY lower(email) HAVING count(*) > 1) THEN
        CREATE UNIQUE INDEX uq_users_email_lower ON users (lower(email));
    END IF;
END $$;

--changeset petshop-team:2-06-indexes
CREATE INDEX IF NOT EXISTS idx_products_category ON products (category_id);
CREATE INDEX IF NOT EXISTS idx_products_sub_category ON products (sub_category_id);
CREATE INDEX IF NOT EXISTS idx_products_store ON products (store_id);
CREATE INDEX IF NOT EXISTS idx_products_vendor ON products (vendor_id);
CREATE INDEX IF NOT EXISTS idx_products_pet_type ON products (pet_type);
CREATE INDEX IF NOT EXISTS idx_products_active_created ON products (is_active, created_at);
CREATE INDEX IF NOT EXISTS idx_products_sold ON products (sold_count);
CREATE INDEX IF NOT EXISTS idx_orders_buyer_created ON orders (buyer_id, created_at);
CREATE INDEX IF NOT EXISTS idx_orders_vendor_created ON orders (vendor_id, created_at);
CREATE INDEX IF NOT EXISTS idx_orders_store ON orders (store_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items (order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items (product_id);
CREATE INDEX IF NOT EXISTS idx_order_status_history_order ON order_status_history (order_id);
CREATE INDEX IF NOT EXISTS idx_reviews_target ON reviews (target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews (user_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items (user_id);
CREATE INDEX IF NOT EXISTS idx_wishlists_user ON wishlists (user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_user ON user_addresses (user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_email ON password_reset_tokens (email);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_schedules_provider ON schedules (provider_id);
CREATE INDEX IF NOT EXISTS idx_team_members_provider ON team_members (provider_id);
CREATE INDEX IF NOT EXISTS idx_provider_galleries_provider ON provider_galleries (provider_id);
CREATE INDEX IF NOT EXISTS idx_services_provider ON services (provider_id);
CREATE INDEX IF NOT EXISTS idx_stores_owner ON stores (owner_id);
CREATE INDEX IF NOT EXISTS idx_vendor_payouts_vendor ON vendor_payouts (vendor_id);
CREATE INDEX IF NOT EXISTS idx_blogs_category ON blogs (category_id);
CREATE INDEX IF NOT EXISTS idx_uploaded_files_owner ON uploaded_files (owner_id);

--changeset petshop-team:2-07-users-password-optional
--comment: Google sign-in accounts have no password; changeset 15 was recorded on some databases without taking effect.
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;
