--liquibase formatted sql

--changeset petshop-team:4-01-store-social-contacts
--comment: WhatsApp number and Instagram handle / URL on the store contact details.
ALTER TABLE stores ADD COLUMN IF NOT EXISTS whatsapp VARCHAR(255);
ALTER TABLE stores ADD COLUMN IF NOT EXISTS instagram VARCHAR(300);
