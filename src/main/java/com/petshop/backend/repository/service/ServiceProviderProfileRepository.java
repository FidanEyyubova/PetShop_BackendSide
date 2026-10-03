package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.ServiceProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderProfileRepository extends JpaRepository<ServiceProviderProfile, Long>,
        JpaSpecificationExecutor<ServiceProviderProfile> {

    Optional<ServiceProviderProfile> findByProviderId(Long providerId);

    @Query("SELECT p.serviceType, COUNT(p) FROM ServiceProviderProfile p, User u WHERE u.id = p.providerId " +
           "AND u.deletedAt IS NULL AND u.status = com.petshop.backend.common.UserStatus.ACTIVE " +
           "AND p.serviceType IS NOT NULL GROUP BY p.serviceType")
    List<Object[]> countActiveByServiceType();

    @Modifying
    @Query("UPDATE ServiceProviderProfile p SET p.rating = :rating, p.reviewCount = :count WHERE p.providerId = :providerId")
    void updateRating(@Param("providerId") Long providerId, @Param("rating") BigDecimal rating, @Param("count") int count);

    @Modifying
    @Query("UPDATE ServiceProviderProfile p SET p.pageViews = COALESCE(p.pageViews, 0) + 1 WHERE p.providerId = :providerId")
    int incrementPageViews(@Param("providerId") Long providerId);

    @Modifying
    @Query("UPDATE ServiceProviderProfile p SET p.contactClicks = COALESCE(p.contactClicks, 0) + 1 WHERE p.providerId = :providerId")
    int incrementContactClicks(@Param("providerId") Long providerId);

    @Modifying
    @Query("UPDATE ServiceProviderProfile p SET p.wishlistAdds = COALESCE(p.wishlistAdds, 0) + 1 WHERE p.providerId = :providerId")
    int incrementWishlistAdds(@Param("providerId") Long providerId);
}
