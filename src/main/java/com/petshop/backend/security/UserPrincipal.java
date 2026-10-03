package com.petshop.backend.security;

import com.petshop.backend.common.Roles;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String password;
    private final String role;
    private final UserStatus status;
    private final boolean deleted;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String fullName, String email, String password, String role,
                         UserStatus status, boolean deleted) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.deleted = deleted;
        this.authorities = List.of(new SimpleGrantedAuthority(role));
    }

    public static UserPrincipal build(User user) {
        return create(user);
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPassword(),
                Roles.normalize(user.getRole()),
                user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus(),
                user.isDeleted());
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public UserStatus getStatus() { return status; }

    public boolean isAdmin() { return Roles.ROLE_ADMIN.equals(role); }
    public boolean isVendor() { return Roles.ROLE_VENDOR.equals(role); }
    public boolean isServiceProvider() { return Roles.ROLE_SERVICE_PROVIDER.equals(role); }
    public boolean isBuyer() { return Roles.ROLE_BUYER.equals(role); }

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    /** Suspended accounts cannot sign in. Pending sellers can sign in to finish their profile. */
    @Override
    public boolean isAccountNonLocked() { return status != UserStatus.SUSPENDED && status != UserStatus.REJECTED; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return !deleted; }
}
