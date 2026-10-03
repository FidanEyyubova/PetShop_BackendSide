package com.petshop.backend.repository.vendor;

import com.petshop.backend.common.PayoutStatus;
import com.petshop.backend.entity.vendor.VendorPayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorPayoutRepository extends JpaRepository<VendorPayout, Long> {

    List<VendorPayout> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    Page<VendorPayout> findByStatus(PayoutStatus status, Pageable pageable);
}
