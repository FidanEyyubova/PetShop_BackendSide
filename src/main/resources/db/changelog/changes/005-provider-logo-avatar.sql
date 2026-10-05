--liquibase formatted sql

--changeset petshop-team:5-01-provider-logo-as-avatar
--comment: Service providers without an avatar get their center logo, so admin user lists show it.
UPDATE users
SET avatar_url = p.logo_url
FROM service_provider_profiles p
WHERE p.provider_id = users.id
  AND (users.avatar_url IS NULL OR users.avatar_url = '')
  AND p.logo_url IS NOT NULL
  AND p.logo_url <> '';
