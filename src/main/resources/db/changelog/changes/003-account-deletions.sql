--liquibase formatted sql

--changeset petshop-team:3-01-account-deletions
--comment: Log of deleted accounts (self-service or by an admin) with the reason, kept after the user row is anonymised.
CREATE TABLE IF NOT EXISTS account_deletions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(50),
    full_name VARCHAR(255),
    email VARCHAR(255),
    reason VARCHAR(50),
    comment TEXT,
    deleted_by VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_account_deletions_created_at ON account_deletions (created_at);
