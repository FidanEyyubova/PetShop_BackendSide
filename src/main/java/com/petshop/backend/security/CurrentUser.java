package com.petshop.backend.security;

import com.petshop.backend.common.UserStatus;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.UnauthorizedException;

import java.util.Objects;

/** Ownership and status guards used by services. */
public final class CurrentUser {

    private CurrentUser() {}

    public static UserPrincipal require(UserPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return principal;
    }

    /** Throws 403 unless the principal owns the resource or is an admin. */
    public static void requireOwnerOrAdmin(UserPrincipal principal, Long ownerId, String resource) {
        require(principal);
        if (!principal.isAdmin() && !Objects.equals(principal.getId(), ownerId)) {
            throw new AccessDeniedCustomException("NOT_OWNER", "You do not have access to this " + resource);
        }
    }

    /** Throws 403 for sellers whose account is still waiting for approval. */
    public static void requireApproved(UserPrincipal principal) {
        require(principal);
        if (!principal.isAdmin() && principal.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedCustomException("ACCOUNT_PENDING_APPROVAL",
                    "Your account is waiting for admin approval");
        }
    }
}
