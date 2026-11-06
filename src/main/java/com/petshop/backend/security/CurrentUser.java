package com.petshop.backend.security;

import com.petshop.backend.common.UserStatus;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.UnauthorizedException;

import java.util.Objects;

public final class CurrentUser {

    private CurrentUser() {}

    public static UserPrincipal require(UserPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return principal;
    }

    public static void requireOwnerOrAdmin(UserPrincipal principal, Long ownerId, String resource) {
        require(principal);
        if (!principal.isAdmin() && !Objects.equals(principal.getId(), ownerId)) {
            throw new AccessDeniedCustomException("NOT_OWNER", "You do not have access to this " + resource);
        }
    }

    public static void requireApproved(UserPrincipal principal) {
        require(principal);
        if (!principal.isAdmin() && principal.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedCustomException("ACCOUNT_PENDING_APPROVAL",
                    "Your account is waiting for admin approval");
        }
    }
}
