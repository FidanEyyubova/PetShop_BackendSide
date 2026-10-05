package com.petshop.backend.repository.user;

import com.petshop.backend.entity.user.AccountDeletion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDeletionRepository extends JpaRepository<AccountDeletion, Long> {

    Page<AccountDeletion> findByDeletedBy(String deletedBy, Pageable pageable);
}
