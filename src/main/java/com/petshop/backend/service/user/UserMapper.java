package com.petshop.backend.service.user;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.repository.product.StoreRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final StoreRepository storeRepository;

    public UserMapper(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public UserResponse toResponse(User user) {
        String role = Roles.normalize(user.getRole());
        Long storeId = Roles.ROLE_VENDOR.equals(role)
                ? storeRepository.findFirstByOwnerIdOrderByIdAsc(user.getId()).map(Store::getId).orElse(null)
                : null;
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(role)
                .accountType(accountType(role))
                .status(user.getStatus() == null ? null : user.getStatus().name())
                .avatarUrl(user.getAvatarUrl())
                .cityAddress(user.getCityAddress())
                .storeId(storeId)
                .createdAt(user.getCreatedAt())
                .build();
    }

    /** Role names used by the frontend register form. */
    public static String accountType(String role) {
        return switch (role) {
            case Roles.ROLE_VENDOR -> "vendor";
            case Roles.ROLE_SERVICE_PROVIDER -> "service";
            case Roles.ROLE_ADMIN -> "admin";
            default -> "customer";
        };
    }
}
