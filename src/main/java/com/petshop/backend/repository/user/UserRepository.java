package com.petshop.backend.repository.user;

import com.petshop.backend.common.UserStatus;
import com.petshop.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByIdIn(Collection<Long> ids);

    List<User> findByRoleAndStatusAndDeletedAtIsNull(String role, UserStatus status);
}
