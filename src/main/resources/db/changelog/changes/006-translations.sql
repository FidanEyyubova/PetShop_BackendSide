--liquibase formatted sql

--changeset petshop-team:6-01-content-translations
--comment: EN / RU versions of user-entered text; the existing columns keep the Azerbaijani text.
ALTER TABLE products ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE sub_categories ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE stores ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE blogs ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE blog_sub_categories ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE hero_sections ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE about_sections ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE admin_site_services ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE contact_info ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE services ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE service_provider_profiles ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE team_members ADD COLUMN IF NOT EXISTS translations TEXT;
ALTER TABLE provider_galleries ADD COLUMN IF NOT EXISTS translations TEXT;
