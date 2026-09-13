package com.petshop.backend.repository.vendor;

import com.petshop.backend.entity.vendor.VendorBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorBankDetailsRepository extends JpaRepository<VendorBankDetails, Long> {
    Optional<VendorBankDetails> findByVendorId(Long vendorId);
}