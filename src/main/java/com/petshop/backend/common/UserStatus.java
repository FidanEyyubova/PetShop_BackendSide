package com.petshop.backend.common;

public enum UserStatus {
    /** Account can use every feature of its role. */
    ACTIVE,
    /** Vendor or service provider waiting for admin approval. */
    PENDING_APPROVAL,
    /** Seller application rejected by an admin. */
    REJECTED,
    /** Blocked by an admin. */
    SUSPENDED
}
